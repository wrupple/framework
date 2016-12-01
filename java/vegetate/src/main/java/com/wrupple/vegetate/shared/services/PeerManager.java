package com.wrupple.vegetate.shared.services;

import com.wrupple.muba.bootstrap.domain.reserved.HasTimestamp;
import com.wrupple.muba.catalogs.domain.VegetatePeer;

/**
 * @author japi
 *
 */
public interface PeerManager extends HasTimestamp {
	
	String CALLBACK_FUNCTION = "callback";
	String ACCESS_TOKEN = "access";
	String REQUEST_SALT = "salt";

	void setPrincipal(Object localPrincipal);
	/**
	 * This allows for my identity to be recognizable across several hosts
	 * 
	 * @return the namespace of my name and keys (eg: www, wrupple, twitter, organizationId)
	 */
	String getDomain();
	
	String getPublicKey();
	
	String getPrivateKey();
	
	/**
	 * in the this(www) domain wrupple.com is Wrupple
	 * in the this(183030) domain 229394 is japi@wrupple.com/183030
	 * 
	 * @param hostId
	 * @return BPM (A person as a service)peer (in the desktop and server implementation) 
	 */
	VegetatePeer getPeer(String hostId);
	String getHost();
	
	
}
