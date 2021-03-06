package com.wrupple.muba.worker.client.services;

import com.google.gwt.inject.client.GinModule;
import com.wrupple.muba.cms.client.services.ContentModule;
import com.wrupple.muba.desktop.client.activity.*;
import com.wrupple.muba.desktop.client.activity.impl.BPMInboxActivity;
import com.wrupple.muba.desktop.client.activity.widgets.HTMLToolbar;
import com.wrupple.muba.desktop.client.activity.widgets.TabbedActivityWindow;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.HTMLCatalogEditor;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.CustomCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.IconCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.panels.NestedActivityWindow;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.DesktopActivityMap;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.ExternalAPILoaderMap;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.OutputHandlerMap;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.ServiceMap;
import com.wrupple.muba.desktop.shared.services.factory.help.ActionAidProvider;
import com.wrupple.muba.desktop.client.services.command.CurrentPlaceCommand;
import com.wrupple.muba.desktop.client.services.command.GoToCommand;
import com.wrupple.muba.desktop.client.services.command.NextPlace;
import com.wrupple.muba.desktop.client.services.command.impl.ExcecuteJavaScriptFuntion;
import com.wrupple.muba.desktop.client.services.command.impl.PlaceInterpretCommand;

public interface BPMModule extends ContentModule, GinModule {
    /*
	 * Activities
	 */

    BPMInboxActivity bpmInboxActivity();

    AdministrativeActionActivity administrativeActivity();

    NavigationActivity wruppleNavigationActivityProvider();

    ProcessBuildingActivity appBuildingActivity();

    DesktopTreeManagementActivity desktopTreeManagementActivity();

    TransactionalActivity defaultActivity();

    DomainRecoveryActivity recoveryActivity();

    NavigationActivity appStoreActivity();

    /*
     * Aid Providers
     */
    ActionAidProvider actionAid();

    /*
     * Service Maps
     */
    ExternalAPILoaderMap apiLoaderMap();

    DesktopActivityMap activityMap();

    OutputHandlerMap outputHandlers();

    ServiceMap commands();

    /*
     * Widgets
     */
    HTMLCatalogEditor html();

    HTMLToolbar htmlToolbar();

    /*
     * Commands
     */
    CurrentPlaceCommand currentPlace();

    ExcecuteJavaScriptFuntion excecuteJavascriptFuntion();

    PlaceInterpretCommand readCurrentPlace();

	/*
	 * OutputHandlers
	 */

    GoToCommand goTo();

    NextPlace next();

    /*
     * ActivityPresenters
     */
    TabbedActivityWindow tabbed();

    NestedActivityWindow nested();


	/*
	 * Cell providers
	 */

    IconCellProvider genericIconCell();

    CustomCellProvider customCell();

}
