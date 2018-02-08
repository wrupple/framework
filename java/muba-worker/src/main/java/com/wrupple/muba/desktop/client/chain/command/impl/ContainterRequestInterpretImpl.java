package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.chain.command.BuildApplicationTree;
import com.wrupple.muba.desktop.client.chain.command.DeclareDependencies;
import com.wrupple.muba.desktop.client.chain.command.PopulateLoadOrder;
import com.wrupple.muba.desktop.client.chain.command.ContainterRequestInterpret;
import com.wrupple.muba.desktop.domain.DesktopRequestContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Provider;

public class ContainterRequestInterpretImpl extends ChainBase implements ContainterRequestInterpret {


    private final Provider<DesktopRequestContext> contextProvider;


    @Inject
    public ContainterRequestInterpretImpl(PopulateLoadOrder pouplate,  Provider<DesktopRequestContext> contextProvider) {
        super(new Command[]{pouplate});
        this.contextProvider = contextProvider;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) throws Exception {
        return contextProvider.get().setRuntimeContext(requestContext);
    }


}
