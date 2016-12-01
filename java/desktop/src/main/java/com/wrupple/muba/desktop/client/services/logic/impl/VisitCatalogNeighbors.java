package com.wrupple.muba.desktop.client.services.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.impl.ParallelProcess;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class VisitCatalogNeighbors extends DataCallback<CatalogDescriptor> {
	

	private StateTransition<CatalogDescriptor> onDone;
	private StorageManager descriptor;
	private Set<String> visitedCatalogs;
	private EventBus eventBus;
	private String host,domain;

	public VisitCatalogNeighbors(String host,String domain,EventBus eventBus,StateTransition<CatalogDescriptor> onDone,StorageManager descriptor, Set<String> visitedCatalogs) {
		this.onDone=onDone;
		this.host=host;
		this.domain=domain;
		this.eventBus=eventBus;
		this.descriptor=descriptor;
		this.visitedCatalogs=visitedCatalogs;
	}

	@Override
	public void execute() {

		final List<String> catalogIds = new ArrayList<String>();
		String foreignCatalog;
		Collection<FieldDescriptor> fields = result.getOwnedFieldsValues();
		for (FieldDescriptor field : fields) {
			if (ImplicitJoinUtils.isJoinableValueField(field)) {
				foreignCatalog = field.getForeignCatalogName();
				if(foreignCatalog != null&&!visitedCatalogs.contains(foreignCatalog)){
						catalogIds.add(foreignCatalog);
				}
			}
		}
		// GWT.log(catalog+" asked for ephemerals"+catalogIds);

		/*
		 * FIXME WHEN SIMULTANEUS CALLS TO THE CATALOG DESCRIPTION SERVICE
		 * ARE MADE IT TENDS TO STALL OR NOT RESPOND:
		 * 
		 * 1.- WHY? IT SEEMS ROBOUST ENOUGH 2.- INLINE CATALOG REQUESTS TO
		 * ALIVIATE THE SERVER
		 * 
		 * 
		 * dispatcher.hook(new DataCallback<List<CatalogDescriptor>>() {
		 * 
		 * @Override public void execute() { resumed=true; } });
		 * Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
		 * 
		 * @Override public boolean execute() { GWT.log(catalog
		 * +" has not repplied retrying..."); retryn(catalogIds,
		 * dispatcher); return !resumed; } }, 10000);
		 */
		
		if (catalogIds.isEmpty()) {
			onDone.setResultAndFinish(result);
		} else {
			LoadDescriptorState loader = new LoadDescriptorState(descriptor,visitedCatalogs, host, domain);
			ParallelProcess<String, CatalogDescriptor> load = new ParallelProcess<String, CatalogDescriptor>(loader, false,false);
			OnLoadDispatch dispatcher = new OnLoadDispatch(result, onDone);
			load.start(catalogIds, dispatcher, eventBus);
		}

	}
	
	static class LoadDescriptorState implements State<String, CatalogDescriptor> {

		
		
		private StorageManager descriptionService;
		private Set<String> visitedCatalogs;
		private String host,domain;
		
		public LoadDescriptorState(StorageManager descriptionService,Set<String> visitedCatalogs, String host, String domain) {
			super();
			this.visitedCatalogs=visitedCatalogs;
			this.descriptionService=descriptionService;
			this.host = host;
			this.domain = domain;
		}

		@Override
		public void start(final String parameter,final StateTransition<CatalogDescriptor> onDone,
				EventBus bus) {
			visitedCatalogs.add(parameter);
			descriptionService.loadCatalogDescriptor(host,domain, parameter, new VisitCatalogNeighbors(host,domain,bus, onDone, descriptionService, visitedCatalogs));
		}

	}
	
	static class OnLoadDispatch extends DataCallback<List<CatalogDescriptor>>{
		final CatalogDescriptor root;
		final  StateTransition<CatalogDescriptor> onDone;

		public OnLoadDispatch(CatalogDescriptor root, StateTransition<CatalogDescriptor> onDone) {
			super();
			this.root = root;
			this.onDone = onDone;
		}

		@Override
		public void execute() {
			onDone.setResultAndFinish(root);
		}
		
	}

}