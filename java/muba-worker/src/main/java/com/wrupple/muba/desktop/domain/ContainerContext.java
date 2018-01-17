package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.desktop.client.service.data.StorageManager;
import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.event.domain.ServiceContext;
import com.wrupple.muba.worker.server.service.ProcessManager;

public interface ContainerContext extends ServiceContext {

    ContextSwitch getContextSwitch();


    ProcessManager getProcessManager();

    StorageManager getStorageManager();
    /*




    public PeerManager getPeerManager();



    PlaceController getPlaceController();

    DesktopManager getDesktopManager();

    public ContentManagementSystem getContentManager();
     */

/**
 * this also holds a reference to the root task presenter
 *
 * @return the widget responsable for holding this process, and processes
 *         that spawn from it and showing current tasks to the user
 */
    //ProcessPresenter getActivityOutputFeature();

    /**
     * went the process switches to a nested process the process presenter
     * spawns a nested TaskPresenter, this method returns a reference for the
     * nested or root process this context is runnning on
     *
     * @return
     */
    //TaskPresenter getNestedTaskPresenter();


    /*

        DesktopPlace place = (DesktopPlace) pc.getWhere();
        if (!dm.isDesktopyConfigured()) {
            if (recoverFromMissconfiguredDesktop(place)) {
                return ;
            }

        }

     */

    void setDisplay(ProcessWindow processWindow);

    /*


    // Start ActivityManager for the main widget setRuntimeContext our ActivityMapper
            //TODO there is no activityMapper since only one type of activity is available
            ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
            context.setDisplay(presenterProvider.get());

            // Start PlaceHistoryHandler setRuntimeContext our PlaceHistoryMapper
            AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
            //theres no app history or url
            PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
            historyHandler.register(placeController, eventBus, defaultPlace);

            final ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);


            activityManager.setDisplay(main.getRootTaskPresenter());

                 if (first != null) {
                //remove splash screen
                RootLayoutPanel.get().remove(first);
                first = null;
            }

            RootLayoutPanel.get().add(main);
     */
    void handleCurrentApplictionState();

    /*
    //hide home toolbar when in home activity
            eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeListener(main, toolbar, dm));
        eventBus.addHandler(DesktopProcessEvent.TYPE, toolbar);
        eventBus.addHandler(VegetateEvent.TYPE, toolbar);




     */


}
