package com.wrupple.base.server.domain;

import java.util.List;

import com.wrupple.vegetate.domain.VegetateAuthenticationToken;

public class VegetateAuthenticationTokenImpl implements VegetateAuthenticationToken {
	
	private static final long serialVersionUID = 6567614756366222091L;
	private Long domain,id;
	private boolean anonymouslyVisible;
	private String name,realm,callback,oauth_token,oauth_verifier,credentials,action;
	private List<String> properties;

	public VegetateAuthenticationTokenImpl() {
	}

	

	public Long getDomain() {
		return domain;
	}



	public void setDomain(Long domain) {
		this.domain = domain;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public boolean isAnonymouslyVisible() {
		return anonymouslyVisible;
	}



	public void setAnonymouslyVisible(boolean anonymouslyVisible) {
		this.anonymouslyVisible = anonymouslyVisible;
	}



	public String getName() {
		return name;
	}
	
	public String getPrincipal(){
		return getName();
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



	public String getCatalog() {
		return CATALOG;
	}


	public String getIdAsString() {
		return String.valueOf(getId());
	}

	public void setIdAsString(String id) {
		this.id=Long.parseLong(id);
	}



	public String getImage() {
		return null;
	}



	public String getCallback() {
		return callback;
	}



	public void setCallback(String callback) {
		this.callback = callback;
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



	@Override
	public String getAction() {
		return action;
	}



	public void setAction(String action) {
		this.action = action;
	}





}
