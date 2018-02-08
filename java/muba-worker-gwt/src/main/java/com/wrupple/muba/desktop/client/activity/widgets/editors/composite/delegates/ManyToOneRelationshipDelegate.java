package com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.worker.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.Collection;
/**
 * Add: Create a new Entry holding a referenco to local entry
 * Remove: Permanently delete entry
 */
public class ManyToOneRelationshipDelegate  implements AbstractValueRelationEditor.RelationshipDelegate{

	final StorageManager catalogDescriptor;
	final EventBus bus;
	final ContentManagementSystem cms;
	final FieldDescriptor descriptor;
	final String host,domain,localCatalogId,localEntryId;
	final ProcessContextServices contextServices;
	final JavaScriptObject contextParameters;
	private StateTransition<JsFilterData> callback;
	
	public ManyToOneRelationshipDelegate(
			StorageManager catalogDescriptor, EventBus bus,ProcessContextServices contextServices, ContentManagementSystem cms,
			FieldDescriptor descriptor,String host,String domain, String localCatalogId,
			String localEntryId,JavaScriptObject contextParameters) {
		super();
		this.host=host;
		this.domain=domain;
		this.contextServices=contextServices;
		this.catalogDescriptor = catalogDescriptor;
		this.bus = bus;
		this.cms = cms;
		this.descriptor = descriptor;
		this.localCatalogId = localCatalogId;
		this.localEntryId = localEntryId;
		this.contextParameters=contextParameters;
	}
	
	
	class DeleteCallback extends DataCallback<JsCatalogEntry>{
		StateTransition<JsFilterData> callback;
		private JsFilterData currentValue;
		
		public DeleteCallback(StateTransition<JsFilterData> callback,JsFilterData currentValue) {
			super();
			this.currentValue = currentValue;
			this.callback = callback;
		}

		@Override
		public void execute() {
			callback.setResultAndFinish(currentValue);
		}
		
	}
	
	class CatalogCallback extends DataCallback<CatalogDescriptor>{
		 StateTransition<JsFilterData> callback;
		private JsFilterData currentValue;
		 
		public CatalogCallback(JsFilterData currentValue, StateTransition<JsFilterData> callback) {
			super();
			this.callback = callback;
			this.currentValue = currentValue;
		}


		@Override
		public void execute() {
			
			Collection<FieldDescriptor> fields = result.getOwnedFieldsValues();
			String foreignCatalog = result.getCatalogId();
			String title = descriptor.getName();
			String fieldPointsToLocalEntry;
			JsCatalogKey entry = JsCatalogEntry.createCatalogEntry(foreignCatalog).cast();
			for(FieldDescriptor field : fields){
				if(localCatalogId.equals(field.getForeignCatalogName())){
					fieldPointsToLocalEntry = field.getFieldId();
					GWTUtils.setAttribute(entry, fieldPointsToLocalEntry, localEntryId);
				}
			}
			ContentManager<JsCatalogEntry> manager = cms.getContentManager(foreignCatalog);
			ProcessManager  pm = contextServices.getProcessManager();
			//Grab Takes value presenter from cms
			Process process = manager.getEditingProcess(CatalogAction.CREATE, bus, contextServices);
			
			pm.processSwitch(process, title, entry, new CreationCallback(callback,currentValue), contextServices);
		}
		
	}
	
	
	class CreationCallback extends DataCallback<JsCatalogEntry>{
		StateTransition<JsFilterData> callback;
		private JsFilterData currentValue;
		
		public CreationCallback(StateTransition<JsFilterData> callback,JsFilterData currentValue) {
			super();
			this.currentValue = currentValue;
			this.callback = callback;
		}

		@Override
		public void execute() {
			callback.setResultAndFinish(currentValue);
		}
		
	}


	@Override
	public void setValueChanger(StateTransition<JsFilterData> callback) {
		this.callback=callback;
	}
	@Override
	public void onRelationshipRemovalRequested(JsFilterData currentValues, String valueToRemove) {
		StorageManager sm = contextServices.getStorageManager();
		String foreignCatalog = descriptor.getForeignCatalogName();
		sm.delete(host, domain, foreignCatalog, valueToRemove, new DeleteCallback(callback,currentValues));		
	}
	@Override
	public void onRelationshipAdditionRequested(JsFilterData currentValue, JavaScriptObject contextParameters, ProcessContextServices contextServices) {
		String foreignCatalog = descriptor.getForeignCatalogName();
		catalogDescriptor.loadCatalogDescriptor(host, domain, foreignCatalog, new CatalogCallback(currentValue,callback));		
	}
	@Override
	public void changeValue(JsArrayString ids) {
		JsFilterData result = JsFilterData.createSingleFieldFilter(JsCatalogKey.ID_FIELD, ids);
		callback.setResultAndFinish(result);		
	}




}