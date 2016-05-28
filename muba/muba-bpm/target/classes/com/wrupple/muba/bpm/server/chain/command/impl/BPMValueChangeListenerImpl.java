package com.wrupple.muba.bpm.server.chain.command.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.wrupple.muba.bpm.server.chain.command.AbstractComparationCommand;
import com.wrupple.muba.bpm.server.chain.command.BPMValueChangeListener;
import com.wrupple.muba.bpm.server.domain.ValueChangeTrigger;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.impl.CatalogActionTriggerHandlerImpl;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.domain.HasFieldId;
import com.wrupple.vegetate.server.chain.CatalogManager;
import com.wrupple.vegetate.server.domain.FilterCriteriaImpl;
import com.wrupple.vegetate.server.services.impl.FilterDataUtils;

@Singleton
public class BPMValueChangeListenerImpl extends AbstractComparationCommand implements BPMValueChangeListener {

	
	private CatalogTriggerInterpret interpret;
	@Inject
	public BPMValueChangeListenerImpl(CatalogPropertyAccesor accesor, Provider<CatalogManager> smp,CatalogTriggerInterpret interpret) {
		super(accesor);
		this.interpret=interpret;
		this.smp = smp;
	}

	private Provider<CatalogManager> smp;
	
	@Override
	protected void compare(String codedFinalValue, String codedInitialValue, Object initialValue, Object finalValue, CatalogEntry old, CatalogEntry neew,
			CatalogDescriptor catalog, FieldDescriptor field, CatalogExcecutionContext context) throws Exception {
		
		if((initialValue==null&&finalValue!=null) ||(initialValue!=null && !initialValue.equals(finalValue))){

			//TODO this means mny unecesary map searches when no triggers are provided
			List<ValueChangeTrigger> changeTriggers = (List<ValueChangeTrigger>) context.get(BPMValueChangeListener.CONTEXT_TRIGGERS_KEY);
			if(changeTriggers==null){
				CatalogDataAccessObject<ValueChangeTrigger> dao = smp.get().getOrAssembleDataSource(ValueChangeTrigger.CATALOG, context, ValueChangeTrigger.class);
				FilterData filterData = FilterDataUtils.createSingleFieldFilter(HasCatalogId.FIELD, catalog.getCatalogId());
				filterData.setConstrained(false);
				filterData.addFilter(new FilterCriteriaImpl(HasFieldId.FIELD, field.getFieldId()));

				changeTriggers = dao.read(filterData);
				context.put(CONTEXT_TRIGGERS_KEY, changeTriggers);
			}
			if (changeTriggers != null) {
				changeTriggers = filterByValue(codedInitialValue, codedFinalValue, changeTriggers);
				if (changeTriggers != null) {
					
					for(ValueChangeTrigger trigger : changeTriggers){
						//TODO this mean unecesary instances
						Map<String, String> properties=CatalogActionTriggerHandlerImpl.parseProperties(trigger.getProperties());
						interpret.invokeTrigger(catalog, neew, old, properties, context, trigger);
					}
					
				}
			}
		}
			

	}

	private List<ValueChangeTrigger> filterByValue(String codedInitialValue, String codedFinalValue, List<ValueChangeTrigger> changeTriggers) {
		int encoding;
		List<ValueChangeTrigger> results = null;
		for(ValueChangeTrigger trigger:changeTriggers){
			encoding = trigger.getEncoding();
			if(trigger.getFinalValue()==null && trigger.getInitialValue()==null){
				//not value dependent
			}else{
				if(results==null){
					results = new ArrayList<ValueChangeTrigger>(changeTriggers.size());
				}
				//depends on value
				if(encoding==1){
					//regex
					
					if(checkValueRegex(codedInitialValue,trigger.getInitialValue())&&checkValueRegex(codedInitialValue,trigger.getInitialValue())){
						results.add(trigger);
					}
					
				}else{
					//plain
					if(checkValue(codedInitialValue,trigger.getInitialValue())&&checkValue(codedInitialValue,trigger.getInitialValue())){
						results.add(trigger);
					}
				}
			}
		}
		if(results==null){
			results= changeTriggers;
		}
		return results;
	}

	private boolean checkValue(String value, String filter) {
		if(value==null){
			return filter!=null;
		}else if(filter!=null && filter.equals(WILDCARD)){
			return true;
		}else{
			return value.equals(filter);
		}
	}

	private boolean checkValueRegex(String value, String filter) {
		if(value==null){
			return filter!=null;
		}else{
			if(filter==null){
				return false;
			}else{
				return Pattern.matches(filter, value);
			}
		}
	}

}
