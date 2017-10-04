package com.wrupple.muba.catalogs.domain;

import java.util.List;
import java.util.Map;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.CatalogEntryImpl;

public class CatalogActionTriggerImpl extends CatalogEntryImpl implements CatalogActionTrigger, CatalogEntry {

	private static final long serialVersionUID = 1609115127939733426L;

	public CatalogActionTriggerImpl(){
	}
	
	public CatalogActionTriggerImpl(int action,String handler, boolean before, String targetCatalogId, List<String> properties,String seed) {
		super();
		this.action=action;
		this.seed=seed;
		this.handler = handler;
		this.advise = before;
		this.catalog = targetCatalogId;
		this.properties = properties;
	}

	private Long stakeHolder;
	private String description,catalog,entry,seed,expression,systemEvent;
	private String handler;
	private int action;
	private boolean advise,runAsStakeHolder,failSilence,stopOnFail;
	
	
	private List<String> properties;
	
	public String getSentence() {
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
	public boolean isAdvice() {
		return advise;
	}
	public void setAdvice(boolean before) {
		this.advise = before;
	}
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String targetCatalogId) {
		this.catalog = targetCatalogId;
	}
	public List<String> getProperties() {
		return properties;
	}
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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


	public boolean isFailSilence() {
		return failSilence;
	}

	public void setFailSilence(Boolean rollbackOnFail) {
		if(rollbackOnFail==null){
			this.failSilence = false;
		}else{
			this.failSilence = rollbackOnFail;
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


	
	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	@Override
	public Map<String, String> getParsedProperties(List<String> rawProperties, Map context) {
		return (Map<String, String>) context.get(getCatalogType()+getId());
	}

	@Override
	public void setParsedProperties(Map<String, String> parsed, List<String> rawProperties, Map context) {
		context.put(getCatalogType()+getId(), parsed);		
	}

	@Override
	public void setEntry(Object id) {
		setEntry((String)id);
	}

    @Override
    public Object getEntryValue() {

        return null;
    }

    @Override
	public String toString() {
		return getName()==null?getHandler():getName();
	}

	@Override
	public String getCatalogType() {
		return CatalogActionTrigger.CATALOG;
	}

	
	
	

}
