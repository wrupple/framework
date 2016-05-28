package com.wrupple.vegetate.server.services;

import java.util.Date;

import org.apache.commons.chain.Context;

import com.wrupple.vegetate.domain.HasStakeHolder;
import com.wrupple.vegetate.domain.Person;
import com.wrupple.vegetate.domain.VegetatePeer;
import com.wrupple.vegetate.domain.VegetateServiceManifest;







public interface SessionContext extends Context,HasStakeHolder {
	
	String getPeer();
	VegetatePeer getPeerValue();
	
	Person getStakeHolderValue();
	
	<T> T getStakeHolderPrincipal(Class<T> clazz);
	
	boolean isMaster();
	
	boolean hasPermissionsToProcessContext(Context context,VegetateServiceManifest manifest);
	
	boolean isGranted(String roleCatalogMaster);

	void releaseAuthority();

	void processAccessToken(String publicKey, String accessToken, String message, String pseudoUniqueRequestId, Date date) throws SecurityException;

	boolean hasPermission(String string);

	Long getStakeHolder();
	
}
