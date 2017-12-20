package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.SessionContext;

public interface ProcessManager {
    /**
     * like unix screens that can be attached or detached to a thread or UI
     * @param startingState
     * @param thread
     * @return
     */
    ApplicationState acquireContext(Workflow startingState, SessionContext thread) throws Exception;


    Solver getSolver();

    ApplicationState requirereContext(Object existingApplicationStateId, RuntimeContext session) throws Exception;


   /* StorageManager getStorageManager();

	public PeerManager getPeerManager();



    PlaceController getPlaceController();

    DesktopManager getDesktopManager();

    public ContentManagementSystem getContentManager();
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


}
