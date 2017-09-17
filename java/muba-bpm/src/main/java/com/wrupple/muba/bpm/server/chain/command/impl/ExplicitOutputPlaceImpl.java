package com.wrupple.muba.bpm.server.chain.command.impl;

import java.util.Collection;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.bpm.server.chain.command.ExplicitOutputPlace;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.desktop.client.services.command.ExplicitOutputPlace;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsNotification;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class ExplicitOutputPlaceImpl implements ExplicitOutputPlace {





	@Override
	public boolean execute() {

        private JsNotification output;
        private StateTransition<DesktopPlace> callback;
        private String[] activity;
        private StorageManager desc;
        private StorageManager sm;
        private boolean entry;
        private JavaScriptObject properties;
        private ProcessContextServices processContext;

		if (activity == null) {
			/*
			 * if activity has not been defined either by properties or command argument:
			 * 
			 * read the value from the output of whateve field that has a foreignKey pointing to Workflow
			 * 
			 */
			if (output != null) {
				String catalogId = output.getCatalog();
				DesktopManager dm = processContext.getDesktopManager();
				desc.loadCatalogDescriptor(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalogId, new DataCallback<CatalogDescriptor>() {

					@Override
					public void execute() {
						
						Collection<FieldDescriptor> fields = result.getOwnedFieldsValues();
						String applicationItem = null;
						for (FieldDescriptor field : fields) {
							// TODO make configurable field
							if (field.isKey() && !field.isMultiple() && field.getForeignCatalogName() != null
									&& ApplicationItem.CATALOG.equals(field.getForeignCatalogName())) {
								
								applicationItem = GWTUtils.getAttribute(output, field.getFieldId());

								break;
							}
						}
						if (applicationItem == null) {
							throw new IllegalArgumentException("unable to determine followup Activiy");
						} else {
							
							JsApplicationItem regreso =(JsApplicationItem) processContext.getDesktopManager().getApplicationItem(applicationItem);
							
							
							new Launch(desc,output,entry,properties,callback).setResultAndFinish(regreso.getUri());


						}
					}
				});
			}
		} else {
			new Launch(desc,output,entry,properties,callback).setResultAndFinish(activity);
		}
        return CONTINUE_PROCESSING;
	}

	@Override
	public void prepare(String command, JavaScriptObject appItemProperties, EventBus eventBus, ProcessContextServices processContext,
                        JsTransactionApplicationContext processParameters, StateTransition<DesktopPlace> callback) {
		if (activity == null) {
			String[] commandTokens = command.split(" ");
			if (commandTokens.length > 1) {
				this.activity = commandTokens[1].split(com.wrupple.muba.desktop.shared.services.UrlParser.TOKEN_SEPARATOR);
			}
		}
		this.processContext=processContext;
		this.properties = appItemProperties;
		this.callback = callback;
		this.output = StandardActivityCommand.getUserOutputEntry(processParameters.getUserOutput());
	}

	@Override
	public void setActivity(String act) {
		this.activity = act.split(com.wrupple.muba.desktop.shared.services.UrlParser.TOKEN_SEPARATOR);
	}

	@Override
	public void setEntry(String is) {
		this.entry = is==null || Boolean.parseBoolean(is);
	}

}
