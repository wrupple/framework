package com.wrupple.muba.bootstrap.server.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.Bootstrap;
import com.wrupple.muba.bootstrap.domain.ContractDescriptorImpl;
import com.wrupple.muba.bootstrap.domain.ParentServiceManifest;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.ServiceManifestImpl;
import com.wrupple.muba.bootstrap.server.chain.command.ServiceInvocationCommand;

@Singleton
public class BootstrapImpl extends ServiceManifestImpl implements Bootstrap {

	

	private static String[] TOKENS=new String[] { "service", "version" };
	
	private final ParentServiceManifest optimized;
	

	
	// {service,{version,()}

	@Inject
	public BootstrapImpl(  List<ServiceManifest> chilcdren,
			@Named("bootstrap.seoAwareService") ParentServiceManifest optimized,  ServiceInvocationCommand invoke) {
		super(NAME,"1.0", new ContractDescriptorImpl(Arrays.asList(TOKENS),
				Context.class.getCanonicalName()),chilcdren,TOKENS, null, invoke);
		this.optimized = optimized;
		invoke.setRootService(this);
	}
	


	@Override
	public ParentServiceManifest getFallbackService() {
		return optimized;
	}




}
