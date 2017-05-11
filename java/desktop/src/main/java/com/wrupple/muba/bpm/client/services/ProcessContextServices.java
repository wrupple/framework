package com.wrupple.muba.bpm.client.services;

import com.google.gwt.place.shared.PlaceController;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.desktop.client.activity.widgets.ProcessPresenter;
import com.wrupple.muba.desktop.client.activity.widgets.TaskPresenter;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.bpm.server.service.TaskRunnerPlugin;

public interface ProcessContextServices extends TaskRunnerPlugin {

	/**
	 * this also holds a reference to the root task presenter
	 * 
	 * @return the widget responsable for holding this process, and processes
	 *         that spawn from it and showing current tasks to the user
	 */
	ProcessPresenter getActivityOutputFeature();

	/**
	 * went the process switches to a nested process the process presenter
	 * spawns a nested TaskPresenter, this method returns a reference for the
	 * nested or root process this context is runnning on
	 * 
	 * @return
	 */
	TaskPresenter getNestedTaskPresenter();

	PlaceController getPlaceController();

	DesktopManager getDesktopManager();

	public ContentManagementSystem getContentManager();

	String getProcessLocalizedName();

	void setProcessLocalizedName(String overridenName);

	ApplicationItem getItem();

}
