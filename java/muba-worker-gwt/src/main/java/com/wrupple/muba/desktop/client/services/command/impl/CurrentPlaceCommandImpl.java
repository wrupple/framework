package com.wrupple.muba.desktop.client.services.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.client.services.command.CurrentPlaceCommand;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public class CurrentPlaceCommandImpl implements CurrentPlaceCommand {

	private String[] targetActivity;
	private JavaScriptObject output;
	private PlaceController pc;

	@Inject
	public CurrentPlaceCommandImpl() {
		super();
	}

	@Override
	public void prepare(String command, JavaScriptObject properties, EventBus eventBus, ProcessContextServices processContext,
                        JsTransactionApplicationContext contextParameters, StateTransition<JsTransactionApplicationContext> callback) {
		this.pc = processContext.getPlaceController();
		this.targetActivity = ((DesktopPlace) pc.getWhere()).getTokens();
		this.output = contextParameters.getUserOutput();

	}

	@Override
	public void execute() {
		JsCatalogKey select;
		JsArray<JsCatalogEntry> outputArray;
		if (GWTUtils.isArray(output)) {
			outputArray = output.cast();
			if (outputArray.length() == 0) {
				Window.alert("Select something first!");
				throw new IllegalArgumentException();
			} else {
				select = outputArray.get(0);
			}
		} else {
			select = output.cast();
		}
		DesktopPlace result = ((DesktopPlace) pc.getWhere()).cloneItem();

		result.setTokens(this.targetActivity);
		result.setProperty(CatalogActionRequest.CATALOG_ID_PARAMETER, select.getCatalog());
		result.setProperty(CatalogActionRequest.CATALOG_ENTRY_PARAMETER, select.getId());
		pc.goTo(result);
	}

}
