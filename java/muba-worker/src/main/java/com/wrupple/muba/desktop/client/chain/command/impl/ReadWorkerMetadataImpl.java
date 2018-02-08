package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.ReadWorkerMetadata;
import com.wrupple.muba.desktop.domain.ContainerContext;
import org.apache.commons.chain.Context;

public class ReadWorkerMetadataImpl implements ReadWorkerMetadata {
    @Override
    public boolean execute(ContainerContext context) throws Exception {

        /*
         if(HasStakeHolder.STAKE_HOLDER_FIELD.equals(metaTagName)){
                sm.setPrincipal(GWTUtils.eval(metaContent));
            }
         */

        return CONTINUE_PROCESSING;
    }
}
