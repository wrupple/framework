package com.wrupple.vegetate.domain;

import java.util.List;

/**
 * vegetate action passed unto vegetate oauth servlet to start a user
 * authentication action, the data contained is passed to a specific 
 * authentication realm by the processing chain
 * 
 * @author japi
 *
 */
public interface VegetateAuthenticationToken {

	String CATALOG = "VegetateAuthenticationToken";
	String OAUTH_SERVICE = "auth";
	String REALM_PARAMETER = "realm";
	String ACTION_PARAMETER = "action";

	String getRealm();

	String getCallback();
	
	String getAction();

	List<String> getProperties();

	public String getCredentials();

	public String getPrincipal();
}
