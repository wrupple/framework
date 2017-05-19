package com.wrupple.vegetate.server.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.domain.ServiceManifest;
import com.wrupple.muba.catalogs.domain.ServiceManifestImpl;
import com.wrupple.muba.catalogs.server.services.ObjectMapper;

public class VegetateUrlServiceBuilder {
	protected static final Logger log = LoggerFactory.getLogger(VegetateUrlServiceBuilder.class);

	private final String protocol;
	private final String host;
	private final int port;
	private final String serviceUrl;
	private ServiceManifest manifest;
	protected final ObjectMapper mapper;

	public VegetateUrlServiceBuilder(String protocol, String host, int port, String serviceUrl, ServiceManifest manifest, ObjectMapper mapper) {
		super();
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.serviceUrl = serviceUrl;
		this.manifest = manifest;
		this.mapper = mapper;
	}

	protected final String encoding = "UTF-8";

	public URL buildUrl(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IllegalAccessError, IOException {

		UrlBuilder urlbuilder = new UrlBuilder();
		urlbuilder.setProtocol(protocol);

		if (object == null) {
			urlbuilder.setPath(this.serviceUrl);
		} else {
			String path = buildPath(object,null);

			urlbuilder.setPath(path);
		}

		urlbuilder.setPort(port);
		urlbuilder.setHost(host);

		URL regreso = new URL(urlbuilder.buildString());
		return regreso;
	}


	public void writeRelativeUrl(Object object, PrintWriter writer) {
		writer.print('/');
		
	}

	private String buildPath(Object object,PrintWriter writer) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IllegalAccessError, IOException {
		if (manifest == null) {
			manifest = getServiceManifest();
		}
		log.trace("[deduce object path with manifest {}] {}",manifest,object);
		String[] addressTokens = manifest.getGrammar();
		StringBuilder builder;
		if(writer==null){
			builder = new StringBuilder(serviceUrl.length() + addressTokens.length * 10);
		}else{
			builder=null;
		}
		
		if(writer==null){
			writer.print(this.serviceUrl);
		}else{
			builder.append(this.serviceUrl);
		}
		

		String tokenValue;
		for (String token : addressTokens) {
			tokenValue = BeanUtils.getSimpleProperty(object, token);
			if (tokenValue == null) {
				break;
			} else {
				if(writer==null){
					builder.append('/');
					builder.append(tokenValue);
				}else{
					writer.print('/');
					writer.append(tokenValue);
				}
				
			}
		}
		String r =builder.toString();
		log.trace("[built path] {}",r);
		return r;
	}

	private ServiceManifest getServiceManifest()
			throws IllegalAccessError, IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		URL url = buildUrl(null);
		log.trace("[get service manifest] {}",url);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

			InputStream inputStream = connection.getInputStream();

			ServiceManifest response = readServiceManifest(inputStream);
			
			log.trace("[ServiceManifest] {}",url,response);
			
			return response;
		} else {
			throw new IllegalAccessError(" Access Exception :( CODE:" + connection.getResponseCode());
		}
	}

	protected ServiceManifest readServiceManifest(InputStream inputStream) throws IOException {
		return mapper.readValue(inputStream, ServiceManifestImpl.class);
	}

	static class UrlBuilder {

		/**
		 * The port to use when no port should be specified.
		 */
		public static final int PORT_UNSPECIFIED = Integer.MIN_VALUE;

		/**
		 * A mapping of query parameters to their values.
		 */

		private String protocol = null;
		private String host = null;
		private int port = PORT_UNSPECIFIED;
		private String path = null;
		private String hash = null;

		/**
		 * Build the URL and return it as an encoded string.
		 * 
		 * @return the encoded URL string
		 */
		public String buildString() {
			StringBuilder url = new StringBuilder();

			if (protocol != null) {
				// http://
				url.append(protocol).append("://");
			}

			// http://www.wrupple.com
			if (host != null) {
				url.append(host);
			}

			// http://www.wrupple.com:80
			if (port != PORT_UNSPECIFIED) {
				url.append(":").append(port);
			}

			// http://www.wrupple.com:80/path/to/file.html
			if (path != null && !"".equals(path)) {
				url.append("/").append(path);
			}


			// http://www.wrupple.com:80/path/to/file.html?k0=v0&k1=v1#token
			if (hash != null) {
				url.append("#").append(hash);
			}

			return url.toString();
		}


		/**
		 * Set the hash portion of the location (ex. myAnchor or #myAnchor).
		 * 
		 * @param hash
		 *            the hash
		 */
		public UrlBuilder setHash(String hash) {
			if (hash != null && hash.startsWith("#")) {
				hash = hash.substring(1);
			}
			this.hash = hash;
			return this;
		}

		/**
		 * Set the host portion of the location (ex. google.com). You can also
		 * specify the port in this method (ex. localhost:8888).
		 * 
		 * @param host
		 *            the host
		 */
		public UrlBuilder setHost(String host) {
			// Extract the port from the host.
			if (host != null && host.contains(":")) {
				String[] parts = host.split(":");
				if (parts.length > 2) {
					throw new IllegalArgumentException("Host contains more than one colon: " + host);
				}
				try {
					setPort(Integer.parseInt(parts[1]));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Could not parse port out of host: " + host);
				}
				host = parts[0];
			}
			this.host = host;
			return this;
		}


		/**
		 * Set the path portion of the location (ex. path/to/file.html).
		 * 
		 * @param path
		 *            the path
		 */
		public UrlBuilder setPath(String path) {
			if (path != null && path.startsWith("/")) {
				path = path.substring(1);
			}
			this.path = path;
			return this;
		}

		/**
		 * Set the port to connect to.
		 * 
		 * @param port
		 *            the port, or {@link #PORT_UNSPECIFIED}
		 */
		public UrlBuilder setPort(int port) {
			this.port = port;
			return this;
		}

		/**
		 * Set the protocol portion of the location (ex. http).
		 * 
		 * @param protocol
		 *            the protocol
		 */
		public UrlBuilder setProtocol(String protocol) {
			assertNotNull(protocol, "Protocol cannot be null");
			if (protocol.endsWith("://")) {
				protocol = protocol.substring(0, protocol.length() - 3);
			} else if (protocol.endsWith(":/")) {
				protocol = protocol.substring(0, protocol.length() - 2);
			} else if (protocol.endsWith(":")) {
				protocol = protocol.substring(0, protocol.length() - 1);
			}
			if (protocol.contains(":")) {
				throw new IllegalArgumentException("Invalid protocol: " + protocol);
			}
			assertNotNullOrEmpty(protocol, "Protocol cannot be empty", false);
			this.protocol = protocol;
			return this;
		}

		/**
		 * Assert that the value is not null.
		 * 
		 * @param value
		 *            the value
		 * @param message
		 *            the message to include with any exceptions
		 * @throws IllegalArgumentException
		 *             if value is null
		 */
		private void assertNotNull(Object value, String message) throws IllegalArgumentException {
			if (value == null) {
				throw new IllegalArgumentException(message);
			}
		}

		/**
		 * Assert that the value is not null or empty.
		 * 
		 * @param value
		 *            the value
		 * @param message
		 *            the message to include with any exceptions
		 * @param isState
		 *            if true, throw a state exception instead
		 * @throws IllegalArgumentException
		 *             if value is null
		 * @throws IllegalStateException
		 *             if value is null and isState is true
		 */
		private void assertNotNullOrEmpty(String value, String message, boolean isState) throws IllegalArgumentException {
			if (value == null || value.length() == 0) {
				if (isState) {
					throw new IllegalStateException(message);
				} else {
					throw new IllegalArgumentException(message);
				}
			}
		}
	}

	public String getProtocol() {
		return protocol;
	}

	public int getPort() {
		return port;
	}

	public String getHost() {
		return host;
	}

	public String getVegetateUrlBase() {
		return serviceUrl;
	}

}
