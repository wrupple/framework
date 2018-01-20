package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.maps.client.LoadApi.Language;
import com.google.gwt.maps.client.LoadApi.LoadLibrary;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.activity.process.state.LoadMapsApi;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.server.service.StateTransition;

import java.util.ArrayList;

public class LoadGoogleMapsApi implements LoadMapsApi {

	static boolean done = false;
	
	public static String PARAMETERS;/*"key=MY_API_KEY" */

	@Override
	public void start(final JsTransactionApplicationContext parameter, final StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {
		if (done) {
			onDone.setResultAndFinish(parameter);
		} else {

			boolean sensor = true;

			// load all the libs for use in the maps
			ArrayList<LoadLibrary> loadLibraries = new ArrayList<LoadApi.LoadLibrary>();
			loadLibraries.add(LoadLibrary.DRAWING);
			loadLibraries.add(LoadLibrary.GEOMETRY);
			loadLibraries.add(LoadLibrary.PLACES);
			loadLibraries.add(LoadLibrary.WEATHER);
			loadLibraries.add(LoadLibrary.VISUALIZATION);

			Runnable onLoad = new Runnable() {
				@Override
				public void run() {
					done = true;
					onDone.setResultAndFinish(parameter);
				}
			};
			String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName().toLowerCase();
			if(currentLocale.startsWith("zh")){
				currentLocale = currentLocale.replace('_', '-');
			}else{
				currentLocale =currentLocale.substring(0, 2);
			}
			Language e=Language.ENGLISH;
			for(Language curr: Language.values()){
				if(curr.getValue().equals(currentLocale)){
					e = curr;
					break;
				}
			}
			
			if(PARAMETERS==null){
				LoadApi.go(onLoad,loadLibraries,sensor,e);
			}else{
				LoadApi.go(onLoad,loadLibraries,sensor,e,PARAMETERS);
			}
			
		}

	}
}
