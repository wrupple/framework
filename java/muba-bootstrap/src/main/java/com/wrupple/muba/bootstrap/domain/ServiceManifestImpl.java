package com.wrupple.muba.bootstrap.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;

public class ServiceManifestImpl implements ServiceManifest {
	private final Map<String, LinkedHashMap<String, ServiceManifest>> childServices;
	private String serviceName,serviceVersion,serviceId;
	private ContractDescriptor contractDescriptor;
	private List<ServiceManifest> childServiceManifests;
	private String[] vocabulary,childServiceIds;
	private final Command contextParsingCommand;
	private final Command contextProcessingCommand;
	
	
	public ServiceManifestImpl(String serviceName, String serviceVersion,
			ContractDescriptor contractDescriptor, List<ServiceManifest> childServiceManifests,
			String[] vocabulary,Command contextParsingCommand,Command contextProcessingCommand) {
		super();
		this.contextProcessingCommand=contextProcessingCommand;
		this.contextParsingCommand=contextParsingCommand;
		this.serviceName = serviceName;
		this.serviceVersion = serviceVersion;
		this.contractDescriptor = contractDescriptor;
		this.childServiceManifests = childServiceManifests;
		this.vocabulary = vocabulary;
		setServiceId(getServiceName() + "-" + getServiceVersion());
		if (childServiceManifests == null) {
			childServices = Collections.EMPTY_MAP;
		} else {
			childServices = new HashMap<>(childServiceManifests.size());
			LinkedHashMap<String, ServiceManifest> versions;
			for (ServiceManifest child : childServiceManifests) {
				serviceName = child.getServiceName();
				versions = childServices.get(serviceName);
				if (versions == null) {
					versions = new LinkedHashMap<>(2, 1);
					childServices.put(serviceName, versions);
				}
				versions.put(child.getServiceVersion(), child);
			}
			Set<String> childPathSet = childServices.keySet();
			String[] childServicePaths = new String[childPathSet.size()];
			int i = 0;
			for (String childPath : childPathSet) {
				childServicePaths[i] = childPath;
				i++;
			}

			setChildServicePaths(childServicePaths);
		}
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceVersion() {
		return serviceVersion;
	}
	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}
	@Override
	public final ContractDescriptor getContractDescriptor() {
		return contractDescriptor;
	}
	public void setContractDescriptor(ContractDescriptor contractDescriptor) {
		this.contractDescriptor = contractDescriptor;
	}
	public List<ServiceManifest> getChildServiceManifests() {
		return childServiceManifests;
	}
	public void setChildServiceManifests(List<ServiceManifest> childServiceManifests) {
		this.childServiceManifests = (List)childServiceManifests;
	}
	public String[] getGrammar() {
		return vocabulary;
	}
	public void setVocabulary(String[] urlPathParameters) {
		this.vocabulary = urlPathParameters;
	}
	public String[] getChildServiceIds() {
		return childServiceIds;
	}
	public void setChildServicePaths(String[] childServicePaths) {
		this.childServiceIds = childServicePaths;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public Map<String, ServiceManifest> getVersions(String service) {
		return this.childServices.get(service);
	}
	@Override
	public Command getContextParsingCommand() {
		return contextParsingCommand;
	}
	@Override
	public Command getContextProcessingCommand() {
		return contextProcessingCommand;
	}


}
