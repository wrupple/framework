package com.wrupple.muba.desktop.server;

import com.wrupple.muba.desktop.client.chain.ContainerRequestEngine;
import com.wrupple.muba.desktop.domain.ContainerRequestManifest;
import com.wrupple.muba.desktop.server.chain.command.DesktopResponseWriter;
import com.wrupple.muba.desktop.server.domain.impl.DesktopRequestContextImpl;
import com.wrupple.muba.desktop.server.service.DesktopRequestTokenizer;
import com.wrupple.vegetate.server.VegetateServlet;
import com.wrupple.vegetate.server.services.RequestScopedContext;
import com.wrupple.vegetate.server.services.SessionContext;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class DesktopServlet extends VegetateServlet<DesktopRequestContextImpl> {

    /**
     *
     */
    private static final long serialVersionUID = 1125078203927802079L;

    @Inject
    public DesktopServlet(DesktopResponseWriter writer, Provider<DesktopRequestTokenizer> requestTokenizer, Provider<ContainerRequestEngine> commandProvider,
                          Provider<SessionContext> usipp, ContainerRequestManifest manifest, Provider<RequestScopedContext> rsc) {
        super(writer, requestTokenizer, commandProvider, usipp, manifest, rsc);
    }

}
