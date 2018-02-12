package com.wrupple.muba.catalogs.client.services;

import com.google.gwt.inject.client.Ginjector;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.SolverConcensorMap;
import com.wrupple.muba.desktop.shared.services.factory.help.TriggerAidProvider;
import com.wrupple.muba.desktop.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.command.CatalogCommandService;
import com.wrupple.muba.desktop.client.services.logic.DesktopModule;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;

public interface CatalogModule extends DesktopModule, Ginjector {
	/*
	 * Dictionaries
	 */

    SolverConcensorMap aidProviders();

	/*
	 * AidProviders
	 */
	
	TriggerAidProvider triggerAid();
	
	/*
	 * Commands
	 */
	
	CatalogCommandService catalog();
	
	
	/*
	 * FIXME Expose and use through the vconfiguration framework (unify
	 * server-client)
	 */
    StorageManager descriptionService();

    FieldDescriptionService fieldDescriptionService();


}
