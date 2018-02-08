package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.JavaScriptImportCommand;
import com.wrupple.muba.desktop.domain.DependencyImportContext;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.server.chain.command.impl.ParallelProcess;
import com.wrupple.muba.worker.server.service.StateTransition;

import java.util.List;

public class JavaScriptImportCommandImpl implements JavaScriptImportCommand {
    @Override
    public boolean execute(DependencyImportContext context) throws Exception {

        final ParallelProcess<String, String> loadScripts = new ParallelProcess<String, String>(new State<String, String>() {

            @Override
            public void start(final String parameter, final StateTransition<String> onDone, EventBus bus) {
                JsCatalogActionRequest request = JsCatalogActionRequest.newRequest(dm.getCurrentActivityDomain(), CatalogActionRequest.LOCALE,
                        WruppleDomainJavascript.CATALOG, CatalogActionRequest.READ_ACTION, parameter, "0", null, null);
                String scriptUrl = remoteStorage.buildServiceUrl(request);

                ScriptInjector.fromUrl(scriptUrl).setWindow(ScriptInjector.TOP_WINDOW).setCallback(new com.wrupple.muba.worker.server.service.impl.Callback<Void, Exception>() {
                    public void onFailure(Exception reason) {
                        Window.alert("Script load failed.");
                        onDone.setResultAndFinish(parameter);
                    }

                    public void onSuccess(Void result) {
                        onDone.setResultAndFinish(parameter);
                    }
                }).inject();
            }
        }, false, false);

        remoteStorage.assertManifest(new DataCallback<Void>() {

            @Override
            public void execute() {
                loadScripts.start(GWTUtils.asStringList(scripts), new DataCallback<List<String>>() {

                    @Override
                    public void execute() {
                        loadedscripts = true;
                        testAndFinish();
                    }
                }, eventBus);
            }
        });


        return CONTINUE_PROCESSING;
    }
}
