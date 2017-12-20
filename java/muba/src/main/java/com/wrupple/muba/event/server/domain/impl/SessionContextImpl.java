package com.wrupple.muba.event.server.domain.impl;

import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.Session;
import com.wrupple.muba.event.domain.SessionContext;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import java.util.Date;

//ShiroSessionContext little brother
public class SessionContextImpl extends ContextBase implements SessionContext {
    private static final long serialVersionUID = 2234214815822637184L;

    private Session sessionValue;

    public SessionContextImpl(Session sessionValue) {
        this.sessionValue = sessionValue;
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
