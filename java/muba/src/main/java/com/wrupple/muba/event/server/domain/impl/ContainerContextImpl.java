package com.wrupple.muba.event.server.domain.impl;

import com.wrupple.muba.event.domain.Container;
import com.wrupple.muba.event.domain.ContainerContext;
import com.wrupple.muba.event.domain.ServiceManifest;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import java.util.Date;

//ShiroSessionContext little brother
public class ContainerContextImpl extends ContextBase implements ContainerContext {
    private static final long serialVersionUID = 2234214815822637184L;

    private Container containerValue;

    public ContainerContextImpl(Container containerValue) {
        this.containerValue = containerValue;
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

        containerValue.setStakeHolder(stakeHolder);
    }


    @Override
    public Container getContainerValue() {
        return containerValue;
    }

    public void setContainerValue(Container containerValue) {
        this.containerValue = containerValue;
    }
}
