package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasProperties;

/**
 * vegetate action passed unto vegetate oauth servlet to start a user
 * authentication action, the data contained is passed to a specific 
 * authentication realm by the processing chain
 * 
 * @author japi
 *
 */
public interface VegetateAuthenticationToken extends HasProperties{

	String CATALOG = "VegetateAuthenticationToken";
	String OAUTH_SERVICE = "auth";
	String REALM_PARAMETER = "realm";
	String ACTION_PARAMETER = "action";

	String getRealm();

	String getCallback();
	
	String getAction();

	public String getCredentials();

	public String getPrincipal();
}
