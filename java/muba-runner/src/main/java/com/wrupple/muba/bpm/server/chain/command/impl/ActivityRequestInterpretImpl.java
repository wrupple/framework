package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.chain.command.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by japi on 11/05/17.
 */
public class ActivityRequestInterpretImpl extends ChainBase implements ActivityRequestInterpret {

    private final Provider<ApplicationContext> activityContextProvider;


    @Inject
    public ActivityRequestInterpretImpl(
                                        Provider<ApplicationContext> activityContextProvider,
                                        // 1. Create a Model inside plugin the context references it by dn (InitializeActivityContext)
                                        LoadTask load
                                        ){
        super(new Command []{
                load
        });

        this.activityContextProvider=activityContextProvider;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {
        ApplicationContext r = activityContextProvider.get();
        r.setExcecutionContext(requestContext);
        return r;
    }

}
