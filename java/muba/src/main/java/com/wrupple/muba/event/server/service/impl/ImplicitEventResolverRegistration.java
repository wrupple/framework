package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.Contract;
import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.ParentServiceManifest;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;
import com.wrupple.muba.event.server.chain.command.RequestInterpret;
import com.wrupple.muba.event.server.service.ImplicitEventResolver;
import org.apache.commons.chain.Command;

import java.util.Arrays;

public abstract class ImplicitEventResolverRegistration implements ImplicitEventResolver.Registration {

    private final ServiceManifest manifest;
    private final Command service;
    private final RequestInterpret contractInterpret;
    private final ParentServiceManifest parent;

    protected ImplicitEventResolverRegistration(ServiceManifest manifest, Command engine, RequestInterpret interpret, ParentServiceManifest parent) {
        this.manifest = manifest;
        this.service = engine;
        this.contractInterpret = interpret;
        this.parent = parent;
    }

    protected ImplicitEventResolverRegistration(String distinguishedName, ContractDescriptor contract, Command engine, RequestInterpret interpret, ParentServiceManifest parent,String... grammar) {
        this.manifest = new ServiceManifestImpl(distinguishedName, "1.0", contract, Arrays.asList(grammar));
        this.service = engine;
        this.contractInterpret = interpret;
        this.parent = parent;
    }

    @Override
    public ServiceManifest getManifest() {
        return manifest;
    }

    @Override
    public Command getService() {
        return service;
    }

    @Override
    public RequestInterpret getContractInterpret() {
        return contractInterpret;
    }

    @Override
    public ParentServiceManifest getParent() {
        return parent;
    }
}
