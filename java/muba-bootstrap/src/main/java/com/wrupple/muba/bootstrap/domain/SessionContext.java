package com.wrupple.muba.bootstrap.domain;

import java.util.Date;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.reserved.HasStakeHolder;

public interface SessionContext extends Context, HasStakeHolder {

	String getPeer();

	Host getPeerValue();

	Person getStakeHolderValue();

	<T> T getStakeHolderPrincipal(Class<T> clazz);

	boolean isMaster();

	boolean hasPermissionsToProcessContext(Context context, ServiceManifest manifest);

	boolean isGranted(String roleCatalogMaster);

	void releaseAuthority();

	void processAccessToken(String publicKey, String accessToken, String message, String pseudoUniqueRequestId,
			Date date) throws SecurityException;

	boolean hasPermission(String string);

	Long getStakeHolder();

}
