package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.worker.server.chain.command.*;
import com.wrupple.muba.worker.shared.domain.HumanApplicationContext;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collection;

@Singleton
public class PresentProblemImpl extends ChainBase<HumanApplicationContext> implements PresentProblem{

    @Inject
    public PresentProblemImpl(ConfigureView configure,AssembleInteractionPanel panel, AssembleView assemble, ToolbarAssemblyDelegate addToolbars, SetValue setValue) {
        super(Arrays.asList(configure,assemble,panel,addToolbars,setValue));
    }
}
