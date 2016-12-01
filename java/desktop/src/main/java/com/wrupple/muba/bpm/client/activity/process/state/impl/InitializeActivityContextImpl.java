package com.wrupple.muba.bpm.client.activity.process.state.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.InitializeActivityContext;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.factory.dictionary.ExternalAPILoaderMap;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public class InitializeActivityContextImpl implements InitializeActivityContext {

	
	private JsApplicationItem item;
	private ExternalAPILoaderMap apiMap;
	private boolean[] resolved;
	private ProcessContextServices context;

	@Inject
	public InitializeActivityContextImpl(ExternalAPILoaderMap apiMap) {
		super();
		this.apiMap = apiMap;
	}

	@Override
	public void start(DesktopPlace parameter, StateTransition<JsTransactionActivityContext> onDone, EventBus bus) {
		
		JsTransactionActivityContext nueva = JsTransactionActivityContext.createObject().cast();
		nueva.setApplicationItem(item);
		
		JavaScriptObject appProperties = item.getPropertiesObject();
		String rawAPINames = GWTUtils.getAttribute(appProperties, ExternalAPILoaderMap.ATTRIBUTE);
		if (rawAPINames == null) {
			onDone.setResultAndFinish(nueva);
		} else {
			State<JsTransactionActivityContext, JsTransactionActivityContext>[] states = getAPILoadingStates(rawAPINames, appProperties, bus, nueva);
			start(nueva, onDone, bus, states);
		}

	}

	private State<JsTransactionActivityContext, JsTransactionActivityContext>[] getAPILoadingStates(String rawAPINames, JavaScriptObject appProperties,
			EventBus bus, JsTransactionActivityContext nueva) {
		String[] names = rawAPINames.split(",");
		State<JsTransactionActivityContext, JsTransactionActivityContext>[] regreso = new State[names.length];
		State<JsTransactionActivityContext, JsTransactionActivityContext> temp;
		String name;
		for (int i = 0; i < names.length; i++) {
			name = names[i];
			GWTUtils.setAttribute(appProperties, ExternalAPILoaderMap.ATTRIBUTE, name);
			temp = apiMap.getConfigured(appProperties, context, bus, nueva);
			if (temp == null) {
				throw new IllegalArgumentException("unrecognized API name " + name);
			}
			regreso[i] = temp;
		}
		return regreso;
	}

	class Resolver extends DataCallback<JsTransactionActivityContext> {
		final int index;
		final StateTransition<JsTransactionActivityContext> onDone;

		public Resolver(int index, StateTransition<JsTransactionActivityContext> onDone) {
			this.index = index;
			this.onDone = onDone;
		}

		@Override
		public void execute() {
			resolved[index] = true;
			for (boolean b : resolved) {
				if (!b) {
					return;
				}
			}
			onDone.setResultAndFinish(result);
		}

	}

	public void start(JsTransactionActivityContext parameter, StateTransition<JsTransactionActivityContext> onDone, EventBus bus,
			State<JsTransactionActivityContext, JsTransactionActivityContext>[] states) {
		if (this.resolved != null) {
			throw new IllegalArgumentException();
		}
		this.resolved = new boolean[states.length];
		State<JsTransactionActivityContext, JsTransactionActivityContext> state;
		for (int i = 0; i < states.length; i++) {
			state = states[i];
			state.start(parameter, new Resolver(i, onDone), bus);
		}
	}

	@Override
	public void setApplicationItem(JsApplicationItem item) {
		this.item = item;
	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.context = context;
	}

}