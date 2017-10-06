package com.wrupple.muba.event.domain;

import java.util.Date;

import org.apache.commons.chain.Context;

import com.wrupple.muba.event.domain.reserved.HasStakeHolder;

public interface SessionContext extends Context {

	Session getSessionValue();

	<T> T getStakeHolderPrincipal(Class<T> clazz);

	boolean hasPermissionsToProcessContext(Context context, ServiceManifest manifest);

	boolean isGranted(String roleCatalogMaster);

	void releaseAuthority();

	void processAccessToken(String publicKey, String accessToken, String message, String pseudoUniqueRequestId,
			Date date) throws SecurityException;

	boolean hasPermission(String string);


    void setStakeHolder(Long stakeHolder);
}
