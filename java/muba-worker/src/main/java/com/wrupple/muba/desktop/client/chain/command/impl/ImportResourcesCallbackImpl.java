package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.ImportResourcesCallback;
import com.wrupple.muba.event.server.chain.command.impl.ParallelProcess;
import com.wrupple.muba.worker.server.service.ProcessManager;

import javax.inject.Inject;
import java.util.List;

public class ImportResourcesCallbackImpl extends ParallelProcess implements ImportResourcesCallback {


    protected final PlaceController pc;
    protected final DesktopManager dm;
            if(welcomeProcessId !=null)
    final String welcomeProcessId = applicationItem.getWelcomeProcess();


    //TODO load logic resources, including finishing welcome process, BEFORE launching application stategetActivityProcess(place, applicationItem, new LaunchApplicationStateImpl.SetApplicationStateAndContext(pm, panel, eventBus, applicationItem));
    final DesktopPlace input, JsApplicationItem
    actd,
    private final TransactionalActivityAssembly assembly;
        callback.hook(vegetateHandler)
            eventBus.addHandler(VegetateEvent.TYPE,vegetateHandler)

            assembly.setApplicationItem(applicationItem)

            if(overridenProcessSteps ==null)
    private final ActivityVegetateEventHandler vegetateHandler; else     /*
     * SERVICES
     */
    private final com.wrupple.muba.event.EventBus eventBus;


        eventBus.fireEvent(applicationState,context.getRuntimeContext(),null)


    /*
    /////////////////////////////////////////////////////////////////////////////////////////

     */
    private final ProcessManager pm;
    protected JsArray<JsProcessTaskDescriptor> overridenProcessSteps;
    DataCallback<ActivityProcess> callback

    {

        callback.hook(new DataCallback<ActivityProcess>() {
            @Override
            public void execute() {
                final StateTransition<Process<JavaScriptObject, JavaScriptObject>> transactionInfoCallback = new DataCallback<Process<JavaScriptObject, JavaScriptObject>>() {

                    @Override
                    public void execute() {
                        JsTransactionApplicationContext i = JsTransactionApplicationContext.createObject().cast();
                        StateTransition<JavaScriptObject> o = DataCallback.nullCallback();
                        pm.processSwitch(result, applicationItem.getName(), i, o, result.getContext());

                    }
                };
                assembly.loadAndAssembleProcess(welcomeProcessId, transactionInfoCallback);
            }
        });
    }

    {
        StateTransition transactionInfoCallback = new ProcessDescriptorCallback(callback);
        String processId = applicationItem.getProcessAsId();
        JsProcessDescriptor process = result.get(0);
        assembly.start(process, callback, eventBus);


    }

    {
        assembly.assembleActivityProcess(overridenProcessSteps, callback);
    }

    // cachuky tuku
    @com.google.inject.Inject
    public LaunchApplicationStateImpl(ProcessManager pm, com.wrupple.muba.event.EventBus eventBus, DesktopManager dm, PlaceController pc, TransactionalActivityAssembly assembly,
                                      ActivityVegetateEventHandler vegetateHandler) {
        this.pm = pm;
        this.eventBus = eventBus;
        this.vegetateHandler = vegetateHandler;
        this.assembly = assembly;
    }


    @Inject
    public LaunchApplicationStateImpl(ProcessManager bpm) {
        this.bpm = bpm;
    }

    protected boolean recoverFromMissconfiguredDesktop(DesktopPlace place) {
        DesktopPlace newPlace = new DesktopPlace(DesktopManager.RECOVERY_ACTIVITY);
        newPlace.setFoward(place);
        pc.goTo(newPlace);
        return true;
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

    public static class SetApplicationStateAndContext extends DataCallback<ActivityProcess> {
        ProcessManager pm;
        AcceptsOneWidget panel;
        EventBus eventBus;
        JsApplicationItem applicationItem;

        public SetApplicationStateAndContext(ProcessManager pm, AcceptsOneWidget panel, EventBus eventBus, JsApplicationItem applicationItem) {
            super();
            this.pm = pm;
            this.panel = panel;
            this.eventBus = eventBus;
            this.applicationItem = applicationItem;
        }

        @Override
        public void execute() {
            pm.setCurrentProcess(applicationItem.getProcessAsId());
            pm.contextSwitch(result, applicationItem, panel, eventBus);
        }

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
