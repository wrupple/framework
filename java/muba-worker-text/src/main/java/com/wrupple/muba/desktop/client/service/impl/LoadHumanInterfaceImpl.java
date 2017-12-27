package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.bpm.domain.Application;
import com.wrupple.muba.desktop.client.service.LoadHumanInterface;
import com.wrupple.muba.desktop.domain.LaunchWorker;
import com.wrupple.muba.event.domain.RuntimeContext;

import javax.inject.Singleton;

@Singleton
public class LoadHumanInterfaceImpl implements LoadHumanInterface{


    @Override
    public Application getInitialActivity(LaunchWorker request, RuntimeContext parent) throws Exception {

        Application root =  request.getHomeApplicationValue();

        PlaceController placeController = clientFactory.getPlaceController();

        // Start ActivityManager for the main widget with our ActivityMapper
        //TODO there is no activityMapper since only one type of activity is available
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(appWidget);

        // Start PlaceHistoryHandler with our PlaceHistoryMapper
        //AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
        //theres no app history or url PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);

        RootPanel.get().add(appWidget);
        // Goes to the place represented on URL else default place
        historyHandler.handleCurrentHistory();


        if (first != null) {
            //remove splash screen
            RootLayoutPanel.get().remove(first);
            first = null;
        }

        final ActivityManager activityManager = new ActivityManager(
                activityMapper, eventBus);

        NestedActivityPresenter main = new NestedActivityPresenter(dm);
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
        paceHistoryHandler.handleCurrentHistory();

        //activity manager set display new NestedActivityPresenter(dm)
        //add display to root layoutpanel
        // fire  state  postSolutionToApplication(applicationState);
        //TODO RETURN FIRST ACTIVITY
        return null;
    }

}
