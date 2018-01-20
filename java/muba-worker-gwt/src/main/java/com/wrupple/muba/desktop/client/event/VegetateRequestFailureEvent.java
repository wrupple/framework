package com.wrupple.muba.desktop.client.event;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.desktop.domain.overlay.JsJavaExceptionOverlay;
import com.wrupple.muba.worker.server.service.StateTransition;

public class VegetateRequestFailureEvent extends VegetateEvent {
	
	private final Throwable exception;
	private final JsJavaExceptionOverlay exceptionOverlay;
	private final StateTransition<?> callback;
	private final JavaScriptObject request;
	
	public VegetateRequestFailureEvent(int requestNumber, String channelId,String host,
			Throwable exception, JsJavaExceptionOverlay exceptionOverlay,StateTransition<?>  callback, JavaScriptObject request) {
		super(requestNumber, channelId,host);
		this.exception = exception;
		this.exceptionOverlay=exceptionOverlay;
		this.callback=callback;
		this.request=request;
	}

	@Override
	protected void dispatch(VegetateEventHandler handler) {
		handler.onRequestFailed(this);
	}

	/**
	 * @return client side generated exception
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * @return Server side retrived exception
	 */
	public JsJavaExceptionOverlay getExceptionOverlay() {
		return exceptionOverlay;
	}

	/**
	 * @return callback for unfulfilled request of an unknown type and origin
	 */
	public StateTransition<?> getCallback() {
		return callback;
	}

	/**
	 * @return vegetate action request
	 */
	public JavaScriptObject getRequest() {
		return request;
	}


}
