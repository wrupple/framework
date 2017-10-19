package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.ParentServiceManifest;
import com.wrupple.muba.event.domain.ServiceManifest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ParentServiceManifestImpl extends ServiceManifestImpl implements ParentServiceManifest {
	private static final long serialVersionUID = -5309668537269581130L;

	private Map<String, LinkedHashMap<String, ServiceManifest>> childServices;

	private static List<String> TOKENS = Arrays.asList(new String[] { "service", "version" });
	// Names.named("event.seoAwareService")
	private ServiceManifest fallbackService;



	@Inject
	public ParentServiceManifestImpl() {
		super(NAME, "1.0",  new ContractDescriptorImpl(TOKENS, CatalogEntry.class),(List) Arrays.asList(TOKENS));
	
	}

	public ParentServiceManifestImpl(String distinguishedName, ContractDescriptor catalogValue, List<String> grammar) {
		super(distinguishedName, catalogValue, grammar);

	}

	public ParentServiceManifestImpl(String distinguishedName, String versionDistinguishedName, ContractDescriptor catalogValue, List<String> grammar) {
		super(distinguishedName, versionDistinguishedName, catalogValue, grammar);
	}

	@Override
	public ServiceManifest getFallbackService() {

		return fallbackService;
	}

	public void setFallbackService(ServiceManifest fallbackService) {
		this.fallbackService = fallbackService;
	}

	public Map<String, ServiceManifest> getVersions(String service) {
		if (getChildrenValues() == null) {
			childServices = Collections.EMPTY_MAP;
		} else {
			List<ServiceManifest> childServiceManifests = getChildrenValues();
			childServices = new HashMap<>(childServiceManifests.size());
			LinkedHashMap<String, ServiceManifest> versions;
			String serviceName;
			for (ServiceManifest child : childServiceManifests) {
				serviceName = child.getDistinguishedName();
				versions = childServices.get(serviceName);
				if (versions == null) {
					versions = new LinkedHashMap<>(2, 1);
					childServices.put(serviceName, versions);
				}
				versions.put(child.getVersionDistinguishedName(), child);
			}
			Set<String> childPathSet = childServices.keySet();
			String[] childServicePaths = new String[childPathSet.size()];
			int i = 0;
			for (String childPath : childPathSet) {
				childServicePaths[i] = childPath;
				i++;
			}

			setChildrenPaths(new ArrayList<String>(childPathSet));
		}
		return this.childServices.get(service);
	}

	@Override
	public void setFallBackService(ServiceManifest fallbackService) {
		this.fallbackService = fallbackService;
		register(fallbackService);
	}

	@Override
	public void register(ServiceManifest manifest) {
		List<ServiceManifest> vvv = getChildrenValues();
		if(vvv==null){
			vvv = new ArrayList<ServiceManifest>();
		}
		vvv.add(manifest);
		//TODO wildly inefficient
		setChildrenValues(vvv);
		manifest.setParentValue(this);
	}

}
