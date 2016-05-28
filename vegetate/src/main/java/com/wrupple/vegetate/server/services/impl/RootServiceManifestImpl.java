package com.wrupple.vegetate.server.services.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.server.services.RequestScopedContext;
import com.wrupple.vegetate.server.services.RootServiceManifest;

public class RootServiceManifestImpl implements RootServiceManifest {

	private static final Logger log = LoggerFactory.getLogger(RootServiceManifestImpl.class);
	
	// {service,{version,()}
	private final Map<String, LinkedHashMap<String, VegetateServiceManifest>> childServices;
	private final String[] childServicePaths;
	private final CatalogDescriptor contract;
	private final List<? extends VegetateServiceManifest> childServiceManifests;
	private String serviceId;
	private RootServiceManifest optimized;

	private static final String[] PATH = { "service", "version" };

	public RootServiceManifestImpl(@Named(NAME) CatalogDescriptor contract, @Named("vegetate.services") List chilcdren,
			@Named("vegetate.seoAwareService") RootServiceManifest optimized) {
		super();
		this.contract = contract;
		this.optimized = optimized;
		childServiceManifests = chilcdren;
		childServices = new HashMap<>(childServiceManifests.size());
		LinkedHashMap<String, VegetateServiceManifest> versions;
		String serviceName;
		for (VegetateServiceManifest child : childServiceManifests) {
			serviceName = child.getServiceName();
			versions = childServices.get(serviceName);
			if (versions == null) {
				versions = new LinkedHashMap<>(2,1);
				childServices.put(serviceName, versions);
			}
			versions.put(child.getServiceVersion(), child);
		}
		Set<String> childPathSet = childServices.keySet();
		this.childServicePaths = new String[childPathSet.size()];
		int i = 0;
		for (String childPath : childPathSet) {
			childServicePaths[i] = childPath;
			i++;
		}

	}

	@Override
	public String getServiceName() {
		return NAME;
	}

	@Override
	public String getServiceVersion() {
		return "1.0";
	}

	@Override
	public String[] getUrlPathParameters() {
		return PATH;
	}

	@Override
	public String[] getChildServicePaths() {
		return childServicePaths;
	}

	@Override
	public CatalogDescriptor getContractDescriptor() {
		return contract;
	}

	@Override
	public List<? extends VegetateServiceManifest> getChildServiceManifests() {
		return childServiceManifests;
	}

	@Override
	public Context createExcecutionContext(Object rc, String[] tokenValues, String serializedContext) throws Exception {
		RequestScopedContext requestContext = (RequestScopedContext) rc;
		
		VegetateServiceManifest manifest = getChildServiceManifest(requestContext, tokenValues);

		Context context = (Context) manifest.createExcecutionContext(requestContext, tokenValues, serializedContext);

		context.put(getUrlPathParameters()[0], manifest.getServiceId());

		return context;
	}

	@Override
	public VegetateServiceManifest getChildServiceManifest(String service) {
		LinkedHashMap<String, VegetateServiceManifest> versions = childServices.get(service);
		if (versions == null) {
			return optimized;
		} else {
			VegetateServiceManifest manifest = versions.values().iterator().next();

			return manifest;
		}

	}

	@Override
	public VegetateServiceManifest getChildServiceManifest(RequestScopedContext requestContext, String[] tokenValues) {
		String service, version;
		LinkedHashMap<String, VegetateServiceManifest> versions;
		VegetateServiceManifest manifest;
		int firstTokenIndex = requestContext.getNextPathToken();
		// 0 service
		if (firstTokenIndex < tokenValues.length) {
			service = tokenValues[firstTokenIndex];
			
			versions = childServices.get(service);
			if (versions == null) {
				log.warn("service manifest not found, delegating to url optimized service");
				return optimized.getChildServiceManifest(requestContext, tokenValues);
			}else{
				log.info("service invoked :{}",service);
				firstTokenIndex++;
			}
			

		} else {
			throw new IllegalArgumentException("no service defined by request");
		}
		// 1 version
		if (firstTokenIndex < tokenValues.length) {
			version = tokenValues[firstTokenIndex];
			manifest = versions.get(version);
			if (manifest == null) {
				log.warn("version not found. Falling back to default version.");
				manifest = versions.values().iterator().next();
			} else {
				log.info("service version :{}",version);
				firstTokenIndex++;
			}
			
		} else {
			log.info("using default version of service");
			manifest = versions.values().iterator().next();
		}
		requestContext.setNextPathToken(firstTokenIndex);
		return manifest;
	}

	@Override
	public String getUrl(VegetateServiceManifest manifest) {
		return manifest.getServiceName();
	}

	
	
	@Override
	public String getServiceId() {
		if(serviceId==null){
			serviceId= getServiceName()+"-"+getServiceVersion();
		}
		return serviceId;
	}

}
