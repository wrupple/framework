package com.wrupple.vegetate.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasTimestamp;
import com.wrupple.muba.catalogs.domain.CatalogKey;

public interface PeerAuthenticationToken extends HasTimestamp,CatalogKey {

    final String MAIN_PARAMETER = "0";

	/**
     * Returns the account identity submitted  to the authentication process.


     *
     * @return the account identity submitted during the authentication process.
     * @see UsernamePasswordToken
     */
    String getPrincipal();

    /**
     * Returns the credentials submitted by the user during the authentication process that verifies
     * the submitted {@link #getPrincipal() account identity}.
     *
     * recomended approach is to use a private key to generate a digested signature of the sent message and send it as credential
     *
     * @return the credential submitted by the user during the authentication process.
     */
    String getCredentials();
    
	/* 
	 * unique token id
	 */
	String getId();
    
	public void setPrincipal(String principal);

	public void setCredentials(String credentials);

	public String getRawMessage() ;

	public void setRawMessage(String rawMessage) ;
}
