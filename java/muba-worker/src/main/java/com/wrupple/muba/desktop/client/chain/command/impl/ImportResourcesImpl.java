package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.ImportResources;
import com.wrupple.muba.desktop.client.chain.command.ImportResourcesCallback;
import com.wrupple.muba.desktop.client.chain.command.SplashScreen;
import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ImportResourcesImpl implements ImportResources {

    private final SplashScreen splash;

    private final ImportResourcesCallback importResourcesCallback;

    @Inject
    public ImportResourcesImpl(SplashScreen splash, ImportResourcesCallback importResourcesCallback) {
        this.splash = splash;
        this.importResourcesCallback = importResourcesCallback;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        ContextSwitchRuntimeContext context = (ContextSwitchRuntimeContext) ctx;
         /*
         * Load transaction data
		 */
        final Application applicationItem = (Application) context.getContextSwitch().getState().getHandleValue();

        List<CatalogActionRequest> actions = transform(applicationItem.getDependenciesValues());

        context.getStorageManager().perform(context, actions);


        return CONTINUE_PROCESSING;
    }
}
