package com.wrupple.vegetate.client.services.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.event.NewVegetateRequestEvent;
import com.wrupple.muba.desktop.client.event.VegetateRequestFailureEvent;
import com.wrupple.muba.desktop.client.event.VegetateRequestSuccessEvent;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.client.services.logic.SerializationService;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsJavaExceptionOverlay;
import com.wrupple.muba.desktop.domain.overlay.JsSignatureGenerator;
import com.wrupple.muba.desktop.domain.overlay.JsVegetateServiceManifest;
import com.wrupple.muba.desktop.domain.overlay.JsonVegetateResponse;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.vegetate.client.services.VegetateChannel;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.shared.services.PeerManager;
import com.wrupple.vegetate.shared.services.SignatureGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleVegetateChannel<T extends JavaScriptObject, R extends JavaScriptObject> implements VegetateChannel<T, R> {

	class JavaScriptCallback implements RequestCallback, AsyncCallback<JsonVegetateResponse> {

		private final List<ActionRequest<T, R>> qualifiedActions;

		public JavaScriptCallback(List<ActionRequest<T, R>> qualifiedActions) {
			this.qualifiedActions = qualifiedActions;
		}

		@Override
		public void onResponseReceived(Request request, Response response) {
			// standard JSON response contract is JsonVegetateResponse, each
			// response object is named after the parameter that spawned it

			String json = response.getText();
			JsonVegetateResponse result = null;
			try {
				result = serializer.deserialize(json);
			} catch (Exception e) {
				onFailure(e);
			}
			onSuccess(result);

		}

		@Override
		public void onError(Request request, Throwable exception) {
			onFailure(exception);
		}

		@Override
		public void onSuccess(JsonVegetateResponse result) {
			ActionRequest<T, R> request;
			JavaScriptObject rawPartialResult;
			R partialResult;
			String parameterValue;
			JsJavaExceptionOverlay exception;
			for (int i = 0; i < qualifiedActions.size(); i++) {
				request = qualifiedActions.get(i);
				if (ignoreVegetateContract_AndReturnResponseObjectAsIs) {
					request.callback.setResultAndFinish((R) result);
					ignoreVegetateContract_AndReturnResponseObjectAsIs = false;
					eventBus.fireEvent(new VegetateRequestSuccessEvent(host,request.requestNumber, servicePath, result));
				} else {
					parameterValue = String.valueOf(i);
					rawPartialResult = result.getNamedResult(parameterValue);
					if (rawPartialResult == null) {
						GWT.log("Response contained no data for inlined request '" + parameterValue + "'");
						eventBus.fireEvent(new VegetateRequestSuccessEvent(host,request.requestNumber, servicePath, null));
					} else {
						partialResult = rawPartialResult.cast();
						exception = isException(partialResult);
						if (exception == null) {
							try {
								request.callback.setResultAndFinish(partialResult);
							} catch (Exception e) {
								GWT.log("Callback failed ", e);
							} finally {
								eventBus.fireEvent(new VegetateRequestSuccessEvent(host,request.requestNumber, servicePath, partialResult));
							}
						} else {
							eventBus.fireEvent(
									new VegetateRequestFailureEvent(request.requestNumber, servicePath, host,null, exception, request.callback, request.object));
						}

					}
				}

			}

		}

		private JsJavaExceptionOverlay isException(R partialResult) {
			JsJavaExceptionOverlay overlay = partialResult.cast();
			if (overlay.getStackTrace() == null) {
				return null;
			} else {
				return overlay;
			}
		}

		@Override
		public void onFailure(Throwable caught) {
			// ALL OF THEM FAILED! since the request failed
			ActionRequest<T, R> request;
			for (int i = 0; i < qualifiedActions.size(); i++) {
				request = qualifiedActions.get(i);
				eventBus.fireEvent(new VegetateRequestFailureEvent(request.requestNumber, servicePath,host, caught, null, request.callback, request.object));
			}

		}

	}

	class RemoveFromQueue extends DataCallback<R> {

		private String key;

		public RemoveFromQueue(String key) {
			this.key = key;
		}

		@Override
		public void execute() {
			urlThrowttleMap.remove(key);
		}

	}

	private static class ActionRequest<T, R> {
		final StateTransition<R> callback;
		final T object;
		final String serializedObject;
		private final int requestNumber;

		public ActionRequest(StateTransition<R> callback, T object, String serializedObject, int requestCount) {
			super();
			this.callback = callback;
			this.object = object;
			this.serializedObject = serializedObject;
			this.requestNumber = requestCount;
		}

	}

	private final JsVegetateServiceManifest manifest;
	// ignored if using jsonP (jsonp always uses GET)
	private final Method method;
	protected final String servicePath;
	private final SerializationService<T, JsonVegetateResponse> serializer;
	private final String protocol;
	private final String host;
	private final int port;
	protected final EventBus eventBus;
	protected final JavaScriptObject properties;
	private final UrlBuilder urlbuilder;
	private final String publicKey;
	private final String privateKey;
	private final SignatureGenerator signer;

	private int usedPort;
	private String usedHost;
	/*
	 * Bottle neck
	 */
	private final JsArray<T> tactions;
	private final List<StateTransition<R>> callbackst;
	private boolean ignoreVegetateContract_AndReturnResponseObjectAsIs;
	
	private static int requestCount = 0;
	private final static Map<String, StateTransition<?>> urlThrowttleMap = new HashMap<String, StateTransition<?>>();

	public SimpleVegetateChannel(VegetateServiceManifest manifest, EventBus bus, String protocol, Method method, String servicePath,
			SerializationService<T, JsonVegetateResponse> serializer, JavaScriptObject properties, String host, int port, String publicKey, String privateKey) {
		super();
		
		//TODO peer manager knows the stake holder , why not just pass it?
		this.signer=JsSignatureGenerator.create(null, publicKey, privateKey);
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		this.manifest = (JsVegetateServiceManifest) manifest;
		this.eventBus = bus;
		this.method = method;
		this.servicePath = servicePath;
		this.serializer = serializer;
		this.port = port;
		this.host = host;
		this.protocol = protocol;
		this.properties = properties;
		urlbuilder = new UrlBuilder();
		tactions = JavaScriptObject.createArray().cast();
		callbackst = new ArrayList<StateTransition<R>>();
	}

	@Override
	public String buildServiceUrl(T object) {
		JsArrayString addessTokens = manifest.getUrlPathParametersArray();
		buildUrl(addessTokens, object);
		String requestUrl = urlbuilder.buildString();
		return requestUrl;
	}

	@Override
	public void send(T object, StateTransition<R> callback) {

		tactions.push(object);
		callbackst.add(callback);
	}

	@Override
	public void flush() {
		if (tactions.length() > 0) {
			try {
				flush(tactions, callbackst);
			} catch (Exception e) {
				GWT.log("unable tu flush channel", e);
			} finally {
				tactions.setLength(0);
				callbackst.clear();
			}
		}
	}

	@Override
	public void getServiceManifest(DataCallback<JsVegetateServiceManifest> dataCallback) {
		send(null, (StateTransition) dataCallback);
		ignoreVegetateContract_AndReturnResponseObjectAsIs = true;
		flush();
	}

	private void flush(JsArray<T> actions, final List<StateTransition<R>> callbacks) throws Exception {
		JsArrayString addessTokens = manifest.getUrlPathParametersArray();

		T object;
		String requestUrl = null;
		String serializedObject;
		String throwttleKey;
		StateTransition<?> transitionQueue;
		StateTransition<R> callback;

		List<ActionRequest<T, R>> qualifiedActions = new ArrayList<ActionRequest<T, R>>(callbacks.size());
		int bufferSize = 0;
		for (int i = 0; i < actions.length(); i++) {
			callback = callbacks.get(i);
			object = actions.get(i);

			serializedObject = object == null ? null : serializer.serialize(object).trim();

			throwttleKey = this.host + this.port + manifest.getServiceName() + serializedObject;
			transitionQueue = urlThrowttleMap.get(throwttleKey);

			if (transitionQueue == null) {
				urlThrowttleMap.put(throwttleKey, callback);
				StateTransition<R> removeFromQueue = new RemoveFromQueue(throwttleKey);
				callback.hook(removeFromQueue);
				if (serializedObject != null) {
					bufferSize += serializedObject.length();
				}
				requestCount++;
				if (qualifiedActions.size() == 0) {
					buildUrl(addessTokens, object);
					requestUrl = urlbuilder.buildString();
					requestUrl = requestUrl.trim();
				}
				qualifiedActions.add(new ActionRequest<T, R>(callback, object, serializedObject, requestCount));
			} else {
				StateTransition<R> cast = (StateTransition) transitionQueue;
				cast.hook(callback);
			}

		}
		if (requestUrl != null) {
			privateSend(requestUrl, bufferSize, qualifiedActions);
		}
	}

	private void privateSend(String url, int bufferSize, List<ActionRequest<T, R>> qualifiedActions) throws RequestException {

		boolean inliningObjects = qualifiedActions.size() > 1;

		JavaScriptCallback requestCallback = new JavaScriptCallback(qualifiedActions);
		int urlPort;
		String portString = Window.Location.getPort();
		if (portString.isEmpty()) {
			urlPort = UrlBuilder.PORT_UNSPECIFIED;
		} else {
			urlPort = Integer.parseInt(portString);
		}

		if (this.usedHost.equals(Window.Location.getHostName()) && this.usedPort == urlPort) {
			RequestBuilder post = new RequestBuilder(method, url);
			post.setHeader("Content-type", "application/x-www-form-urlencoded");
			StringBuilder postData = new StringBuilder(bufferSize + (qualifiedActions.size() * 2));
			ActionRequest<T, R> request;
			int notNullPropertyCount = 0;
			for (int i = 0; i < qualifiedActions.size(); i++) {
				request = qualifiedActions.get(i);
				if (inliningObjects) {
					// every action must have a parameter name , so url tokens
					// dont get written on the wrong excecution context
					if (i > 0) {
						postData.append("&");
					}
					postData.append(i);
					postData.append("=");
					if (request.serializedObject == null) {
						postData.append("{}");
					} else {
						postData.append(URL.encodeQueryString(request.serializedObject));
					}
				} else {
					// attempt to remove empty action (theres only one anyways)
					notNullPropertyCount = countNonNullProperties(request.object);
					if (notNullPropertyCount > 0) {
						postData.append(i);
						postData.append("=");
						postData.append(URL.encodeQueryString(request.serializedObject));
					}
				}

				eventBus.fireEvent(new NewVegetateRequestEvent(host,request.requestNumber, servicePath, request.object, manifest, url));
			}
			modifyPostData(postData, urlbuilder, post);
			post.setRequestData(postData.toString());
			post.setCallback(requestCallback);
			post.send();
		} else {
			
			JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
			modifyExternalRequest(urlbuilder);
			ActionRequest<T, R> request;
			int notNullPropertyCount = 0;
			String signature=null;
			for (int i = 0; i < qualifiedActions.size(); i++) {
				request = qualifiedActions.get(i);
				if (inliningObjects) {
					// every action must have a parameter name , so url tokens
					// dont get written on the wrong excecution context
					if (request.serializedObject == null) {
						urlbuilder.setParameter(String.valueOf(i), "{}");
					} else {
						urlbuilder.setParameter(String.valueOf(i), request.serializedObject);
					}
				} else {
					// attempt to remove empty action (theres only one anyways)
					notNullPropertyCount = countNonNullProperties(request.object);
					if (notNullPropertyCount > 0) {
						urlbuilder.setParameter(String.valueOf(i), request.serializedObject);
					}
				}
				if(i==0 && privateKey!=null&& publicKey!=null ){
					signature = signer.generateSignature(request.serializedObject);
				}

				eventBus.fireEvent(new NewVegetateRequestEvent(host,request.requestNumber, servicePath, request.object, manifest, url));
			}
			if(privateKey!=null&& publicKey!=null){
				urlbuilder.setParameter(PeerManager.PUBLIC_KEY, publicKey);
				urlbuilder.setParameter(PeerManager.ACCESS_TOKEN, signature);
                //sign all jsonp requests (assume comunication setRuntimeContext direct host is authenticated in another way

            }
			url = urlbuilder.buildString();
			jsonp.setCallbackParam(PeerManager.CALLBACK_FUNCTION);
			jsonp.requestObject(url, requestCallback);
		}
	}

	private native int countNonNullProperties(T source) /*-{
														var sourceValue;
														var count = 0;
														var arr = [];
														for ( var k in source) {
														sourceValue = source[k];
														if (sourceValue == null) {
														arr.push(k);
														} else {
														count++;
														}
														}
														for (var i = 0; i < arr.length; i++) {
														delete source[arr[i]];
														}
														return count;
														}-*/;

	public static native void copyAllProperties(JavaScriptObject target,
			JavaScriptObject source) /*-{
										
										}-*/;

	protected void modifyPostData(StringBuilder postData, UrlBuilder urlbuilder, RequestBuilder post) {

	}

	protected void modifyExternalRequest(UrlBuilder urlbuilder) {

	}

	private void buildUrl(JsArrayString addressTokens, T object) {
		if (protocol != null) {
			urlbuilder.setProtocol(protocol);
		}
		String urlHost;
		int urlPort;
		if (host == null) {
			urlHost = Window.Location.getHostName();
			// PORT SETTINGS IGNORED IF HOST NOT SPECIFIED
			String portString = Window.Location.getPort();
			if (portString.isEmpty()) {
				urlPort = UrlBuilder.PORT_UNSPECIFIED;
			} else {
				urlPort = Integer.parseInt(portString);
			}
		} else {
			urlHost = host;
			urlPort = port;
		}

		if (object == null) {
			urlbuilder.setPath(this.servicePath + "/");
		} else {
			String path = buildPath(addressTokens, object);

			urlbuilder.setPath(path);
		}
		this.usedPort = urlPort;
		this.usedHost = urlHost;
		urlbuilder.setPort(urlPort);
		urlbuilder.setHost(urlHost);
	}

	private String buildPath(JsArrayString addressTokens, T object) {
		StringBuilder builder = new StringBuilder(servicePath.length() + addressTokens.length() * 10);
		builder.append(this.servicePath);
		String token;
		String tokenValue;
		for (int i = 0; i < addressTokens.length(); i++) {
			token = addressTokens.get(i);
			builder.append('/');
			tokenValue = GWTUtils.getAttribute(object, token);
			if (tokenValue == null) {
				break;
			} else {
				// DON'T delete attributes since we might use them for a request
				// retry
				// JSOHelper.deleteAttribute(object, token);
				builder.append(tokenValue);
			}
		}
		return builder.toString();
	}

	public String getHost() {
		return host;
	}

	public Method getMethod() {
		return method;
	}

	public String getVegetateUrlBase() {
		return servicePath;
	}

	public int getPort() {
		return port;
	}

	public String getProtocol() {
		return protocol;
	}

	public JavaScriptObject getProperties() {
		return properties;
	}

	public JsVegetateServiceManifest getManifest() {
		return manifest;
	}

	public static void getServiceManifest(Method method, String protocol, String host, int port, String servicePath,
			final StateTransition<JsVegetateServiceManifest> callback) throws RequestException {
		int localPort;
		String localHost = Window.Location.getHostName();
		String portString = Window.Location.getPort();
		if (portString.isEmpty()) {
			localPort = UrlBuilder.PORT_UNSPECIFIED;
		} else {
			localPort = Integer.parseInt(portString);
		}
		UrlBuilder urlbuilder = new UrlBuilder();
		urlbuilder.setHost(host);
		urlbuilder.setPath(servicePath);
		urlbuilder.setPort(port);
		urlbuilder.setProtocol(protocol);

		final String url = urlbuilder.buildString();

		if ((host == null || host.equals(localHost)) && port == localPort) {
			RequestBuilder post = new RequestBuilder(method, url);
			post.setHeader("Content-type", "application/x-www-form-urlencoded");
			post.setCallback(new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					String json = response.getText();
					JavaScriptObject raw = GWTUtils.eval(json);
					JsVegetateServiceManifest manifest = raw.cast();
					callback.setResultAndFinish(manifest);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					GWT.log("unable to get service manifest at " + url, exception);

				}
			});
			post.send();
		} else {
			JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
			jsonp.requestObject(url, new AsyncCallback<JsVegetateServiceManifest>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("unable to get service manifest at " + url, caught);
				}

				@Override
				public void onSuccess(JsVegetateServiceManifest result) {
					callback.setResultAndFinish(result);
				}
			});
		}
	}
}
