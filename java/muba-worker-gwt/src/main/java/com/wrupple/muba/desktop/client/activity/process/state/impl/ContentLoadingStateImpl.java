package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogProcessDescriptor;
import com.wrupple.muba.desktop.client.activity.process.state.ContentLoadingState;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public class ContentLoadingStateImpl implements ContentLoadingState {

	
	class ModifyLoadedEntry extends DataCallback<JsCatalogEntry>{
		 StateTransition<JsCatalogEntry> onDone;
		 
		public ModifyLoadedEntry(StateTransition<JsCatalogEntry> onDone) {
			super();
			this.onDone = onDone;
		}

		@Override
		public void execute() {
			alterLoadedEntry(result);
		}
		
	}
	private ProcessContextServices context;

	@Inject
	public ContentLoadingStateImpl() {
		super();
	}


	@Override
	public void start(CatalogProcessDescriptor parameter, final StateTransition<JsCatalogEntry> onDone,EventBus bus) {
		String entryId = parameter.getSelectedValueId();
		String catalog = parameter.getSelectedType();
		JsCatalogEntry regreso = null;
		if (entryId == null||entryId.isEmpty()) {
			regreso = JsCatalogEntry.createCatalogEntry(catalog);
			alterLoadedEntry(regreso);
			onDone.setResult(regreso);
			onDone.execute();
		} else {
			context.getStorageManager().read(context.getDesktopManager().getCurrentActivityHost(),context.getDesktopManager().getCurrentActivityDomain(), catalog, entryId, onDone);
		}
	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.context=context;
	}
	protected void alterLoadedEntry(JsCatalogEntry result) {
		
	}
}
