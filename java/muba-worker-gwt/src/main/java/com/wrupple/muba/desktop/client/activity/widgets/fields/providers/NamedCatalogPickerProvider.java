package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.activity.process.CatalogTypeSelectionProcess;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.SimpleTextCell;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.worker.client.activity.process.state.State;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Provider;
import java.util.List;
public class NamedCatalogPickerProvider implements CatalogFormFieldProvider{

	private CatalogUserInterfaceMessages msgs;
	private Provider<CatalogTypeSelectionProcess> provider;
	private ProcessContextServices contextParameters;
	
	@Inject
	public NamedCatalogPickerProvider( Provider<CatalogTypeSelectionProcess> provider,CatalogUserInterfaceMessages msgs) {
		super();
		this.provider=provider;
		this.msgs = msgs;
	}



	@Override
	public Cell<String> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		this.contextParameters=contextServices;
		if(mode==CatalogAction.READ){
			return new TextCell();
		}else{
			Provider<Process<String, String>> nestedProcessProvider= new Provider<Process<String,String>>() {
				
				@Override
				public Process<String, String> get() {
					return getDelegateProcess();
				}
			};
			return new SimpleTextCell(bus, contextServices, contextParameters, d, mode, nestedProcessProvider, msgs.selectWorkingCatalog());
		}
	}
	protected Process<String, String> getDelegateProcess() {
		SequentialProcess< String, String> regreso = new SequentialProcess<String, String>();
		regreso.add(new Begin());
		regreso.addAll(this.provider.get());
		regreso.add(new End());
		return regreso;
	}
	
	static class Begin implements State<String,DesktopPlace>{

		@Override
		public void start(String parameter,
				StateTransition<DesktopPlace> onDone, EventBus bus) {
			onDone.setResultAndFinish(null);
		}
		
	}
	
	class End implements State<List<DesktopPlace>,String>{

		@Override
		public void start(List<DesktopPlace> parameter,
				final StateTransition<String> onDone, EventBus bus) {
			if(parameter==null){
				onDone.setResultAndFinish(null);
			}
			final String resultt = parameter.get(0).getProperty(CatalogActionRequest.CATALOG_ID_PARAMETER);
			contextParameters.getStorageManager().loadCatalogDescriptor(contextParameters.getDesktopManager().getCurrentActivityHost(), contextParameters.getDesktopManager().getCurrentActivityDomain(), resultt, new DataCallback<CatalogDescriptor>() {

				@Override
				public void execute() {
					//just to load in cache
					onDone.setResultAndFinish(resultt);
				}
			});
		}
		
	}

}
