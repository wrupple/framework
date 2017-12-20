package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.DesktopActivityMapper;
import com.wrupple.muba.desktop.client.activity.widgets.ContentPanel;
import com.wrupple.muba.desktop.client.activity.widgets.ContentPanel.ToolbarDirection;
import com.wrupple.muba.desktop.client.activity.widgets.panels.NestedActivityPresenter;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.HomeToolbar;
import com.wrupple.muba.desktop.client.chain.command.LaunchApplicationState;
import com.wrupple.muba.desktop.client.event.DesktopInitializationDoneEvent;
import com.wrupple.muba.desktop.client.event.DesktopProcessEvent;
import com.wrupple.muba.desktop.client.event.VegetateEvent;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LaunchApplicationStateImpl implements LaunchApplicationState /*com.google.gwt.core.client.RunAsyncCallback*/ {

    final PlaceController placeController;
    final PlaceHistoryMapper historyMapper;
    final DesktopManager dm;
    final DesktopActivityMapper activityMapper;
    final HomeToolbar toolbar;
    final com.google.web.bindery.event.shared.EventBus eventBus;
    //spash screen
    Widget first;


    @Inject
    public LaunchApplicationStateImpl(PlaceController placeController, PlaceHistoryMapper historyMapper, DesktopManager dm, Widget first, DesktopActivityMapper activityMapper, HomeToolbar toolbar, EventBus eventBus) {
        this.placeController = placeController;
        this.historyMapper = historyMapper;
        this.dm = dm;
        this.first = first;
        this.activityMapper = activityMapper;
        this.toolbar = toolbar;
        this.eventBus = eventBus;
    }

    @Override
    public boolean execute(Context context) throws Exception {


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


        return CONTINUE_PROCESSING;
    }

    static class PlaceChangeListener implements com.google.gwt.place.shared.PlaceChangeEvent.Handler {
        final NestedActivityPresenter main;

        final HomeToolbar toolbar;

        final DesktopManager dm;


        public PlaceChangeListener(NestedActivityPresenter main, HomeToolbar toolbar, DesktopManager dm) {
            super();
            this.dm = dm;
            this.main = main;
            this.toolbar = toolbar;

            NodeList<Element> metaTags = Document.get().getElementsByTagName("meta");
            MetaElement meta;
            String metaTagName;
            String metaContent = null;
            for (int i = 0; i < metaTags.getLength(); i++) {
                meta = metaTags.getItem(i).cast();
                metaTagName = meta.getName();
                if (ContentPanel.ActivityPresenterToolbarHeight.equals(metaTagName)) {
                    metaContent = meta.getContent();
                }
            }
            int height;
            if (metaContent == null) {
                height = 30;
            } else {
                height = Integer.parseInt(metaContent);
            }
            toolbar.setSize(height);
            main.addToolbarAndRedraw(toolbar, false, false, ToolbarDirection.NORTH, "home", height);
        }


        @Override
        public void onPlaceChange(PlaceChangeEvent event) {
            DesktopPlace place = (DesktopPlace) event.getNewPlace();
            String[] newActivity = place.getTokens();
            JsApplicationItem item = (JsApplicationItem) dm.getApplicationItem(place);
            toolbar.onPlaceChange(place, item);
            if (DesktopLoadingStateHolder.homeActivity.equals(newActivity)) {
                main.hideToolbar(toolbar);
            } else {
                main.showToolbar(toolbar);
            }
        }

    }
}
