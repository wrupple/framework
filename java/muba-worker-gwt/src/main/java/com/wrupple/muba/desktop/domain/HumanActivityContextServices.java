package com.wrupple.muba.desktop.domain;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.desktop.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.ServiceBus;
import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.client.widgets.TaskContainer;
import com.wrupple.vegetate.shared.services.PeerManager;
public class HumanActivityContextServices implements ProcessContextServices {
	
	//TODO this static instance is ugly! though necesarry (?) for JSNI
	public static HumanActivityContextServices SATIC_INSTANCE;

	
	private final DesktopManager desktopManager;
	
	private final ProcessManager processManager;
	
	private final ContentManagementSystem contentManager;
	
	private final StorageManager storageManager;
	
	private final PeerManager peerManager;
	
	
	
	private final ServiceBus serviceBus;
	
	
	
	private final PlaceController placeController;
	private final EventBus eventBus;

	/**
	 * Same for all nested processes
	 */
    private final ProcessWindow outputFeature;
    /**
	 * Unique per nested process
	 */
    private final TaskContainer jobInterface;

	private final ApplicationItem item;

	private final Process<?, ?> process;


	private String processLocalizedName;

    public HumanActivityContextServices(Process<?, ?> process, ApplicationItem item, PlaceController placeController, TaskContainer jobInterface, ProcessWindow outputFeature, ProcessManager processManager,
                                        ServiceBus serviceBus, DesktopManager desktopManager, ContentManagementSystem contentManager, StorageManager storageManager, PeerManager pm, EventBus bus) {
        super();
		this.process=process;
        this.jobInterface = jobInterface;
        this.item=item;
		this.outputFeature = outputFeature;
		this.processManager = processManager;
		this.serviceBus = serviceBus;
		this.desktopManager = desktopManager;
		this.storageManager = storageManager;
		this.placeController = placeController;
		this.peerManager=pm;
		this.contentManager=contentManager;
		this.eventBus = bus;
		SATIC_INSTANCE = this;
	}

	@Override
    public ProcessWindow getActivityOutputFeature() {
        return outputFeature;
	}

	@Override
	public ServiceBus getServiceBus() {
		return serviceBus;
	}

	@Override
	public ProcessManager getProcessManager() {
		return processManager;
	}

	@Override
	public DesktopManager getDesktopManager() {
		return desktopManager;
	}

	@Override
	public StorageManager getStorageManager() {
		return storageManager;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	public static String getCurrentPlaceProperty(String name) {
		DesktopPlace place = (DesktopPlace) SATIC_INSTANCE.getPlaceController().getWhere();
		return place.getProperty(name);
	}


	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public Process<?, ?> getProcess() {
		return process;
	}

	@Override
	public String getProcessLocalizedName() {
		if(processLocalizedName==null){
			return item.getName();
		}else{
			return processLocalizedName;
		}
	}

	public ContentManagementSystem getContentManager() {
		return contentManager;
	}

	public PeerManager getPeerManager() {
		return peerManager;
	}

	@Override
	public ApplicationItem getItem() {
		return item;
	}

	@Override
	public void setProcessLocalizedName(String overridenName) {
		this.processLocalizedName=overridenName;
	}

	@Override
    public TaskContainer getNestedTaskPresenter() {
        return jobInterface;
    }

}
