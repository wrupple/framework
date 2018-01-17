package com.wrupple.muba.desktop.server.service;

import com.wrupple.vegetate.domain.VegetateServiceManifest;
import org.apache.commons.chain.Context;

public interface FailedPermissionCheckRecovery {
    boolean attemptToRecoverFromPermissionCheck(Context context, VegetateServiceManifest manifest);
}
