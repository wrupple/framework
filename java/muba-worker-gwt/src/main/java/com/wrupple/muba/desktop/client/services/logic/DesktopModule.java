package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.DesktopActivityMapper;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.HomeToolbar;
import com.wrupple.muba.desktop.client.factory.dictionary.ActivityPresenterMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.ThemedImagesDictionary;
import com.wrupple.muba.desktop.client.services.command.CommitCommand;
import com.wrupple.muba.desktop.client.services.command.HistoryBackCommand;
import com.wrupple.muba.desktop.client.services.command.InterruptActivity;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.client.services.presentation.ModifyUserInteractionStatePanelCommand;

public interface DesktopModule extends Ginjector {
	ActivityPresenterMap activityPresenter();

	/*
	 * Desktop Structure
	 */

	DesktopActivityMapper getActivityMapper();
//TaskMapper
	PlaceHistoryMapper getPlaceHistoryMapper();

	DesktopManager getDesktopManager();

	ServiceBus getServiceBus();

	EventBus getEventBus();

	PlaceController getPlaceController();


	/*
	 * Desktop Loading
	 */

	ReadDesktopMetadata getPlaceRegister();

	HomeToolbar getHomeToolbar();

	/*
	 * Commands
	 */

	InterruptActivity gotoActivity();

	HistoryBackCommand historyBack();

	ProcessSwitchCommand processSwitch();

	ModifyUserInteractionStatePanelCommand modifyDesktop();

	ModifyUserInteractionStateModelCommand modifyModel();

	CommitCommand commitState();

	/*
	 * Service
	 */
	DesktopTheme desktopTheme();
	
	ThemedImagesDictionary images();
}
