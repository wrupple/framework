		package com.wrupple.muba.bpm.client.activity.process.state.impl;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.AbstractStartUserTransaction;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;


public class StartSelectTransaction extends AbstractStartUserTransaction{

	@Inject
	public StartSelectTransaction(CatalogPlaceInterpret placeInterpret) {
		super(placeInterpret);
	}
	
	
	@Override
	public void start(JsTransactionActivityContext context,
			StateTransition<JsTransactionActivityContext> onDone, EventBus bus) {
		super.start(context, onDone, bus);
		JsFilterData filterData = context.getFilterData();
		if (filterData == null) {
			// THIS READS CRITERIA... so order, offest, and columns should be parsed separately
			PlaceController pc = contextServices.getPlaceController();
			DesktopPlace place = ((DesktopPlace) pc.getWhere()).cloneItem();
			filterData = (JsFilterData) this.placeInterpret
					.getCurrentPlaceFilterData(place);
			if(filterData==null){
				filterData=JsFilterData.newFilterData();
			}
			context.setFilterData(filterData);
		}
		
		onDone.setResultAndFinish(context);
	}


}
