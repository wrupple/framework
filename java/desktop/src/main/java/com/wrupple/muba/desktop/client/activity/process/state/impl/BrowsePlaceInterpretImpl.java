package com.wrupple.muba.desktop.client.activity.process.state.impl;

import java.util.List;

import javax.inject.Provider;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogProcessDescriptor;
import com.wrupple.muba.desktop.client.activity.process.state.CatalogSelectionLoader;
import com.wrupple.muba.desktop.client.activity.process.state.CatalogTypeSelectionTask;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.vegetate.domain.FilterData;

public class BrowsePlaceInterpretImpl implements com.wrupple.muba.bpm.client.activity.process.state.State.ContextAware<DesktopPlace,CatalogProcessDescriptor> {
	
	class CatalogSelectionCallback extends DataCallback<List<DesktopPlace>>{
		 DesktopPlace originalItem;
		 StateTransition<CatalogProcessDescriptor> originalCallback;
		 EventBus bus;
		 
		public CatalogSelectionCallback(DesktopPlace originalItem,
				StateTransition<CatalogProcessDescriptor> originalCallback,
				EventBus bus) {
			super();
			this.originalItem = originalItem;
			this.originalCallback = originalCallback;
			this.bus = bus;
		}

		@Override
		public void execute() {
			String catalogid = result.get(0).getProperty(CatalogActionRequest.CATALOG_ID_PARAMETER);
			originalItem.setProperty(CatalogActionRequest.CATALOG_ID_PARAMETER, catalogid);
			start(originalItem, originalCallback, bus);
		}
		
	}

	private ProcessContextServices context;
	Provider<CatalogTypeSelectionTask> typeSelection;
	Provider<CatalogSelectionLoader> selectionInterpretState;
	private CatalogPlaceInterpret interpret;
	@Inject
	public BrowsePlaceInterpretImpl(  CatalogPlaceInterpret interpret, Provider<CatalogTypeSelectionTask> typeSelection,Provider<CatalogSelectionLoader> selectionInterpretState) {
		super();
		this.interpret=interpret;
		this.typeSelection=typeSelection;
		this.selectionInterpretState= selectionInterpretState;
	}

	@Override
	public void start(final DesktopPlace parameter, final StateTransition<CatalogProcessDescriptor> onDone,EventBus bus) {
		String catalogid = interpret.getPlaceCatalog(parameter);
		String entryId = interpret.getCurrentPlaceEntry(parameter);
		FilterData filterData = interpret.getCurrentPlaceFilterData(parameter);
		
		if(catalogid==null){
			CatalogSelectionLoader interpretState = selectionInterpretState.get();
			CatalogTypeSelectionTask state = typeSelection.get();
			SequentialProcess<DesktopPlace, List<DesktopPlace>> process = new SequentialProcess<DesktopPlace, List<DesktopPlace>>();
			process.add(interpretState);
			process.add(state);
			StateTransition<List<DesktopPlace>> callback = new CatalogSelectionCallback(parameter, onDone, bus);
			context.getProcessManager().processSwitch(process, "BrowsePlaceInterpretImpl", parameter , callback, context);
		}else{
			final CatalogProcessDescriptor regreso = new CatalogProcessDescriptor();
			regreso.setSelectedType(catalogid);
			regreso.setSelectedValueIdd(entryId);
			regreso.setFilterData(filterData);

			onDone.setResult(regreso);
			onDone.execute();
		}
		
	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.context= context;
	}



}
