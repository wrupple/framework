package com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.muba.worker.client.activity.process.state.HumanTask;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;
import com.wrupple.vegetate.domain.FilterData;

import java.util.List;

/**
 * 
 * Add: select Remove: unlink ¿remove?
 * 
 * @author japi
 * 
 */
public class OneToManyRelationshipDelegate
		implements AbstractValueRelationEditor.RelationshipDelegate {
	

	static class SelectionState implements HumanTask<List<JsCatalogEntry>,List<JsCatalogEntry>>{
		
		private TakesValue<List<JsCatalogEntry>> delegate;
		
		public SelectionState(TakesValue<List<JsCatalogEntry>> delegate) {
			super();
			this.delegate = delegate;
		}

		@Override
		public void start(List<JsCatalogEntry> parameter,
				StateTransition<List<JsCatalogEntry>> onDone, EventBus bus) {
			delegate.setValue(parameter);
			
		}

		@Override
		public Widget asWidget() {
			return ((IsWidget)delegate).asWidget();
		}
		
	}
	
	private ContentManagementSystem cms;
	private FieldDescriptor descriptor;
	private String permanentDelete;
	private StorageManager sm;
	protected boolean limitToOne;
	private StateTransition<JsFilterData> callback;
	private DesktopManager dm;

	public OneToManyRelationshipDelegate(DesktopManager dm, 
			FieldDescriptor descriptor, ContentManagementSystem cms,
			StorageManager sm, 
			String permanentDelete) {
		limitToOne = false;
		this.cms = cms;
		this.dm=dm;
		this.descriptor = descriptor;
		this.sm = sm;
		this.permanentDelete = permanentDelete;
	}

	

	private void unlinkValue(FilterData filter, String valueToRemove) {

		filter.removeFilterByValue(JsCatalogEntry.ID_FIELD, valueToRemove);
	}

	class PermanentDeleteCallback extends DataCallback<JsCatalogEntry> {

		@Override
		public void execute() {
			// TODO Auto-generated method stub

		}

	}

	class SelectionCallback extends DataCallback<JsTransactionApplicationContext> {
		JsFilterData previousValue;
		private StateTransition<JsFilterData> callback;

		public SelectionCallback(JsFilterData previousValue,
				StateTransition<JsFilterData> callback) {
			super();
			this.previousValue = previousValue;
			this.callback = callback;
		}

		@Override
		public void execute() {
			JsArray<JsCatalogEntry> userOutput = super.result.getUserOutput().cast();
			JsCatalogKey temp;
			if (userOutput == null || userOutput.length()==0) {
				callback.setResultAndFinish(null);
			} else {
				if (limitToOne||previousValue == null) {
					previousValue = JsFilterData.newFilterData();
				}
				FilterCriteria criteria = previousValue
						.fetchCriteria(JsCatalogEntry.ID_FIELD);
				if (criteria == null) {
					criteria = JsFilterCriteria.newFilterCriteria();
					criteria.setOperator(FilterData.EQUALS);
					criteria.pushToPath(JsCatalogEntry.ID_FIELD);
					previousValue.addFilter( criteria);
				}
				if (limitToOne) {
					String value = null;
					if (userOutput != null) {
						JsCatalogKey entry = userOutput.get(0);
						temp = entry.cast();
						value = temp.getId();
					}
					criteria.addValue(value);

				} else {

					if (userOutput != null) {
						String value;
						JsCatalogKey entry;
						
						for (int  i = 0 ; i< userOutput.length(); i++) {
							entry = userOutput.get(i);
							temp = entry.cast();
							value = temp.getId();
							criteria.addValue(value);
						}
					}
				}
				callback.setResultAndFinish(previousValue);
			}

		}
	}

	@Override
	public void setValueChanger(StateTransition<JsFilterData> callback) {
		this.callback=callback;
	}

	@Override
	public void onRelationshipRemovalRequested(JsFilterData currentValues, String valueToRemove) {
		// unlink
				unlinkValue(currentValues, valueToRemove);
				// remove?
				if (permanentDelete != null) {
					boolean delete = Window.confirm(permanentDelete);
					if (delete) {
						String catalog = descriptor.getForeignCatalogName();
						sm.delete(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, valueToRemove, new PermanentDeleteCallback());
					}
				}

				callback.setResultAndFinish(currentValues);
	}

	@Override
	public void onRelationshipAdditionRequested(JsFilterData currentValue, JavaScriptObject contextParameters, ProcessContextServices contextServices) {
		String catalog = descriptor.getForeignCatalogName();
		String title = descriptor.getName();
		ContentManager<JsCatalogEntry> manager = cms.getContentManager(catalog);
		StateTransition<JsTransactionApplicationContext> selectionCallback = new SelectionCallback(
				currentValue, callback);
		
		JsTransactionApplicationContext input = JavaScriptObject.createObject().cast();
		
		com.wrupple.muba.bpm.client.services.Process<JsTransactionApplicationContext,JsTransactionApplicationContext> process =manager.getSelectionProcess(contextServices, true,true);
		contextServices.getProcessManager().processSwitch(process, title, input, selectionCallback, contextServices);		
	}



	@Override
	public void changeValue(JsArrayString ids) {
		JsFilterData result = JsFilterData.createSingleFieldFilter(JsCatalogKey.ID_FIELD, ids);
		callback.setResultAndFinish(result);
	}

}