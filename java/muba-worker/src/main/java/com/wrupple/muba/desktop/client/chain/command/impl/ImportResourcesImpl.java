package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.ImportResources;
import com.wrupple.muba.desktop.client.chain.command.ImportResourcesCallback;
import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import com.wrupple.muba.desktop.domain.DependencyImportContext;
import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.ApplicationDependency;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.server.service.impl.ForkCallback;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.generic.LookupCommand;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ImportResourcesImpl  implements ImportResources {


    private final com.wrupple.muba.worker.server.service.impl.Callback<DependencyImportContext> forkCallback;
    private final ImportResourcesCallback mainCallback;

    private final LookupCommand importHandler;

    @Inject
    public ImportResourcesImpl(final ImportResourcesCallback importResourcesCallback, CatalogFactory catalogFactory, @Named("worker.importHandler.catalog") String catalogName/*like JavaScriptImportCommand*/,@Named("worker.importHandler.discriminatingKey") String contextKey) {


        importHandler = new LookupCommand(catalogFactory);

        this.mainCallback = importResourcesCallback;
        this.forkCallback = new com.wrupple.muba.worker.server.service.impl.Callback<DependencyImportContext>(new Command<DependencyImportContext>() {
            @Override
            public boolean execute(DependencyImportContext context) throws Exception {
                return importResourcesCallback.execute(context.getApplicationSwitchContext());
            }
        });
        importHandler.setCatalogName(catalogName);
        importHandler.setOptional(false);
        importHandler.setNameKey(contextKey);
    }



    @Override
    public boolean execute(ContextSwitchRuntimeContext context) throws Exception {
         /*
          *  Load transaction data
		  */

        ForkCallback<DependencyImportContext> fork = new ForkCallback<DependencyImportContext>(forkCallback);

        final Application applicationItem = (Application) context.getContextSwitch().getWorkerStateValue().getStateValue().getApplicationValue();

        List<ApplicationDependency> actions = applicationItem.getDependenciesValues();

        int forkPaths = actions == null ? 0 : actions.size();

        if(forkPaths>0){

            List<StateTransition<DependencyImportContext>> callbacks = new ArrayList<>(forkPaths);

            for (int i = 0 ; i< forkPaths; i++) {
                callbacks.add(fork.fork());
            }

            ApplicationDependency plugin;
            DependencyImportContextImpl subContext;
            for (int i = 0; i < actions.size(); i++) {
                plugin = actions.get(i);
                subContext = new DependencyImportContextImpl(plugin,context,callbacks.get(i));

                importHandler.execute(subContext);
            }
            return CONTINUE_PROCESSING;

        }else{
            return mainCallback.execute(context);
        }
    }



}
