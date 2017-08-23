package com.wrupple.muba.bootstrap.server.chain.command.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.ContractDescriptor;
import com.wrupple.muba.bootstrap.domain.RootServiceManifest;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.server.chain.command.EventDispatcher;
import com.wrupple.muba.bootstrap.server.chain.command.RequestInterpret;
import com.wrupple.muba.bootstrap.server.service.ValidationGroupProvider;

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
		RuntimeContext requestContext = (RuntimeContext) context;
		Set<ConstraintViolation<?>> violations = null;
		Object contract = requestContext.getServiceContract();
		String key, value;
		
		
		if (requestContext.hasNext()) {
			if (log.isTraceEnabled()) {
				log.info("[ SENTENCE] {}", (Object) requestContext.getSentence().subList(requestContext.nextIndex(),
						requestContext.getSentence().size()));
			}
			if (validator == null) {

			} else {
				violations = (Set) validator.validate(requestContext, groups);
				requestContext.setConstraintViolations(violations);
				if (!(violations == null || violations.isEmpty())) {
					log.warn("excecution request encountered constraint violations ");
					if (log.isTraceEnabled()) {
						for (ConstraintViolation<?> v : violations) {
							log.trace("{}", v.getMessage());
						}
					}
					return PROCESSING_COMPLETE;
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

		} else {
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
					log.trace("service grammar defined contract key {}={}", key, value);
					PropertyUtils.setProperty(contract, key, value);
				} else {
					log.warn("token \"{}\" from service grammar was not recognized by contract and was ignored ");
				}

			}

			if (validator == null) {
			} else {
				violations = (Set) validator.validate(contract, groups);
				requestContext.setConstraintViolations(violations);
				if (!(violations == null || violations.isEmpty())) {
					log.error("contract violates restrictions");
					if (log.isDebugEnabled()) {
						for (ConstraintViolation<?> v : violations) {
							log.debug(v.getLeafBean().toString());
							log.debug("\t{} : {}",v.getPropertyPath(),v.getMessage());
						}
					}
					return PROCESSING_COMPLETE;
				}
			}
		}

		if (requestContext.getSession().hasPermissionsToProcessContext(requestContext,
				requestContext.getServiceManifest())) {

			log.trace("excecution permission GRANTED for request {}, transaction will begin on {}",
					requestContext.getId(), manifest.getServiceId());
			if (CONTINUE_PROCESSING == incorporateContract(requestContext)) {
				Command serviceHandler = requestContext.getApplication().getDictionaryFactory()
						.getCatalog(RootServiceManifest.NAME)
						.getCommand(requestContext.getServiceManifest().getServiceId());
				log.debug("delegating to service handler {}", serviceHandler);
				return serviceHandler.execute(requestContext.getServiceContext());
			} else {
				log.error("could not understand contract");
				return PROCESSING_COMPLETE;
			}

		} else {
			log.error("Permission to process request denied");
			return PROCESSING_COMPLETE;
		}
	}

	private boolean incorporateContract(RuntimeContext requestContext) throws Exception {

		List<String> tokens = requestContext.getServiceManifest().getGrammar();
		RequestInterpret explicitInterpret = requestContext.getApplication().getRequestInterpret(requestContext);
		Object contract = requestContext.getServiceContext();
		Context context = materializeContext(requestContext,
				/* we know it's this class:registration is private */ explicitInterpret);
		String key, value;
		if ( explicitInterpret == null) {
			log.debug("no explicit contract interpret");
			if (tokens != null) {
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
			
			if(contract != null){
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

	private Context materializeContext(RuntimeContext requestContext, RequestInterpret explicitInterpret) {
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
		RootServiceManifest rootService = requestContext.getApplication().getRootService();
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
