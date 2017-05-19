package com.wrupple.muba.desktop.server.service;

import org.apache.commons.chain.Context;

import com.wrupple.vegetate.domain.VegetateServiceManifest;

public interface FailedPermissionCheckRecovery {
	boolean attemptToRecoverFromPermissionCheck(Context context, VegetateServiceManifest manifest);
}
