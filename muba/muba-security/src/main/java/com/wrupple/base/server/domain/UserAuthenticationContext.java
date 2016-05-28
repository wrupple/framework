package com.wrupple.base.server.domain;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.vegetate.domain.VegetateAuthenticationToken;
import com.wrupple.vegetate.server.services.RequestScopedContext;

public class UserAuthenticationContext extends ContextBase implements VegetateAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private String name,action,principal,realm,callback,oauth_token,oauth_verifier,credentials;
	List<String> properties;

	private final RequestScopedContext request;
	
	@Inject
	public UserAuthenticationContext(RequestScopedContext request) {
		super();
		request.setFormat("0");
		this.request = request;
	}
	
	public String getCallback(){
		return this.callback;
	}
	
	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}



	public String getOauth_token() {
		return oauth_token;
	}



	public void setOauth_token(String oauth_token) {
		this.oauth_token = oauth_token;
	}



	public String getOauth_verifier() {
		return oauth_verifier;
	}



	public void setOauth_verifier(String oauth_verifier) {
		this.oauth_verifier = oauth_verifier;
	}



	public String getCredentials() {
		return credentials;
	}



	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public RequestScopedContext getRequest() {
		return request;
	}

	@Override
	public String getAction() {
		return action;
	}

	@Override
	public String getPrincipal() {
		return principal;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	

}
