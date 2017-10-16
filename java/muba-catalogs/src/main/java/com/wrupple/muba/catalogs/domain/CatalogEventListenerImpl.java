package com.wrupple.muba.catalogs.domain;

import java.util.List;
import java.util.Map;

import com.wrupple.muba.event.domain.CatalogEntryImpl;
import com.wrupple.muba.event.domain.annotations.CatalogField;

public class CatalogEventListenerImpl extends CatalogEntryImpl implements CatalogEventListener {

	private static final long serialVersionUID = 1609115127939733426L;

	public CatalogEventListenerImpl(){
	}
	
	public CatalogEventListenerImpl(int action, String handler, boolean before, String targetCatalogId, List<String> properties, String seed) {
		super();
		this.action=action;
		this.seed=seed;
		setName(handler);
		this.advise = before;
		this.catalog = targetCatalogId;
		this.properties = properties;
	}

	private Long stakeHolder;
	@CatalogField(filterable = true)
	private String catalog;
	private String description,entry,seed,systemEvent;
    @CatalogField(filterable = true)
	private Integer action;
	private Boolean advise,runAsStakeHolder,failSilence,stopOnFail;
	
	
	private List<String> properties,sentence;


	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = seed;
	}

	public Integer getAction() {
		return action;
	}
	public void setAction(Integer action) {
		this.action = action;
	}
	public Boolean isAdvice() {
		return advise;
	}
	public void setAdvice(Boolean before) {
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
	public String getCatalogType() {
		return CatalogEventListener.CATALOG;
	}


	@Override
	public List<String> getSentence() {
		return sentence;
	}

	public void setSentence(List<String> sentence) {
		this.sentence = sentence;
	}
}
