package com.wrupple.muba.desktop.client.services.logic;

import java.util.List;

import com.google.gwt.view.client.Range;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;

public interface RangePicker {

	void pickRange(double[] data, StateTransition<Range> callback,EventBus bus);
	
	void setNarrowers(List<? extends OutcomeNarrower> narrowers );
}
