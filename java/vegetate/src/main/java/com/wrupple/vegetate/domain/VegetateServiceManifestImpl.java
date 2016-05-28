package com.wrupple.vegetate.domain;

import java.util.List;

public class VegetateServiceManifestImpl implements VegetateServiceManifest {

	private String serviceName,serviceVersion,serviceId;
	private CatalogDescriptor contractDescriptor;
	private List<VegetateServiceManifestImpl> childServiceManifests;
	private String[] urlPathParameters,childServicePaths;
	public VegetateServiceManifestImpl() {
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
	public CatalogDescriptor getContractDescriptor() {
		return contractDescriptor;
	}
	public void setContractDescriptor(CatalogDescriptor contractDescriptor) {
		this.contractDescriptor = contractDescriptor;
	}
	public List<VegetateServiceManifestImpl> getChildServiceManifests() {
		return childServiceManifests;
	}
	public void setChildServiceManifests(List<VegetateServiceManifestImpl> childServiceManifests) {
		this.childServiceManifests = childServiceManifests;
	}
	public String[] getUrlPathParameters() {
		return urlPathParameters;
	}
	public void setUrlPathParameters(String[] urlPathParameters) {
		this.urlPathParameters = urlPathParameters;
	}
	public String[] getChildServicePaths() {
		return childServicePaths;
	}
	public void setChildServicePaths(String[] childServicePaths) {
		this.childServicePaths = childServicePaths;
	}
	@Override
	public Object createExcecutionContext(Object rc, String[] tokenValues, String serializedContext) throws Exception {
		return null;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}


}
