package com.wrupple.muba.event.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceManifestImpl extends CatalogEntryImpl implements ServiceManifest {
	private static final long serialVersionUID = -2346526516336493001L;
	private String distinguishedName, versionDistinguishedName,outputCatalog;
	private Long stakeHolder;
	private ContractDescriptor catalogValue;
	private List<String> grammar,properties;
	private List<Long> children;
	private List<ServiceManifest> childrenValues;
	private List<String> childrenPaths;
	private Long parent;
    private ServiceManifest parentValue;

    public ServiceManifestImpl() {
		super();
	}

	public ServiceManifestImpl(String distinguishedName, ContractDescriptor catalogValue,
			List<String> grammar) {
		super();
		this.distinguishedName = distinguishedName;
		this.catalogValue = catalogValue;
		this.grammar = grammar;
	}

	public ServiceManifestImpl(String distinguishedName, String versionDistinguishedName,
							   ContractDescriptor catalogValue, List<String> grammar) {
		this(distinguishedName, catalogValue, grammar);
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
            children = new ArrayList<>(childrenValues.size());
			for(ServiceManifest s : childrenValues){
                s.setParentValue(this);
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

	public void setVersionDistinguishedName(String serviceVersion) {
		this.versionDistinguishedName = serviceVersion;
	}

	@Override
	public final ContractDescriptor getCatalogValue() {
		return catalogValue;
	}

    @Override
    public void setParentValue(ServiceManifest serviceManifest) {
        this.parentValue=serviceManifest;
    }

    public void setCatalogValue(ContractDescriptor contractDescriptor) {
		this.catalogValue = contractDescriptor;
	}

	public void setGrammar(List<String> grammar) {
		this.grammar = grammar;
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

	@Override
	public String getCatalog() {
		return getCatalogValue().getDistinguishedName();
	}

	@Override
	public void setCatalog(String catalog) {
		throw new IllegalStateException();
	}

    @Override
    public ServiceManifest getParentValue() {
        return parentValue;
    }


	@Override
	public ServiceManifest getRootAncestor() {
		return CatalogEntryImpl.getRootAncestor(this);
	}

	@Override
	public String getOutputCatalog() {
		return outputCatalog;
	}

	public void setOutputCatalog(String outputCatalog) {
		this.outputCatalog = outputCatalog;
	}

	@Override
	public Long getStakeHolder() {
		return stakeHolder;
	}

	@Override
	public void setStakeHolder(Long stakeHolder) {
		this.stakeHolder = stakeHolder;
	}

	@Override
	public List<String> getProperties() {
		return properties;
	}

	@Override
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}


    @Override
    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }
}
