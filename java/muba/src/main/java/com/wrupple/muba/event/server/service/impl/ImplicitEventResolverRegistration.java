package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.ParentServiceManifest;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.server.chain.command.RequestInterpret;
import com.wrupple.muba.event.server.service.ImplicitEventResolver;
import org.apache.commons.chain.Command;

public abstract class ImplicitEventResolverRegistration implements ImplicitEventResolver.Registration {

    protected ImplicitEventResolverRegistration(ServiceManifest manifest) {

    }

    @Override
    public ServiceManifest getManifest() {
        return null;
    }

    @Override
    public Command getService() {
        return null;
    }

    @Override
    public RequestInterpret getContractInterpret() {
        return null;
    }

    @Override
    public ParentServiceManifest getParent() {
        return null;
    }
}
