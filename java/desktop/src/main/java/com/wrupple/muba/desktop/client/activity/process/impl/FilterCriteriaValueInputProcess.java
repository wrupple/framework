package com.wrupple.muba.desktop.client.activity.process.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.activity.widgets.FilterOptionSelectionView;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.FilterToolbarImpl;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsFilterCriteria;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;
public class FilterCriteriaValueInputProcess extends
		SequentialProcess<String, JsFilterCriteria> {
	
	

	@Inject
	public FilterCriteriaValueInputProcess(final DesktopManager dm,final StorageManager loadCatalog,FilterOptionSelectionView fieldSelection, ContentManagementSystem cms) {
		super();
		add(new State<String, CatalogDescriptor>(){

			@Override
			public void start(String parameter, StateTransition<CatalogDescriptor> onDone, EventBus bus) {
				loadCatalog.loadCatalogDescriptor(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), parameter, onDone);
			}});
		add(new ExtractFilterableFields());
		add(fieldSelection);
		add(new DetermineFollowupStepsAccordingToFieldSelection(cms));
		
	}
	
	class DetermineFollowupStepsAccordingToFieldSelection implements State.ContextAware<List<FieldDescriptor>,JavaScriptObject>{
		 ContentManagementSystem cms;
		ProcessContextServices contextServices;
		
		
		public DetermineFollowupStepsAccordingToFieldSelection(
				ContentManagementSystem cms) {
			this.cms=cms;
		}

		@Override
		public void start(List<FieldDescriptor> parameter,
				StateTransition<JavaScriptObject> onDone, EventBus bus) {
			JsFieldDescriptor field = (JsFieldDescriptor) parameter.get(0);
			if(field.isKey() && field.getForeignCatalogName()!=null){
				String catalog = field.getForeignCatalogName();
				ContentManager<JsCatalogEntry> cm = cms.getContentManager(catalog);
				Process<JsTransactionActivityContext, JsTransactionActivityContext> process = cm.getSelectionProcess(contextServices, true, false);
				addAll(process);
				add(new BuildCriteriaFromUserSelection());
				JsTransactionActivityContext regreso = JsTransactionActivityContext.createObject().cast();
				saveFieldData(regreso,field);
				onDone.setResultAndFinish(regreso);
			}else{
				JsFilterCriteria criteria = JsFilterCriteria.createObject().cast();
				criteria.setOperator(FilterData.EQUALS);
				criteria.pushToPath(field.getFieldId());
				FilterToolbarImpl.setRefreshData(criteria, false);
				onDone.setResultAndFinish(criteria);
			}
		}

		@Override
		public void setContext(ProcessContextServices context) {
			this.contextServices=context;
		}
		
	}
	
	 class BuildCriteriaFromUserSelection implements State<JsTransactionActivityContext,JsFilterCriteria>{

		@Override
		public void start(JsTransactionActivityContext result,
				StateTransition<JsFilterCriteria> onDone, EventBus bus) {
			JsCatalogEntry output = result.getUserOutput();
			if(output!=null){
				JsArray<JsCatalogEntry> regreso = output.cast();
				if(regreso.length()>0){
					JsCatalogEntry temp;
					String key;
					JsFieldDescriptor field = getFieldData(result);
					JsFilterCriteria criteria = JsFilterCriteria.createObject().cast();
					criteria.setOperator(FilterData.EQUALS);
					criteria.pushToPath(field.getFieldId());
					for(int i = 0 ; i < regreso.length(); i++){
						temp = regreso.get(i);
						key = temp.getId();
						criteria.addValue(key);
					}
					onDone.setResultAndFinish(criteria);
				}else{
					onDone.setResultAndFinish(null);
				}
			}else{
				onDone.setResultAndFinish(null);
			}
		}
		
	}
	
	static class ExtractFilterableFields implements State<JsCatalogDescriptor,List<FieldDescriptor>>{

		@Override
		public void start(JsCatalogDescriptor parameter,
				StateTransition<List<FieldDescriptor>> onDone, EventBus bus) {
			 List<FieldDescriptor> fields = parameter.getOwnedFieldsValues();
			 List<FieldDescriptor> filterableFields = new ArrayList<FieldDescriptor>();
			 for(FieldDescriptor field: fields){
				 //dont know (yet) how to filter ephemeral or multiple fields
				 if(field.isFilterable() && !(field.isEphemeral()||field.isMultiple())){
					 filterableFields.add(field);
				 }
			 }
			 onDone.setResultAndFinish(filterableFields);
		}
		
	}

	protected native void saveFieldData(JsTransactionActivityContext regreso,
			FieldDescriptor field) /*-{
		regreso.fieldDescriptorCriteriaData=field;
	}-*/;
	
	protected native JsFieldDescriptor getFieldData(JsTransactionActivityContext regreso) /*-{
		return regreso.fieldDescriptorCriteriaData;
	}-*/;

}
