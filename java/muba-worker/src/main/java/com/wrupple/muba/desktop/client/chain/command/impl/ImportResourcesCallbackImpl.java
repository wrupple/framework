package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.ImportResourcesCallback;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.event.server.chain.command.impl.ParallelProcess;
import com.wrupple.muba.worker.server.service.ProcessManager;
import com.wrupple.muba.worker.server.service.impl.Callback;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ImportResourcesCallbackImpl extends Callback<ContainerContext> implements ImportResourcesCallback {

    // cachuky tuku
    @Inject
    public ImportResourcesCallbackImpl() {
        super(commands);
    }


    static class ResourceLoadingCallback extends DataCallback<ActivityProcess> {
        final DataCallback<ActivityProcess> wrapped;

        boolean activityProcess = false;
        boolean loadedstyles = false;
        boolean loadedscripts = false;

        public ResourceLoadingCallback(final DesktopManager dm, DataCallback<ActivityProcess> wrapped, final JsArrayString scripts, JsArrayString sheets,
                                       final StorageManager sm, final EventBus eventBus) {
            super();
            this.wrapped = wrapped;
            final RemoteStorageUnit remoteStorage = sm.getRemoteStorageUnit(dm.getCurrentActivityHost());
            /*
			 * LOAD JAVASCRIPT FILES
			 */
            if (scripts == null || scripts.length() == 0) {
                loadedscripts = true;
            } else {
                final ParallelProcess<String, String> loadScripts = new ParallelProcess<String, String>(new State<String, String>() {

                    @Override
                    public void start(final String parameter, final StateTransition<String> onDone, EventBus bus) {
                        JsCatalogActionRequest request = JsCatalogActionRequest.newRequest(dm.getCurrentActivityDomain(), CatalogActionRequest.LOCALE,
                                WruppleDomainJavascript.CATALOG, CatalogActionRequest.READ_ACTION, parameter, "0", null, null);
                        String scriptUrl = remoteStorage.buildServiceUrl(request);

                        ScriptInjector.fromUrl(scriptUrl).setWindow(ScriptInjector.TOP_WINDOW).setCallback(new Callback<Void, Exception>() {
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

            }
            if (sheets == null || sheets.length() == 0) {
                loadedstyles = true;
            } else {
                sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), WruppleDomainStyleSheet.CATALOG, GWTUtils.asStringList(sheets),
                        new DataCallback<List<JsCatalogEntry>>() {

                            @Override
                            public void execute() {
                                loadedstyles = true;
                                testAndFinish();
                                for (JsCatalogEntry js : result) {
                                    StyleInjector.inject(js.getStringValue());
                                }
                            }
                        });

            }
        }

        protected void testAndFinish() {
            if (activityProcess && loadedscripts && loadedstyles) {
                wrapped.setResultAndFinish(result);
            }
        }

        @Override
        public void execute() {
            activityProcess = true;
            testAndFinish();
        }

    }




}
