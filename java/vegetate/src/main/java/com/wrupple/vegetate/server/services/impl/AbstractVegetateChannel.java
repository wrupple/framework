package com.wrupple.vegetate.server.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import com.wrupple.muba.catalogs.domain.ServiceManifest;
import com.wrupple.muba.catalogs.server.services.ErrorAccuser;
import com.wrupple.muba.catalogs.server.services.ObjectMapper;

public abstract class AbstractVegetateChannel<T, R> extends VegetateUrlServiceBuilder{

	private final int readTimeout;
	private final int connectionTimeOut;
	private final String requestMethod;
	private ErrorAccuser<? super T> accuser;

	

	public AbstractVegetateChannel(String host, String vegetateUrlBase, ServiceManifest manifest,ObjectMapper mapper) {
		this(host, vegetateUrlBase, "http", UrlBuilder.PORT_UNSPECIFIED, 10000, 10000, "GET", manifest,mapper);
	}

	public AbstractVegetateChannel(String host, String serviceUrl, String protocol, int port, int connectionTimeOut, int readTimeout, String requestMethod,
			ServiceManifest manifest,ObjectMapper mapper) {
		super(protocol,host,port,serviceUrl,manifest,mapper);
		this.readTimeout = readTimeout;
		this.connectionTimeOut = connectionTimeOut;
		this.requestMethod = requestMethod;
	}

	public R send(T object) throws  , NoSuchMethodException, IOException {
		try {

			URL url = buildUrl(object);

			Map<String, R> response = send(object, url);

			return response.get("0");
		} catch (Exception e) {
			if (accuser != null) {
				accuser.report(e, object);
			}
			throw e;
		}

	}

	protected Map<String, R> send(T object, URL url) throws  , NoSuchMethodException, IOException {
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(connectionTimeOut);
		connection.setReadTimeout(readTimeout);
		connection.setDoOutput(true);
		initializeConnection(connection);
		if (object != null) {
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			log.debug("[SEND OBJECT TO URL {}] {}",url,object);
			writeObject(object, writer);
			writer.close();
		}

		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

			InputStream inputStream = connection.getInputStream();
			Map<String, R> r = readObject(inputStream);
			log.debug("[URL RESPONSE] {}",r);
			return r;
		} else {
			log.warn("[CONVESARSATION FAILED]");
			throw new IllegalAccessError("CONVESARSATION FAILED :( CODE:" + connection.getResponseCode());
		}

	}

	protected void initializeConnection(HttpURLConnection connection) throws ProtocolException {
		connection.setRequestMethod(requestMethod);
	}

	

	public int getConnectionTimeOut() {
		return connectionTimeOut;
	}

	public int getReadTimeout() {
		return readTimeout;
	}
	
	public ErrorAccuser<? super T> getAccuser() {
		return accuser;
	}

	public void setAccuser(ErrorAccuser<? super T> accuser) {
		this.accuser = accuser;
	}
	

	public String getRequestMethod() {
		return requestMethod;
	}

	protected abstract void writeObject(T object, OutputStreamWriter writer) throws IOException;

	protected abstract Map<String, R> readObject(InputStream inputStream) throws IOException;

}