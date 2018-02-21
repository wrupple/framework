package com.wrupple.muba.catalogs.server.service.impl;

import com.google.inject.Inject;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.catalogs.server.service.TriggerCreationScope;
import com.wrupple.muba.event.domain.*;
import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class SystemCatalogPluginImpl extends StaticCatalogDescriptorProvider  implements SystemCatalogPlugin {

	protected static final Logger log = LoggerFactory.getLogger(SystemCatalogPluginImpl.class);



    @Inject
	public SystemCatalogPluginImpl(
            Provider<NamespaceContext> domainContextProvider,
            @Named(FieldDescriptor.CATALOG_ID) CatalogDescriptor fieldProvider,
            @Named(CatalogDescriptor.CATALOG_ID) CatalogDescriptor catalogProvider,
            @Named(Host.CATALOG) CatalogDescriptor peerProvider,
            @Named(DistributiedLocalizedEntry.CATALOG) CatalogDescriptor i18nProvider,
            @Named(Trigger.CATALOG) CatalogDescriptor triggerProvider,
            @Named(LocalizedString.CATALOG) CatalogDescriptor localizedStringProvider,
            @Named(Constraint.CATALOG_ID) CatalogDescriptor constraintProvider,
            @Named(Trash.CATALOG) CatalogDescriptor trashP,
            @Named(ContentRevision.CATALOG) CatalogDescriptor revisionP,
            @Named(ContentNode.CATALOG_TIMELINE) CatalogDescriptor timeline,
            @Named(ServiceManifest.CATALOG) CatalogDescriptor serviceManifest,
            @Named(Session.CATALOG) CatalogDescriptor session) {
        super();
        super.put(fieldProvider);
        super.put(serviceManifest);
            super.put(catalogProvider);
            super.put(peerProvider);
            super.put(i18nProvider);
            super.put(triggerProvider);
            super.put(localizedStringProvider);
            super.put(constraintProvider);
            super.put(trashP);
            super.put(timeline);
        super.put(revisionP);


        super.put(session);



	}


	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor regreso, CatalogActionContext context, TriggerCreationScope triggerInterpret) throws Exception {
        String catalogId = regreso.getDistinguishedName();
        TriggerImpl trigger;
        if (FieldDescriptor.CATALOG_ID.equals(catalogId)) {
            trigger = new TriggerImpl(1,
                    FieldDescriptorUpdateTrigger.class.getSimpleName(), false, null, null, null);
            trigger.setFailSilence(true);
            trigger.setStopOnFail(true);
            triggerInterpret.add(trigger, regreso,context);
            trigger = new TriggerImpl(2, FieldDescriptorUpdateTrigger.class.getSimpleName(), false,
                    null, null, null);
            trigger.setFailSilence(true);
            trigger.setStopOnFail(true);
            triggerInterpret.add(trigger, regreso,context);
        }  else if (Trash.CATALOG.equals(catalogId)) {
            trigger = new TriggerImpl(1, RestoreTrash.class.getSimpleName(),
                    true, null, null, null);
            trigger.setFailSilence(false);
            trigger.setStopOnFail(false);
            triggerInterpret.add(trigger, regreso,context);
            trigger = new TriggerImpl(2, TrashDeleteTrigger.class.getSimpleName(), false, null, null,
                    null);
            trigger.setFailSilence(true);
            trigger.setStopOnFail(true);
            triggerInterpret.add(trigger, regreso,context);
        }


		trigger = new TriggerImpl(2, GarbageCollection.class.getSimpleName(), false, null, null, null);
		trigger.setFailSilence(true);
		trigger.setStopOnFail(true);

        triggerInterpret.add(trigger, regreso,context);

		FieldDescriptor field = regreso.getFieldDescriptor(Trash.TRASH_FIELD);
		if (field != null && field.getDataType() == CatalogEntry.BOOLEAN_DATA_TYPE) {

			trigger = new TriggerImpl(1, EntryDeleteTrigger.class.getSimpleName(), false, null, null,
					null);
			trigger.setFailSilence(true);
			trigger.setStopOnFail(true);


            triggerInterpret.add(trigger, regreso,context);

		}

	}


	@Override
	public ValidationExpression[] getValidations() {// TODO catalog field
													// consistency
													// and getInheritance loop
													// detection
		return null;
	}

    @Override
    public Command[] getCatalogActions() {

        //in theory CRUD actions are the main actions provide by this plugin but is seems simpler to just allow the dictionary to register those instead
        return null;
    }

}
