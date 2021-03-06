package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.services.logic.ExternalAPILoader;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.server.service.StateTransition;
import org.sgx.raphael4gwt.graphael.GRaphaelLoader;

public class LoadGraphaelSVGAPI implements ExternalAPILoader {
	static boolean loaded = false;

	@Override
	public void start(final JsTransactionApplicationContext parameter, final StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {
		if(loaded){
			onDone.setResultAndFinish(parameter);
		}else{
			GRaphaelLoader.loadGRaphael(new org.sgx.raphael4gwt.raphael.RaphaelLoader.Listener() {
				@Override
				public void loaded(Exception error) {
					if (error == null) {
						loaded=true;
						onDone.setResultAndFinish(parameter);
					} else {
						GWT.log("ERROR loading GRaphael : " + error);
					}
				}
			});
		}
	}

}
