package com.wrupple.muba.bootstrap.server.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.domain.Bootstrap;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ContractDescriptorImpl;
import com.wrupple.muba.bootstrap.domain.ParentServiceManifest;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.ServiceManifestImpl;
import com.wrupple.muba.bootstrap.server.chain.command.ServiceInvocationCommand;

@Singleton
public class BootstrapImpl extends ServiceManifestImpl implements Bootstrap {

	private static String[] TOKENS = new String[] { "service", "version" };
	// Names.named("bootstrap.seoAwareService")
	private ParentServiceManifest fallbackService;

	// {service,{version,()}

	@Inject
	public BootstrapImpl(List<ServiceManifest> chilcdren, ServiceInvocationCommand invoke) {
		super(NAME, "1.0", new ContractDescriptorImpl(Arrays.asList(TOKENS), CatalogEntry.class), chilcdren, TOKENS,
				null, invoke);
	}

	@Override
	public ParentServiceManifest getFallbackService() {
		return fallbackService;
	}

	public void setFallbackService(ParentServiceManifest fallbackService) {
		this.fallbackService = fallbackService;
	}

}
