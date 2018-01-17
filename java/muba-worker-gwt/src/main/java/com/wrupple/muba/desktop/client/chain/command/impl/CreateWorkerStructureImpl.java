package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.CreateWorkerStructure;
import com.wrupple.muba.desktop.domain.ContainerContext;
import org.apache.commons.chain.Context;

public class CreateWorkerStructureImpl implements CreateWorkerStructure {


/*
    @Override
    public Application getInitialActivity(LaunchWorker request, RuntimeContext parent) throws Exception {

        Application root =  request.getHomeApplicationValue();

        PlaceController placeController = clientFactory.getPlaceController();

        // Start ActivityManager for the main widget setRuntimeContext our ActivityMapper
        //TODO there is no activityMapper since only one type of activity is available
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(appWidget);

        // Start PlaceHistoryHandler setRuntimeContext our PlaceHistoryMapper
        //AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
        //theres no app history or url PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);

        RootPanel.get().add(appWidget);
        // Goes to the place represented on URL else default place
        historyHandler.handleCurrentApplictionState();


        if (first != null) {
            //remove splash screen
            RootLayoutPanel.get().remove(first);
            first = null;
        }

        final ActivityManager activityManager = new ActivityManager(
                activityMapper, eventBus);

        NestedActivityWindow main = new NestedActivityWindow(dm);
        main.setStyleName("desktop");

        activityManager.setDisplay(main.getRootTaskPresenter());
        eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeListener(main, toolbar, dm));
        eventBus.addHandler(VegetateEvent.TYPE, toolbar);
        eventBus.addHandler(DesktopProcessEvent.TYPE, toolbar);
        RootLayoutPanel.get().add(main);
        // Fire Event
        eventBus.fireEvent(new DesktopInitializationDoneEvent());
        // Goes to the place represented on URL else default place
        final PlaceHistoryHandler paceHistoryHandler = new PlaceHistoryHandler(historyMapper);
        paceHistoryHandler.register(placeController, eventBus, dm.getDefaultPlace());
        GWT.log("Desktop Loading finished, handling current url history state");
        paceHistoryHandler.handleCurrentApplictionState();

        //activity manager set display new NestedActivityWindow(dm)
        //add display to root layoutpanel
        // fire  state  postSolutionToApplication(applicationState);
        //TODO RETURN FIRST ACTIVITY
        return null;
    }*/

    @Override
    public boolean execute(Context ctx) throws Exception {

        ContainerContext context = (ContainerContext) ctx;

        AcceptsOneWidget panel = context.getPanel()

        HelloView helloView = clientFactory.getHelloView();
        helloView.setName(name);
        helloView.setPresenter(this);
        containerWidget.setWidget(helloView.asWidget());


        DesktopPlace place = (DesktopPlace) pc.getWhere();
        if (!dm.isDesktopyConfigured()) {
            if (recoverFromMissconfiguredDesktop(place)) {
                return;
            }

        }

        JavaScriptObject o = dm.getApplicationItem(place);

        JsApplicationItem applicationItem;
        if (o == null) {
            applicationItem = null;
        } else {
            applicationItem = o.cast();
        }
        getActivityProcess(place, applicationItem, new SetApplicationStateAndContext(pm, panel, eventBus, applicationItem));


        return CONTINUE_PROCESSING;
    }
}
