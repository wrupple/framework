package com.wrupple.muba.cms.server.domain;

import java.util.List;

import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.cms.domain.TaskToolbarDescriptor;

public class ProcessTaskDescriptorImpl implements ProcessTaskDescriptor {
	private static final long serialVersionUID = 7957074775049623796L;
	private Long id,domain;
	private boolean anonymouslyVisible;
	private String image,name,catalogId,vanityId,machineTaskCommandName,transactionType;
	private List<String> properties,urlTokens;
	private List<Long> toolbars,userActions;
	private List<? extends TaskToolbarDescriptor> toolbarsValues;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDomain() {
		return domain;
	}
	public void setDomain(Long domain) {
		this.domain = domain;
	}
	public boolean isAnonymouslyVisible() {
		return anonymouslyVisible;
	}
	public void setAnonymouslyVisible(boolean anonymouslyVisible) {
		this.anonymouslyVisible = anonymouslyVisible;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}
	public String getVanityId() {
		return vanityId;
	}
	public void setVanityId(String vanityId) {
		this.vanityId = vanityId;
	}
	public String getMachineTaskCommandName() {
		return machineTaskCommandName;
	}
	public void setMachineTaskCommandName(String machineTaskCommandName) {
		this.machineTaskCommandName = machineTaskCommandName;
	}
	public List<String> getProperties() {
		return properties;
	}
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}
	public List<String> getUrlTokens() {
		return urlTokens;
	}
	public void setUrlTokens(List<String> urlTokens) {
		this.urlTokens = urlTokens;
	}
	public List<Long> getToolbars() {
		return toolbars;
	}
	public void setToolbars(List<Long> toolbars) {
		this.toolbars = toolbars;
	}
	public List<Long> getUserActions() {
		return userActions;
	}
	public void setUserActions(List<Long> userActions) {
		this.userActions = userActions;
	}
	public List<? extends TaskToolbarDescriptor> getToolbarsValues() {
		return toolbarsValues;
	}
	public void setToolbarsValues(List<? extends TaskToolbarDescriptor> toolbarsValues) {
		this.toolbarsValues = toolbarsValues;
	}
	@Override
	public String getCatalog() {
		return CATALOG;
	}
	@Override
	public String getIdAsString() {
		return getId()==null? null : String.valueOf(getId());
	}
	@Override
	public void setIdAsString(String id) {
		setId(Long.parseLong(id));
	}
	@Override
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	

}
