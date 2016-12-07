package com.wrupple.muba.bootstrap.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceManifestImpl extends CatalogEntryImpl implements ServiceManifest {
	private static final long serialVersionUID = -2346526516336493001L;
	private String distinguishedName, versionDistinguishedName;
	private ContractDescriptor contractDescriptorValue;
	private List<String> grammar;
	private List<Long> children;
	private List<ServiceManifest> childrenValues;
	private List<String> childrenPaths;

	public ServiceManifestImpl() {
		super();
	}

	public ServiceManifestImpl(String distinguishedName, ContractDescriptor contractDescriptorValue,
			List<String> grammar) {
		super();
		this.distinguishedName = distinguishedName;
		this.contractDescriptorValue = contractDescriptorValue;
		this.grammar = grammar;
	}

	public ServiceManifestImpl(String distinguishedName, String versionDistinguishedName,
			ContractDescriptor contractDescriptorValue, List<String> grammar) {
		this(distinguishedName, contractDescriptorValue, grammar);
		this.versionDistinguishedName = versionDistinguishedName;
	}


	public List<Long> getChildren() {
		return children;
	}

	public void setChildren(List<Long> children) {
		this.children = children;
	}

	public List<ServiceManifest> getChildrenValues() {
		return childrenValues;
	}

	public void setChildrenValues(List<ServiceManifest> childrenValues) {
		this.childrenValues = childrenValues;
		List<String> childrenPaths;
		if(childrenValues==null){
			childrenPaths = null;
		}else{
			Set<String >childarenPaths = new HashSet<String>(childrenValues.size());
			for(ServiceManifest s : childrenValues){
				childarenPaths.add(s.getDistinguishedName());
			}
			childrenPaths = new ArrayList<>(childarenPaths);
		}
		setChildrenPaths(childrenPaths);
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String serviceName) {
		this.distinguishedName = serviceName;
	}

	public String getVersionDistinguishedName() {
		return versionDistinguishedName;
	}

	public void setServiceVersion(String serviceVersion) {
		this.versionDistinguishedName = serviceVersion;
	}

	@Override
	public final ContractDescriptor getContractDescriptorValue() {
		return contractDescriptorValue;
	}

	public void setContractDescriptor(ContractDescriptor contractDescriptor) {
		this.contractDescriptorValue = contractDescriptor;
	}

	@Override
	public List<String> getGrammar() {
		return grammar;
	}

	public String getServiceId() {
		if (getVersionDistinguishedName() == null) {
			return getDistinguishedName();
		}
		return getDistinguishedName() + "-" + getVersionDistinguishedName();
	}

	@Override
	public String getCatalogType() {
		return ServiceManifest.CATALOG;
	}

	@Override
	public List<String> getChildrenPaths() {
		return childrenPaths;
	}

	public void setChildrenPaths(List<String> childrenPaths) {
		this.childrenPaths = childrenPaths;
	}

}
