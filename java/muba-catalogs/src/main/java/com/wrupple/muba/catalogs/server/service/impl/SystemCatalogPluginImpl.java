package com.wrupple.muba.catalogs.server.service.impl;

import com.google.inject.Inject;
import com.wrupple.muba.catalogs.domain.CatalogEventListener;
import com.wrupple.muba.catalogs.server.service.*;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class SystemCatalogPluginImpl extends StaticCatalogDescriptorProvider  implements SystemCatalogPlugin {

	protected static final Logger log = LoggerFactory.getLogger(SystemCatalogPluginImpl.class);


	private final Provider<CatalogTriggerInterpret> triggerInterpretProvider;

    @Inject
	public SystemCatalogPluginImpl(
            Provider<NamespaceContext> domainContextProvider,
            @Named(FieldDescriptor.CATALOG_ID) CatalogDescriptor fieldProvider,
            @Named(CatalogDescriptor.CATALOG_ID) CatalogDescriptor catalogProvider,
            @Named(Host.CATALOG) CatalogDescriptor peerProvider,
            @Named(DistributiedLocalizedEntry.CATALOG) CatalogDescriptor i18nProvider,
            @Named(CatalogEventListener.CATALOG) CatalogDescriptor triggerProvider,
            @Named(LocalizedString.CATALOG) CatalogDescriptor localizedStringProvider,
            @Named(Constraint.CATALOG_ID) CatalogDescriptor constraintProvider,
            @Named(Trash.CATALOG) CatalogDescriptor trashP,
            @Named(ContentRevision.CATALOG) CatalogDescriptor revisionP,
            @Named(ContentNode.CATALOG_TIMELINE) CatalogDescriptor timeline,
             Provider<CatalogTriggerInterpret> triggerInterpret) {
        super();
        this.triggerInterpretProvider = triggerInterpret;
        super.put(fieldProvider);
            super.put(catalogProvider);
            super.put(peerProvider);
            super.put(i18nProvider);
            super.put(triggerProvider);
            super.put(localizedStringProvider);
            super.put(constraintProvider);
            super.put(trashP);
            super.put(timeline);
        super.put(revisionP);



	}


	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor regreso, CatalogActionContext context) throws Exception {
        String catalogId = regreso.getDistinguishedName();
        CatalogTriggerInterpret triggerInterpret = triggerInterpretProvider.get();
        CatalogEventListenerImpl trigger;
        if (FieldDescriptor.CATALOG_ID.equals(catalogId)) {
            trigger = new CatalogEventListenerImpl(1,
                    FieldDescriptorUpdateTrigger.class.getSimpleName(), false, null, null, null);
            trigger.setFailSilence(true);
            trigger.setStopOnFail(true);
            triggerInterpret.addNamespaceScopeTrigger(trigger, regreso,context);
            trigger = new CatalogEventListenerImpl(2, FieldDescriptorUpdateTrigger.class.getSimpleName(), false,
                    null, null, null);
            trigger.setFailSilence(true);
            trigger.setStopOnFail(true);
            triggerInterpret.addNamespaceScopeTrigger(trigger, regreso,context);
        } else if (CatalogDescriptor.CATALOG_ID.equals(catalogId)) {
             trigger = new CatalogEventListenerImpl(1,
                    CatalogDescriptorUpdateTrigger.class.getSimpleName(), false, null, null, null);
            trigger.setFailSilence(true);
            trigger.setStopOnFail(true);
            triggerInterpret.addNamespaceScopeTrigger(trigger, regreso,context);
            trigger = new CatalogEventListenerImpl(2, CatalogDescriptorUpdateTrigger.class.getSimpleName(), false,
                    null, null, null);
            trigger.setFailSilence(true);
            trigger.setStopOnFail(true);
            triggerInterpret.addNamespaceScopeTrigger(trigger, regreso,context);

            trigger = new CatalogEventListenerImpl(0, PluginConsensus.class.getSimpleName(), true, CatalogDescriptor.CATALOG_ID, null, null);
            trigger.setFailSilence(true);
            trigger.setStopOnFail(true);
            triggerInterpret.addNamespaceScopeTrigger(trigger, regreso,context);
        }  else if (Trash.CATALOG.equals(catalogId)) {
            trigger = new CatalogEventListenerImpl(1, RestoreTrash.class.getSimpleName(),
                    true, null, null, null);
            trigger.setFailSilence(false);
            trigger.setStopOnFail(false);
            triggerInterpret.addNamespaceScopeTrigger(trigger, regreso,context);
            trigger = new CatalogEventListenerImpl(2, TrashDeleteTrigger.class.getSimpleName(), false, null, null,
                    null);
            trigger.setFailSilence(true);
            trigger.setStopOnFail(true);
            triggerInterpret.addNamespaceScopeTrigger(trigger, regreso,context);
        }


		trigger = new CatalogEventListenerImpl(2, GarbageCollection.class.getSimpleName(), false, null, null, null);
		trigger.setFailSilence(true);
		trigger.setStopOnFail(true);

        triggerInterpret.addNamespaceScopeTrigger(trigger, regreso,context);

		FieldDescriptor field = regreso.getFieldDescriptor(Trash.TRASH_FIELD);
		if (field != null && field.getDataType() == CatalogEntry.BOOLEAN_DATA_TYPE) {

			trigger = new CatalogEventListenerImpl(1, EntryDeleteTrigger.class.getSimpleName(), false, null, null,
					null);
			trigger.setFailSilence(true);
			trigger.setStopOnFail(true);


            triggerInterpret.addNamespaceScopeTrigger(trigger, regreso,context);

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
