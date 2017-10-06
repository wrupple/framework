package com.wrupple.muba.event.server.domain.impl;

import java.util.Date;

import com.wrupple.muba.event.domain.*;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

//ShiroSessionContext little brother
public class SessionContextImpl extends ContextBase implements SessionContext {
	private static final long serialVersionUID = 2234214815822637184L;

	private Session sessionValue;

    public SessionContextImpl(Session sessionValue) {
        this.sessionValue=sessionValue;
    }

    @Override
	public <T> T getStakeHolderPrincipal(Class<T> clazz) {
		return null;
	}
	@Override
	public boolean hasPermissionsToProcessContext(Context context, ServiceManifest manifest) {
		return true;
	}
	@Override
	public boolean isGranted(String roleCatalogMaster) {
		return true;
	}
	@Override
	public void releaseAuthority() {

	}
	@Override
	public void processAccessToken(String publicKey, String accessToken, String message, String pseudoUniqueRequestId,
			Date date) throws SecurityException {

	}
	@Override
	public boolean hasPermission(String string) {
		return true;
	}

    @Override
    public void setStakeHolder(Long stakeHolder) {

        sessionValue.setStakeHolder(stakeHolder);
    }


    @Override
    public Session getSessionValue() {
        return sessionValue;
    }

    public void setSessionValue(Session sessionValue) {
        this.sessionValue = sessionValue;
    }
}
