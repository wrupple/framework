package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.ParentServiceManifest;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.server.chain.command.EventDispatcher;
import com.wrupple.muba.event.server.chain.command.RequestInterpret;
import com.wrupple.muba.event.server.service.ValidationGroupProvider;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class EventDispatcherImpl implements EventDispatcher {
	private static final Logger log = LoggerFactory.getLogger(EventDispatcherImpl.class);

	private final Validator validator;
	private final Class<?>[] groups;

	/**
	 * set context writing precedence of the value of a property in favor of
	 * what's in the sentence, over the contract
	 */
	protected boolean sentenceOverContract;

	@Inject
	public EventDispatcherImpl(Validator validator, ValidationGroupProvider a) {
		sentenceOverContract = true;
		this.validator = validator;
		this.groups = a == null ? null : a.get();

	}

	@Override
	public boolean execute(Context context) throws Exception {
        log.debug("<{}>",this.getClass().getSimpleName());


        RuntimeContext requestContext = (RuntimeContext) context;
		Set<ConstraintViolation<?>> violations = null;
		Object contract = requestContext.getServiceContract();
		String key, value;
		
		
		if (requestContext.hasNext()) {
            if (log.isDebugEnabled()) {
                log.info("[ SENTENCE] {}", requestContext.getSentence().subList(requestContext.nextIndex(),
                        requestContext.getSentence().size()));
			}
			if (validator == null) {

			} else {
				violations = (Set) validator.validate(requestContext, groups);
				requestContext.setConstraintViolations(violations);
				if (!(violations == null || violations.isEmpty())) {
					log.error("excecution request encountered constraint violations ");
					//if (log.isWarnEnabled()) {
						for (ConstraintViolation<?> v : violations) {
							log.warn("{}", v.getMessage());
						}
					//}
					throw new IllegalArgumentException();
				}
			}
		} else {
			log.error("Excecution interpret iterator is at the end of the sentence.");
		}

		String service = requestContext.next();
		ServiceManifest manifest = getChildServiceManifest(service, requestContext);
		requestContext.setServiceManifest(manifest);
		log.debug("VALIDATING CONTRACT {}:", manifest.getServiceId(),contract);

        if (contract == null) {
            if (manifest.getCatalogValue().getClazz() != null) {
                contract = manifest.getCatalogValue().getClazz().newInstance();
                requestContext.setServiceContract(contract);
            }
        }

        if (contract != null) {
            List<String> tokens = requestContext.getServiceManifest().getGrammar();
            log.trace("Incomming contract {}", contract);
            for (int i = 0; i < tokens.size(); i++) {
                key = tokens.get(i);

                if (PropertyUtils.isWriteable(contract, key)
                        && (PropertyUtils.getProperty(contract, key) == null || sentenceOverContract)) {

                    if (requestContext.hasNext()) {
                        value = requestContext.next();
                    } else {
                        value = null;
                    }
                    if (value != null) {
						log.debug("service grammar defined contract key {}={}", key, value);

                        BeanUtilsBean2.getInstance().setProperty(contract, key, value);
                    }
                } else {
                    log.error("token \"{}\" from service grammar was not recognized by contract and was ignored ", key);
                    log.trace("stop analizing sentence");
                    requestContext.setNextWordIndex(requestContext.previousIndex());
                    break;
                }

            }

            if (validator == null) {
            } else {
                violations = (Set) validator.validate(contract, groups);
                requestContext.setConstraintViolations(violations);
                if (!(violations == null || violations.isEmpty())) {
                    log.error("contract violates restrictions");
					if (log.isInfoEnabled()) {
						for (ConstraintViolation<?> v : violations) {
							log.info(v.getLeafBean().toString());
							log.info("\t{} : {}", v.getPropertyPath(), v.getMessage());
						}
                    }
                    log.debug("</{}>", this.getClass().getSimpleName());
                    throw new IllegalArgumentException("Contract violates constrains");
                }
            }
        }

        if (requestContext.getSession().hasPermissionsToProcessContext(requestContext,
				requestContext.getServiceManifest())) {

			log.trace("excecution permission GRANTED for request {}, transaction will begin on {}",
					requestContext.getId(), manifest.getServiceId());
			if (CONTINUE_PROCESSING == incorporateContract(requestContext)) {
				Command serviceHandler = requestContext.getEventBus().getIntentInterpret().getDictionaryFactory()
						.getCatalog(ParentServiceManifest.NAME)
						.getCommand(requestContext.getServiceManifest().getServiceId());
				log.debug("delegating to service handler {}", serviceHandler);
				boolean r = serviceHandler.execute(requestContext.getServiceContext());
				log.debug("</{}>",this.getClass().getSimpleName());
				return r;
			} else {
				log.error("could not understand contract");
				log.debug("</{}>",this.getClass().getSimpleName());
				return PROCESSING_COMPLETE;
			}

		} else {
			log.error("Permission to process request denied");
			log.debug("</{}>",this.getClass().getSimpleName());
			return PROCESSING_COMPLETE;
		}
	}

	private boolean incorporateContract(RuntimeContext requestContext) throws Exception {

		List<String> tokens = requestContext.getServiceManifest().getGrammar();
		RequestInterpret explicitInterpret = requestContext.getEventBus().getIntentInterpret().getExplicitIntentInterpret(requestContext);
		Object contract = requestContext.getServiceContext();
		Context context = materializeContext(requestContext,
				/* we know it's this class:registration is private */ explicitInterpret);
		String key, value;
		if ( explicitInterpret == null) {
			log.debug("no explicit contract interpret");
			if (tokens != null && requestContext.hasNext()) {
				log.trace("read-copy known grammar properties into service context");

				for (int i = 0; i < tokens.size(); i++) {
					key = tokens.get(i);
					if (requestContext.hasNext()) {
						value = requestContext.next();
						log.trace("[service parameter] {}={}", key, value);
						context.put(key, value);
					} else {
						value = null;
					}
				}
			}

			if (contract != null && contract != context) {
				ContractDescriptor descriptor = requestContext.getServiceManifest().getCatalogValue();
				if (descriptor != null) {
					Collection<String> fields = descriptor.getFieldsIds();
					Object v;
					log.trace("read-copy contract  properties into service context");

					for (String field : fields) {
						v = PropertyUtils.getProperty(contract, field);
						if (v == null) {
							log.trace("ignoring empty contract field {}", field);
						} else {
							log.trace("[service parameter] {}={}", field, v);

							context.put(field, v);
						}

					}
				}
			}
			
			

			
		} else {
			log.debug("delegating to explicit contract interpret {}", explicitInterpret);
			return explicitInterpret.execute(requestContext);

		}

		return CONTINUE_PROCESSING;
	}

	private Context materializeContext(RuntimeContext requestContext, RequestInterpret explicitInterpret) throws InvocationTargetException, IllegalAccessException {
		Context context = requestContext.getServiceContext();

		if (context == null) {
			if (explicitInterpret == null) {
				log.warn("Using request context space to excecute service");
				context = requestContext;
			} else {
				context = explicitInterpret.materializeBlankContext(requestContext);
			}

		}
		requestContext.setServiceContext(context);
		return context;
	}

	private ServiceManifest getChildServiceManifest(String service, RuntimeContext requestContext) {
		ParentServiceManifest rootService = requestContext.getEventBus().getIntentInterpret().getRootService();
		if (rootService == null) {
			throw new IllegalStateException("No root service has been configured");
		}
		Map<String, ServiceManifest> versions = rootService.getVersions(service);
		if (versions == null) {
			log.warn("unknown service \"{}\", attempting fallback", service);
			if (rootService.getFallbackService() == null) {
				log.error("unknown service \"{}\" and no fallbaack", service);
				throw new IllegalArgumentException("No such service :" + service);
			}
			log.info("serving \"{}\" with fallback service handler {}", service, rootService.getFallbackService());
			requestContext.setNextWordIndex(requestContext.nextIndex() - 1);
			return rootService.getFallbackService();
		} else {
			ServiceManifest manifest;

			log.trace("service invoked :{}", service);

			// 1 version
			if (requestContext.hasNext()) {
				String version = requestContext.next();
				manifest = versions.get(version);
				if (manifest == null) {
					log.warn("Service version \"{}\" not found. Falling back to default version.", version);
					requestContext.setNextWordIndex(requestContext.nextIndex() - 1);
					manifest = versions.values().iterator().next();
				} else {
					log.trace("service version :{}", version);
				}

			} else {
				log.trace("using default version of service");
				manifest = versions.values().iterator().next();
			}
			return manifest;
		}
	}

}
