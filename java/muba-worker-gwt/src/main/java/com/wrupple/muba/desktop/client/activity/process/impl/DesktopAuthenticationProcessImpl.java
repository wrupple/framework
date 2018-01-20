package com.wrupple.muba.desktop.client.activity.process.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.activity.process.DesktopAuthenticationProcess;
import com.wrupple.muba.worker.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.worker.client.activity.process.state.HumanTask;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.vegetate.domain.VegetateAuthenticationToken;

public class DesktopAuthenticationProcessImpl extends SequentialProcess<Void, Boolean> implements DesktopAuthenticationProcess {

	public DesktopAuthenticationProcessImpl() {
		addState(new HumanTask<Void, Boolean>() {
			FlowPanel panel= new FlowPanel();
			
			@Override
			public void start(Void parameter, StateTransition<Boolean> onDone, EventBus bus) {
				panel.setStyleName("DesktopAuthenticationProcessImpl");
				panel.add(new Button("twitter",new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						String uri=Window.Location.getHref();
						String param = new JSONObject(getValue(uri)).toString();
						Window.Location.assign("/"+VegetateAuthenticationToken.OAUTH_SERVICE+"/TwitterAuthenticationRealm/?0="+URL.encode(param));
					}

					private native JavaScriptObject getValue(String uri) /*-{
						return {"callback":uri};
					}-*/;
				}));
			}

			@Override
			public Widget asWidget() {
				return panel;
			}
		});

	}

}
