package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.view.client.Range;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.worker.server.service.StateTransition;

import java.util.List;

public interface RangePicker {

	void pickRange(double[] data, StateTransition<Range> callback,EventBus bus);
	
	void setNarrowers(List<? extends OutcomeNarrower> narrowers );
}
