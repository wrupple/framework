package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.server.chain.command.LoadTask;
import org.apache.commons.chain.Context;

/**
 * Created by rarl on 11/05/17.
 */
public class LoadTaskImpl implements LoadTask {
    @Override
    public boolean execute(Context ctx) throws Exception {
        ExcecutionContext requestContext = (ExcecutionContext) ctx;
        ActivityContext context = requestContext.getServiceContext();
        ProcessTaskDescriptor request = context.getTaskDescriptorValue();
        if(request==null){
            request = (ProcessTaskDescriptor) requestContext.getServiceContract();
            if(request==null){
                //TODO task plugin is used as a shorthand for the more verbose catalog engine

                //TODO get task descriptor (from token¡?)
                throw new NullPointerException("there is no task definition");
            }
            context.setTaskDescriptorValue(request);
        }


        return CONTINUE_PROCESSING;
    }
}
