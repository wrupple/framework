package com.wrupple.muba.bpm.domain;


import com.wrupple.muba.bpm.server.service.ProcessManager;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 11/05/17.
 */
public interface ApplicationContext extends ServiceContext {
    final String CATALOG = "ApplicationContext";

    ProcessManager getProcessManager();

    ApplicationContext setRuntimeContext(RuntimeContext requestContext);

    ApplicationState getStateValue();

    void setStateValue(ApplicationState state);

    //EventBus available througg RuntimeContext

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