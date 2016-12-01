package com.wrupple.muba.bootstrap.server.chain.command.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.Bootstrap;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.server.chain.command.ServiceInvocationCommand;
import com.wrupple.muba.bootstrap.server.service.ValidationGroupProvider;

@Singleton
public class ServiceInvocationCommandImpl implements ServiceInvocationCommand {
	private static final Logger log = LoggerFactory.getLogger(ServiceInvocationCommandImpl.class);
	
	private final Validator validator;
	private final Class<?>[] groups;
	
	@Inject
	public ServiceInvocationCommandImpl(Validator validator,  ValidationGroupProvider a) {
		super();
		this.validator = validator;
		this.groups = a.get();
	}

	@Override
	public boolean execute(Context context) throws Exception {
		ExcecutionContext requestContext = (ExcecutionContext) context;
		if (requestContext.hasNext()) {
			if (log.isTraceEnabled()) {
				log.trace("[RUN SENTENCE] {}", (Object) Arrays.copyOfRange(requestContext.getSentence(),
						requestContext.nextIndex(), requestContext.getSentence().length));
			}
			Set<ConstraintViolation<?>> violations;
			if (validator == null) {

			} else {
				violations = (Set) validator.validate(requestContext, groups);
				requestContext.setConstraintViolations(violations);
				if (!(violations == null || violations.isEmpty())) {
					return PROCESSING_COMPLETE;
				}
			}
		} else {
			log.error("Excecution interpret iterator is at the end of the sentence.");
		}

		
		String service = requestContext.next();
		ServiceManifest manifest = getChildServiceManifest(service, requestContext);
		requestContext.setServiceManifest(manifest);
		log.trace("PARSING SERVICE REQUEST {}", manifest.getServiceId());
		manifest.getContextParsingCommand().execute(requestContext);

		if(requestContext.getConstraintViolations()==null || requestContext.getConstraintViolations().isEmpty()){

			Context transactionContext = requestContext.getServiceContext();

			if (requestContext.getSession().hasPermissionsToProcessContext(transactionContext,
					requestContext.getServiceManifest())) {

				log.trace("excecution permission GRANTED for request {}, transaction will begin on {}",
						requestContext.getId(), manifest.getServiceId());
				// use ServiceInvocationThread for multithreading
				return requestContext.getServiceManifest().getContextProcessingCommand().execute(transactionContext);
			} else {
				log.error("Permission to process request denied");
				return PROCESSING_COMPLETE;
			}
		}else{
			log.warn("Request violates constraints {}",requestContext.getConstraintViolations());
			return PROCESSING_COMPLETE;
		}

	}

	private ServiceManifest getChildServiceManifest(String service, ExcecutionContext requestContext) {
		Bootstrap rootService = requestContext.getApplication().getRootService();
		if(rootService==null){
			throw new IllegalStateException("No root service has been configured");
		}
		Map<String, ServiceManifest> versions = rootService.getVersions(service);
		if (versions == null) {
			log.warn("unknown service \"{}\", attempting fallback", service);
			if (rootService.getFallbackService() == null) {
				log.error("unknown service \"{}\" and no fallbaack", service);
				throw new IllegalArgumentException("No such service :"+service);
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
