package com.wrupple.muba.event;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.event.server.chain.command.SentenceNativeInterface;
import com.wrupple.muba.event.server.chain.command.impl.JavaSentenceNativeInterface;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;
import com.wrupple.muba.event.server.domain.impl.PersistentCatalogEntityImpl;
import com.wrupple.muba.event.server.service.*;
import com.wrupple.muba.event.server.service.impl.*;
import org.apache.commons.chain.CatalogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wrupple.muba.event.server.chain.command.EventDispatcher;
import com.wrupple.muba.event.server.chain.command.impl.EventDispatcherImpl;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationModule extends AbstractModule {

	@Override
	protected void configure() {
        bind(Boolean.class).annotatedWith(Names.named("event.parallel")).toInstance(false);
        bind(String.class).annotatedWith(Names.named("chain.unknownService")).toInstance("chain.unknownService");
        bind(FieldDescriptor.class).annotatedWith(Names.named("event.sentence")).to(SentenceField.class);

        /*
         * Application
         */
		bind(EventBus.class).to(EventBusImpl.class);//ServletContext
        /*
         * model
         */
		bind(RuntimeContext.class).to(RuntimeContextImpl.class);//request scoped
        bind(EventBroadcastQueueElement.class).to(EventBroadcastQueueElementImpl.class);
        bind(PersistentCatalogEntity.class).to(PersistentCatalogEntityImpl.class);
		
		/*
		 * Commands
		 */
		bind(EventDispatcher.class).to(EventDispatcherImpl.class);
		bind(SentenceNativeInterface.class).to(JavaSentenceNativeInterface.class);
		/*bind(EventRegistry.class).to(EventRegistryImpl.class);
		 * Services
		 */
		bind(EventRegistry.class).to(EventRegistryImpl.class);
		bind(CatalogFactory.class).toInstance(CatalogFactory.getInstance());
		bind(ParentServiceManifest.class).to(ParentServiceManifestImpl.class);
		bind(SentenceValidator.class).to(SentenceValidatorImpl.class);
		/*
		 * Native Services
		 */
        bind(ObjectNativeInterface.class).to(JavaObjectNativeInterface.class);
        bind(FieldAccessStrategy.class).to(JavaFieldAccessStrategy.class);
        bind(FilterNativeInterface.class).to(JavaFilterNativeInterfaceImpl.class);
        bind(LargeStringFieldDataAccessObject.class).to(LargeStringFieldDataAccessObjectImpl.class);

    }

    @Provides
    @Singleton
    @Inject
    @Named(ServiceManifest.CATALOG)
    public CatalogDescriptor contentRevision() {
        CatalogDescriptorImpl regreso = new CatalogDescriptorImpl();
        regreso.setClazz(ServiceManifestImpl.class);
        regreso.setDescriptiveField(CatalogEntry.NAME_FIELD);
        Map<String, FieldDescriptor> fields = new LinkedHashMap<String, FieldDescriptor>();
        FieldDescriptorImpl field;


        field = new FieldDescriptorImpl().makeDefault("distinguishedName", "distinguishedName", "text",
                CatalogEntry.STRING_DATA_TYPE);
        fields.put(field.getFieldId(), field);

        regreso.setFieldsValues(fields);
        regreso.setDistinguishedName(ServiceManifest.CATALOG);
        regreso.setId(-278532l);
        regreso.setKeyField(CatalogEntry.ID_FIELD);
        regreso.setName("Revision");
        regreso.setConsolidated(true);
        return regreso;
    }

}
