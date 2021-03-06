package com.wrupple.muba.desktop.client.services.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.services.command.CommandService;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.vegetate.domain.FilterData;

public class PlaceInterpretCommand implements CommandService {

	CatalogPlaceInterpret interpret;
	ProcessContextServices context;
	private StateTransition<JsTransactionApplicationContext> onDone;
	private JsTransactionApplicationContext parameter;
	
	@Inject
	public PlaceInterpretCommand(CatalogPlaceInterpret interpret) {
		super();
		this.interpret = interpret;
	}

	@Override
	public void execute() {
		PlaceController placeController = context.getPlaceController();
		DesktopPlace place=(DesktopPlace) placeController.getWhere();
		String entry=interpret.getCurrentPlaceEntry(place);
		String catalog = interpret.getPlaceCatalog(place);
		FilterData filter = interpret.getCurrentPlaceFilterData(place);
		
		GWTUtils.setAttribute(parameter, CatalogActionRequest.CATALOG_ENTRY_PARAMETER, entry);
		GWTUtils.setAttribute(parameter, CatalogActionRequest.CATALOG_ID_PARAMETER, catalog);
		GWTUtils.setAttribute(parameter, CatalogActionRequest.FILTER_DATA_PARAMETER,(JsFilterData)filter);
		
		onDone.setResultAndFinish(parameter);		
	}



	@Override
	public void prepare(String command, JavaScriptObject commandProperties,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionApplicationContext processParameters,
			StateTransition<JsTransactionApplicationContext> callback) {
		this.parameter=processParameters;
		this.onDone=callback;
		this.context=processContext;
	}

}
