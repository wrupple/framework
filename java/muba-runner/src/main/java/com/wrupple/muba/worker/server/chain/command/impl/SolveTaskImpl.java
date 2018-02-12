package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Task;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.SolveTask;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SolveTaskImpl implements SolveTask {
    protected Logger log = LoggerFactory.getLogger(SolveTaskImpl.class);

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
        //FIXME missing methods might be in ProblemPresenterImpl

        String catalog = (String) task.getCatalog();
        String saveTo = task.getOutputField();
        List<String> userSelection = null;

        if (saveTo != null) {
            Object savedData = context.get(saveTo);

            if (savedData == null) {
               HasAccesablePropertyValues params = state.getWorkerStateValue().getParametersValue();
               if(params!=null){
                   userSelection = (List<String> ) params.getPropertyValue(saveTo);
                   if (userSelection != null) {
                       if (userSelection.isEmpty()) {
                           userSelection = null;
                       }
                   }
               }

            } else {
                log.warn("context contained task solution and delegation to runners is skipped");
                return callback.execute(context);
            }
        }
        state.setUserSelection(userSelection);



            /*
             * Runner INTERACTION REQUIRED
             */

            log.info("Thinking synchronously...");

            if (plugin.getSolver().solve(context, callback) == CONTINUE_PROCESSING) {
                return CONTINUE_PROCESSING;
            } else {
                throw new IllegalStateException("No viable solution found for problem");
            }





    }
}
