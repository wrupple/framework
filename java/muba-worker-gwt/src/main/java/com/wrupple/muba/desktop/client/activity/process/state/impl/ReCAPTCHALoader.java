package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.client.ScriptInjector.FromUrl;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.desktop.client.services.logic.ExternalAPILoader;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public class ReCAPTCHALoader implements ExternalAPILoader {

	static boolean loaded = false;
	static JsTransactionApplicationContext a;
	static StateTransition<JsTransactionApplicationContext> b;

	@Override
	public void start(final JsTransactionApplicationContext parameter, final StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {
		init();
		if (!loaded) {
			a = parameter;
			b = onDone;
			FromUrl f = ScriptInjector.fromUrl("https://www.google.com/recaptcha/api.js?onload=onloadCaptchaCallback&render=explicit");

			f.inject();
		}

	}

	private native void init() /*-{
		$wnd.onloadCaptchaCallback = function() {
			@com.wrupple.muba.desktop.client.activity.process.state.impl.ReCAPTCHALoader::captchaLoadedCallback();
		}
	}-*/;

	public static void captchaLoadedCallback() {
		loaded = true;
		b.setResultAndFinish(a);
	}

}
