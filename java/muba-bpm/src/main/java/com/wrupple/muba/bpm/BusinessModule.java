package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.wrupple.muba.bpm.server.domain.IntentResolverContextImpl;
import com.wrupple.muba.event.domain.ImplicitIntent;
import com.wrupple.muba.event.server.service.EventRegistry;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.domain.impl.*;
import com.wrupple.muba.bpm.server.chain.BusinessEngine;
import com.wrupple.muba.bpm.server.chain.IntentResolverEngine;
import com.wrupple.muba.bpm.server.chain.command.*;
import com.wrupple.muba.bpm.server.chain.command.impl.*;
import com.wrupple.muba.bpm.server.chain.impl.BusinessEngineImpl;
import com.wrupple.muba.bpm.server.service.BusinessPlugin;
import com.wrupple.muba.bpm.server.service.impl.BusinessPluginImpl;
import com.wrupple.muba.event.server.service.impl.EventRegistryImpl;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;

/**
 * Created by japi on 11/08/17.
 */
public class BusinessModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(Integer.class).annotatedWith(Names.named("com.wrupple.errors.unknownUser")).toInstance(0);

        bind(BusinessServiceManifest.class).to(BusinessServiceManifestImpl.class);
        bind(BusinessEngine.class).to(BusinessEngineImpl.class);
        bind(BusinessRequestInterpret.class).to(BusinessRequestInterpretImpl.class);
        bind(BusinessContext.class).to(BusinessContextImpl.class);

        bind(IntentResolverServiceManifest.class).to(IntentResolverServiceManifestImpl.class);
        bind(IntentResolverEngine.class).to(IntentResolverEngineImpl.class);
        bind(IntentResolverRequestInterpret.class).to(IntentResolverRequestInterpretImpl.class);
        bind(IntentResolverContext.class).to(IntentResolverContextImpl.class);

        bind(EventRegistry.class).to(EventRegistryImpl.class);
        bind(BusinessPlugin.class).to(BusinessPluginImpl.class);
        bind(StakeHolderTrigger.class).to(StakeHolderTriggerImpl.class);
        bind(ValueChangeAudit.class).to(ValueChangeAuditImpl.class);
        bind(ValueChangeListener.class).to(ValueChangeListenerImpl.class);
    }

    @Provides
    @Singleton
    @Inject
    @Named(Workflow.CATALOG)
    public CatalogDescriptor activity(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(WorkflowImpl.class, Workflow.CATALOG, "Activity",
                -990090, null);
        r.setClazz(WorkflowImpl.class);
        return r;
    }


    @Provides
    @Singleton
    @Inject
    @Named(Notification.CATALOG)
    public CatalogDescriptor notification(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(NotificationImpl.class, Notification.CATALOG, "Notification",
                -990091, null);
        r.setClazz(NotificationImpl.class);
        return r;
    }

    @Provides
    @Singleton
    @Inject
    @Named(ImplicitIntent.CATALOG)
    public CatalogDescriptor intent(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ImplicitIntentImpl.class, ImplicitIntent.CATALOG, "Event",
                -990091, null);
        r.setClazz(ImplicitIntentImpl.class);
        return r;
    }


}
