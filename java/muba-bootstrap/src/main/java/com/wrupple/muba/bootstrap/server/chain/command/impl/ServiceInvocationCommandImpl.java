package com.wrupple.muba.bootstrap.server.chain.command.impl;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.Bootstrap;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.KnownException;
import com.wrupple.muba.bootstrap.domain.KnownExceptionImpl;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.server.chain.command.ServiceInvocationCommand;

@Singleton
public class ServiceInvocationCommandImpl implements ServiceInvocationCommand {
	private static final Logger log = LoggerFactory.getLogger(ServiceInvocationCommandImpl.class);
	@Inject
	private Bootstrap rootService;

	@Inject
	public ServiceInvocationCommandImpl() {
		super();
	}

	@Override
	public boolean execute(Context context) throws Exception {
		ExcecutionContext requestContext = (ExcecutionContext) context;
		log.trace("get service manifest {} {}", "arr:", requestContext.getSentence());
		String service = requestContext.next();
		ServiceManifest manifest = getChildServiceManifest(service, requestContext);
		requestContext.setServiceManifest(manifest);
		manifest.getContextParsingCommand().execute(requestContext);
		Context transactionContext = requestContext.getServiceContext();
		log.trace("EXCECUTING SERVICE {}", manifest.getServiceId());
		

		if (requestContext.getSession().hasPermissionsToProcessContext(transactionContext,
				requestContext.getServiceManifest())) {

			log.trace("excecution permission GRANTED on {}, transaction will begin ",
					requestContext.getIdAsString());
			// use ServiceInvocationThread for multithreading
			requestContext.getServiceManifest().getContextProcessingCommand().execute(transactionContext);

		} else {
			throw new KnownExceptionImpl(" Context Processing Denied ", KnownException.DENIED, null);
		}
		
		return CONTINUE_PROCESSING;
	}

	private ServiceManifest getChildServiceManifest(String service, ExcecutionContext requestContext) {
		Map<String, ServiceManifest> versions = rootService.getVersions(service);
		if (versions == null) {
			log.warn("unknown service {}, attempting fallback", service);
			if (rootService.getFallbackService() == null) {
				throw new IllegalArgumentException(service);
			}
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
					log.warn("Service version \"{}\" not found. Falling back to default version.",version);
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

	@Override 
	public void setRootService(Bootstrap rootService) {
		this.rootService = rootService;

	}

}
