package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.annotations.CatalogField;

import java.util.List;

public class TriggerImpl extends CatalogActionConstraintImpl implements Trigger {

	private static final long serialVersionUID = 1609115127939733426L;

	@CatalogField(filterable = true)
	private Long action;

	private Long stakeHolder;

	private String systemEvent;

	private Boolean advice,runAsStakeHolder,failSilence,stopOnFail;


	public TriggerImpl(Long id){
		this();
		setId(id);
	}
	
	public TriggerImpl(Long id,int action, String handler, boolean before, String targetCatalogId, List<String> properties, String seed) {
		this();
		this.action=Long.valueOf(action);
		super.setSeed(seed);
		setName(handler);
		this.advice = before;
		super.setCatalog(targetCatalogId);
		super.setProperties(properties);
	}

	public TriggerImpl() {
		super();
	}

	public Long getAction() {
		return action;
	}
	public void setAction(Long action) {
		this.action = action;
	}


	public Boolean getAdvice() {
		return isAdvice();
	}
	public Boolean isAdvice() {
		return advice;
	}
	public void setAdvice(Boolean before) {
		this.advice = before;
	}
	public Boolean getRunAsStakeHolder() {
		return runAsStakeHolder==null?false:runAsStakeHolder;
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

	public void setStakeHolder(Object stakeHolder) {
		this.stakeHolder = (Long) stakeHolder;
	}

    public void setStakeHolder(Long stakeHolder) {
        this.stakeHolder = stakeHolder;
    }


	public Boolean getFailSilence() {
		return failSilence;
	}

	public void setFailSilence(Boolean rollbackOnFail) {
		if(rollbackOnFail==null){
			this.failSilence = false;
		}else{
			this.failSilence = rollbackOnFail;
		}

	}

	public Boolean getStopOnFail() {
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
    public String getCatalogType() {
        return Trigger.CATALOG;
    }

}
