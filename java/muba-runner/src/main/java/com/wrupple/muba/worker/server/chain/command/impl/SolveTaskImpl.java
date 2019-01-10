package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.SolveTask;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SolveTaskImpl implements SolveTask {
    protected Logger log = LogManager.getLogger(SolveTaskImpl.class);

    private final ProcessManager plugin;
    private final Callback callback;

    @Inject
    public SolveTaskImpl(ProcessManager plugin, Callback callback) {
        this.plugin = plugin;
        this.callback = callback;
    }
    
    @Override
    public boolean execute(Context ctx) throws Exception {
        ApplicationContext context = (ApplicationContext) ctx;
        ApplicationState state = context.getStateValue();
        Task task = state.getTaskDescriptorValue();


        /*
             * Runner INTERACTION REQUIRED
             */

            log.info("Solving {} ", task.getDistinguishedName());

            if (plugin.getSolver().solve(context, callback) == CONTINUE_PROCESSING) {
                //NEVER write code HERE: call back mechanism does not act as expected on sync threads
                return CONTINUE_PROCESSING;
            } else {
                throw new IllegalStateException("No viable solution found for problem");
            }





    }


}
