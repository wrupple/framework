package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogEntry;

public class CatalogActionTriggerImpl implements CatalogActionTrigger, CatalogEntry {

	private static final long serialVersionUID = 1609115127939733426L;

	public CatalogActionTriggerImpl(){
	}
	
	public CatalogActionTriggerImpl(int action,String handler, boolean before, String targetCatalogId, List<String> properties,String seed) {
		super();
		this.action=action;
		this.seed=seed;
		this.handler = handler;
		this.before = before;
		this.catalogId = targetCatalogId;
		this.properties = properties;
	}
	private Long id,domain,stakeHolder;
	private String name,description,catalogId,catalogEntryId,seed,expression,systemEvent;
	private String handler;
	private int action;
	private boolean before,runAsStakeHolder,rollbackOnFail,stopOnFail;
	
	private List<String> properties;
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = seed;
	}
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public boolean isBefore() {
		return before;
	}
	public void setBefore(boolean before) {
		this.before = before;
	}
	public String getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(String targetCatalogId) {
		this.catalogId = targetCatalogId;
	}
	public List<String> getProperties() {
		return properties;
	}
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String getCatalog() {
		return CatalogActionTrigger.CATALOG;
	}
	@Override
	public String getImage() {
		return null;
	}
	@Override
	public boolean isAnonymouslyVisible() {
		return false;
	}
	@Override
	public void setAnonymouslyVisible(boolean p) {
		
	}
	@Override
	public void setIdAsString(String id) {
		setId(Long.parseLong(id));
	}

	@Override
	public String getIdAsString() {
		return String.valueOf(getId());
	}

	public boolean isRunAsStakeHolder() {
		return runAsStakeHolder;
	}

	public void setRunAsStakeHolder(Boolean runAsStakeHolder) {
		this.runAsStakeHolder = runAsStakeHolder==null?false:runAsStakeHolder;
	}

	public Long getStakeHolder() {
		if(stakeHolder==null){
			return PUBLIC_ID;
		}
		return stakeHolder;
	}

	public void setStakeHolder(long stakeHolder) {
		this.stakeHolder = stakeHolder;
	}


	public boolean isRollbackOnFail() {
		return rollbackOnFail;
	}

	public void setRollbackOnFail(Boolean rollbackOnFail) {
		if(rollbackOnFail==null){
			this.rollbackOnFail = false;
		}else{
			this.rollbackOnFail = rollbackOnFail;
		}
	
	}

	public boolean isStopOnFail() {
		return stopOnFail;
	}

	public void setStopOnFail(Boolean stopOnFail) {
		this.stopOnFail = stopOnFail==null?false:stopOnFail;
	}


	@Override
	public String getSystemEvent() {
		return systemEvent;
	}

	public void setSystemEvent(String systemEvent) {
		this.systemEvent = systemEvent;
	}

	@Override
	public String getCatalogEntryId() {
		return catalogEntryId;
	}

	@Override
	public void setCatalogEntryId(String catalogEntryId) {
		this.catalogEntryId=catalogEntryId;
	}
	
	
	

}
