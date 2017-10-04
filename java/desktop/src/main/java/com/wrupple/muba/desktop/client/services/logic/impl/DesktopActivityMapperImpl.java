package com.wrupple.muba.desktop.client.services.logic.impl;

import java.util.Map;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.process.state.DesktopActivityMapper;
import com.wrupple.muba.desktop.client.factory.dictionary.DesktopActivityMap;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;

public class DesktopActivityMapperImpl implements DesktopActivityMapper {
	private final DesktopActivityMap activityMap;
	private final DesktopManager dm;
	
	@Inject
	public DesktopActivityMapperImpl(DesktopActivityMap activityMap, DesktopManager dm) {
		super();
		this.activityMap = activityMap;
		this.dm=dm;
	}


	@Override
	public Activity getActivity(Place place) {
		DesktopPlace desktopPlace = (DesktopPlace) place;
		Map<String, String> proper = desktopPlace.getProperties();
		JavaScriptObject configuration=GWTUtils.convertMapToJavascriptObject(proper);
		
		if(desktopPlace.getApplicationItem()==null){
			dm.getApplicationItem(desktopPlace);
		}
		
		GWTUtils.setAttribute(configuration, "activity", desktopPlace.getActivityUri());
		//Workflow rootPlace = getRootPlace(desktopPlace);
		Activity a = this.activityMap.getConfigured(configuration, null, null, null);
		return a;
	}




}
