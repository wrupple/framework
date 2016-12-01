package com.wrupple.muba.bpm.client.services.impl;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.wrupple.muba.desktop.domain.DesktopPlace;

public class SimpleActivityTransition extends DataCallback<DesktopPlace> {
	
	private PlaceController pc;
	
	public SimpleActivityTransition(PlaceController pc) {
		super();
		this.pc=pc;
	}


	@Override
	public void execute() {
		try{
			pc.goTo(result);
		}catch(Exception e){
			GWT.log("SimpleActivityTransition fail", e);
		}
	}

}
