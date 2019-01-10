package com.wrupple.muba.catalogs;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.domain.CatalogActionBroadcastImpl;
import com.wrupple.muba.event.domain.impl.*;
import com.wrupple.muba.catalogs.server.domain.CatalogServiceManifestImpl;
import com.wrupple.muba.catalogs.server.domain.catalogs.DistributiedLocalizedEntryDescriptor;
import com.wrupple.muba.catalogs.server.domain.catalogs.LocalizedStringDescriptor;
import com.wrupple.muba.catalogs.server.domain.catalogs.TrashDescriptor;
import com.wrupple.muba.catalogs.server.service.*;
import com.wrupple.muba.catalogs.server.service.impl.*;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.event.server.service.*;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author japi
 *
 */
public class CatalogModule extends AbstractModule {

	@Override
	protected void configure() {
		/*
	     * Contract Handlers
	     */
        bind(CatalogServiceManifest.class).to(CatalogServiceManifestImpl.class);
        bind(CatalogActionFilterManifest.class).to(CatalogActionFilterManifestImpl.class);
        bind(CatalogIntentListenerManifest.class).to(CatalogIntentListenerManifestImpl.class);

        bind(CatalogEngine.class).to(CatalogEngineImpl.class);
        bind(CatalogActionFilterEngine.class).to(CatalogActionFilterEngineImpl.class);
        bind(com.wrupple.muba.catalogs.server.chain.command.CatalogEventHandler.class).to(com.wrupple.muba.catalogs.server.chain.command.impl.CatalogEventHandlerImpl.class);

        bind(CatalogRequestInterpret.class).to(CatalogRequestInterpretImpl.class);
        bind(CatalogActionFilterInterpret.class).to(CatalogActionFilterInterpretImpl.class);
        bind(CatalogEventInterpret.class).to(CatalogEventInterpretImpl.class);

		//${my.service.invocation}
		String rawPattern = "\\$\\{([A-Za-z0-9]+\\.){0,}[A-Za-z0-9]+\\}";
		// 2014-01-18T00:35:03.463Z
		String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        //"default" catalog storage is not actually set, but but an unrecognized storage will resolve to default
        bind(String.class).annotatedWith(Names.named("catalog.storage")).toInstance("default");
        bind(String.class).annotatedWith(Names.named("catalog.storage.secure")).toInstance("Secure");
        bind(String.class).annotatedWith(Names.named("catalog.storage.metadata")).toInstance(CatalogDescriptor.CATALOG_ID);
		bind(String.class).annotatedWith(Names.named("catalog.storage.trigger")).toInstance(Trigger.CATALOG);

		bind(String.class).annotatedWith(Names.named("template.token.splitter")).toInstance("\\.");
		Pattern pattern = Pattern.compile(rawPattern);
		bind(Pattern.class).annotatedWith(Names.named("template.pattern")).toInstance(pattern);

		bind(String.class).annotatedWith(Names.named("catalog.datePattern")).toInstance(datePattern);
		bind(DateFormat.class).toInstance(new SimpleDateFormat(datePattern));
		bind(String.class).annotatedWith(Names.named("catalog.domainField")).toInstance(CatalogEntry.DOMAIN_FIELD);
		bind(Boolean.class).annotatedWith(Names.named("catalog.createablePrimaryKeys")).toInstance(false);
		bind(Boolean.class).annotatedWith(Names.named("catalog.followGraph")).toInstance(true);


		bind(String.class).annotatedWith(Names.named("catalog.ancestorKeyField")).toInstance("parentHID");
		bind(String.class).annotatedWith(Names.named("catalog.timeline.entryDiscriminator"))
				.toInstance("childHIdentity");
		bind(String.class).annotatedWith(Names.named("catalog.timeline.typeDiscriminator")).toInstance("childHType");

        bind(String.class).annotatedWith(Names.named(CatalogDescriptor.CATALOG_ID)).toInstance("/static/img/catalog.png");
        bind(String.class).annotatedWith(Names.named(CatalogDescriptor.CATALOG_ID)).toInstance("/static/img/catalog.png");


        bind(String.class).annotatedWith(Names.named(FieldDescriptor.CATALOG_ID)).toInstance( "/static/img/fields.png");
        bind(String.class).annotatedWith(Names.named(Constraint.CATALOG_ID)).toInstance("/static/img/check.png");
        bind(String.class).annotatedWith(Names.named(Trigger.CATALOG)).toInstance("/static/img/excecute.png");
        bind(String.class).annotatedWith(Names.named(WebEventTrigger.CATALOG)).toInstance( "/static/img/excecute.png");
        bind(String.class).annotatedWith(Names.named(DistributiedLocalizedEntry.CATALOG)).toInstance("/static/img/locale.png");
        bind(String.class).annotatedWith(Names.named(LocalizedString.CATALOG)).toInstance( "/static/img/locale.png");
        bind(String.class).annotatedWith(Names.named(ContentRevision.CATALOG)).toInstance( "/static/img/revision.png");
        bind(String.class).annotatedWith(Names.named(Trash.CATALOG)).toInstance( "/static/img/trash.png");

		bind(Class.class).annotatedWith(Names.named(DistributiedLocalizedEntry.CATALOG))
				.toInstance(HasAccesablePropertyValues.class);
		bind(Class.class).annotatedWith(Names.named(Constraint.CATALOG_ID)).toInstance(ConstraintImpl.class);
		bind(Class.class).annotatedWith(Names.named(LocalizedString.CATALOG))
				.toInstance(HasAccesablePropertyValues.class);
		bind(Class.class).annotatedWith(Names.named(Trash.CATALOG)).toInstance(HasAccesablePropertyValues.class);
		bind(Class.class).annotatedWith(Names.named(Trigger.CATALOG))
				.toInstance(TriggerImpl.class);
		bind(Class.class).annotatedWith(Names.named(FieldDescriptor.CATALOG_ID)).toInstance(FieldDescriptorImpl.class);
		bind(Class.class).annotatedWith(Names.named(CatalogDescriptor.CATALOG_ID))
				.toInstance(CatalogDescriptorImpl.class);
        bind(Class.class).annotatedWith(Names.named(CatalogActionRequest.CATALOG))
                .toInstance(CatalogActionRequestImpl.class);
        bind(Class.class).annotatedWith(Names.named(CatalogActionFiltering.CATALOG))
                .toInstance(CatalogActionFilteringImpl.class);
        bind(Class.class).annotatedWith(Names.named(CatalogActionBroadcast.CATALOG))
                .toInstance(CatalogActionBroadcastImpl.class);

		/*
		 * CONFIGURATION
		 */

		bind(NamespaceContext.class).to(NamespaceContextImpl.class);
        bind(CatalogActionFiltering.class).to(CatalogActionFilteringImpl.class);
        bind(CatalogActionRequest.class).to(CatalogActionRequestImpl.class);
		bind(CatalogDescriptor.class).annotatedWith(Names.named(DistributiedLocalizedEntry.CATALOG))
				.to(DistributiedLocalizedEntryDescriptor.class);
		bind(CatalogDescriptor.class).annotatedWith(Names.named(LocalizedString.CATALOG))
				.to(LocalizedStringDescriptor.class);
		bind(CatalogDescriptor.class).annotatedWith(Names.named(Trash.CATALOG)).to(TrashDescriptor.class);
		bind(Class.class).annotatedWith(Names.named(ContentRevision.CATALOG)).toInstance(PersistentCatalogEntity.class);

		/*
		 * Dictionaries
		 */
		bind(ActionsDictionary.class).to(ActionsDictionaryImpl.class);
		bind(TransactionsDictionary.class).to(TransactionsDictionaryImpl.class);
		bind(EntryCreators.class).to(EntryCreatorsImpl.class);
		bind(PrimaryKeyReaders.class).to(PrimaryKeyReadersImpl.class);
		bind(QueryReaders.class).to(QueryReadersImpl.class);
		bind(Writers.class).to(WritersImpl.class);
		bind(Deleters.class).to(DeletersImpl.class);

		/*
		 * Commands
		 */

        bind(WriteOutput.class).to(WriteOutputImpl.class);
        bind(CatalogTransactionState.class).to(CatalogTransactionStateImpl.class);
        bind(CatalogCreateTransaction.class).to(CatalogCreateTransactionImpl.class);
		bind(CatalogReadTransaction.class).to(CatalogReadTransactionImpl.class);
		bind(CatalogUpdateTransaction.class).to(CatalogUpdateTransactionImpl.class);
		bind(CatalogDeleteTransaction.class).to(CatalogDeleteTransactionImpl.class);

		bind(EntryDeleteTrigger.class).to(EntryDeleteTriggerImpl.class);
		bind(FieldDescriptorUpdateTrigger.class).to(FieldDescriptorUpdateTriggerImpl.class);
		bind(CatalogDescriptorUpdateTrigger.class).to(CatalogDescriptorUpdateTriggerImpl.class);
        bind(CatalogPluginQueryCommand.class).to(CatalogPluginQueryCommandImpl.class);

        bind(PluginConsensus.class).to(PluginConsensusImpl.class);
		bind(GarbageCollection.class).to(GarbageCollectionImpl.class);
		bind(TrashDeleteTrigger.class).to(TrashDeleteTriggerImpl.class);
		bind(RestoreTrash.class).to(RestoreTrashImpl.class);
		bind(IncreaseVersionNumber.class).to(IncreaseVersionNumberImpl.class);
		bind(Timestamper.class).to(TimestamperImpl.class);
		bind(UpdateTreeLevelIndex.class).to(UpdateTreeLevelIndexImpl.class);
		bind(WritePublicTimelineEventDiscriminator.class).to(WritePublicTimelineEventDiscriminatorImpl.class);
        //used by BroadcastInterpretImpl
        bind(EventSuscriptionMapper.class).to(ImplicitSuscriptionMapper.class);

		bind(CompleteCatalogGraph.class).to(EventuallyCompleteCatalogGraphImpl.class);
		bind(ExplicitDataJoin.class).to(ExplicitDataJoinImpl.class);
		bind(ImplicitDataJoin.class).to(ImplicitDataJoinImpl.class);
		bind(TriggerPluginQueryCommand.class).to(TriggerPluginQueryCommandImpl.class);
        bind(BuildResult.class).to(BuildResultImpl.class);
		/*
		 * Services
		 */
		bind(CatalogDescriptorService.class).to(CatalogDescriptorServiceImpl.class);
        bind(CatalogDeserializationService.class).to(CatalogDeserializationServiceImpl.class);
        bind(CatalogKeyServices.class).to(CatalogKeyServicesImpl.class);
		bind(EntrySynthesizer.class).to(EntrySynthesizerImpl.class);
		bind(JSRAnnotationsDictionary.class).to(JSRAnnotationsDictionaryImpl.class);

		bind(CatalogTriggerInterpret.class).to(CatalogTriggerInterpretImpl.class);
        bind(ResultSetService.class).to(ResultSetServiceImpl.class);
		bind(KeyDomainValidator.class).to(KeyDomainValidatorImpl.class);

		bind(SystemCatalogPlugin.class).to(SystemCatalogPluginImpl.class);
		bind(CatalogDescriptorBuilder.class).to(CatalogDescriptorBuilderImpl.class);

		bind(CatalogActionRequestValidator.class).to(CatalogActionRequestValidatorImpl.class);


	}

	/*
	 * CONFIGURATION
	 */
    @Provides
    @Singleton
    @Inject
    @Named(Session.CATALOG)
    public CatalogDescriptor session(CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(SessionImpl.class, Session.CATALOG, "Sessions", -1911198,
                null);
        return r;
    }

	@Provides
	@Singleton
	@Inject
	@Named(Constraint.CATALOG_ID)
	public CatalogDescriptor constraint(CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(ConstraintImpl.class, Constraint.CATALOG_ID, "Constraints", -1911199,
				null);
		return r;
	}

	@Provides
	@Singleton
	@Inject
	@Named(Host.CATALOG)
	public CatalogDescriptor catalogPeer(CatalogDescriptorBuilder builder,@Named("catalog.storage.peers") String systemPeers, @Named("catalog.storage") String defaultStorage) {
		CatalogDescriptor r = builder.fromClass(HostImpl.class, Host.CATALOG, "Peers", -1911193, null);
		r.setStorage(Arrays.asList(defaultStorage, systemPeers));
		return r;
	}

    @Provides
	@Singleton
	@Inject
	@Named(ContentNode.CATALOG_TIMELINE)
	public CatalogDescriptor timeline(CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(ContentNodeImpl.class, ContentNode.CATALOG_TIMELINE, "Timeline Contract", -1911192, null);
		return r;
	}

	@Provides
	@Singleton
	@Inject
	@Named(ContentRevision.CATALOG)
	public CatalogDescriptor contentRevision(@Named(ContentRevision.CATALOG) java.lang.Class clazz) {
		CatalogDescriptorImpl regreso = new CatalogDescriptorImpl();
		regreso.setClazz(clazz);
		regreso.setDescriptiveField(CatalogEntry.NAME_FIELD);
		Map<String, FieldDescriptor> fields = new LinkedHashMap<String, FieldDescriptor>();
		FieldDescriptorImpl field;
		field = new FieldDescriptorImpl().makeDefault("catalogId", "Catalog",
				CatalogEntry.STRING_DATA_TYPE);
		fields.put(field.getDistinguishedName(), field);
		field = new FieldDescriptorImpl().makeDefault("catalogEntryId", "Entry",
				CatalogEntry.INTEGER_DATA_TYPE);
		fields.put(field.getDistinguishedName(), field);
		field = new FieldDescriptorImpl().makeKey(HasStakeHolder.STAKE_HOLDER_FIELD, "By", Person.CATALOG, false);
		fields.put(field.getDistinguishedName(), field);
		field = new FieldDescriptorImpl().makeDefault("value", "Value",  CatalogEntry.LARGE_STRING_DATA_TYPE);
		fields.put(field.getDistinguishedName(), field);
		regreso.setFieldsValues(fields);
		regreso.setDistinguishedName(ContentRevision.CATALOG);
		regreso.setId(-178532l);
		regreso.setKeyField(CatalogEntry.ID_FIELD);
		regreso.setName("Revision");
		regreso.setConsolidated(true);
		regreso.setParent(ContentNode.NUMERIC_ID);
		return regreso;
	}

	@Provides
	@Inject
	@Singleton
	@Named(CatalogDescriptor.CATALOG_ID)
    public CatalogDescriptor catalogDescriptor(@Named(CatalogDescriptor.CATALOG_ID) String image, @Named("catalog.storage.metadata") String catalogPluginStorage, @Named("catalog.storage") String defaultStorage, @Named(CatalogDescriptor.CATALOG_ID) Class clazz,
                                               CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(CatalogDescriptorImpl.class, CatalogDescriptor.CATALOG_ID, "Catalogs",
				-191191, null);
		r.setClazz(clazz);
        r.setStorage(Arrays.asList(defaultStorage, catalogPluginStorage));
        r.setImage(image);
		return r;
	}

	@Provides
	@Inject
	@Singleton
	@Named(FieldDescriptor.CATALOG_ID)
	public CatalogDescriptor catalogFieldDescriptor(@Named(FieldDescriptor.CATALOG_ID) Class clazz,
			CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(FieldDescriptorImpl.class, FieldDescriptor.CATALOG_ID, "Catalog Fields",
				-192929162, null);
		r.setClazz(clazz);
		r.getFieldDescriptor("dataType")
				.setDefaultValueOptions(Arrays.asList(String.valueOf(CatalogEntry.BOOLEAN_DATA_TYPE) + "=Boolean",
						String.valueOf(CatalogEntry.DATE_DATA_TYPE) + "=Date",
						String.valueOf(CatalogEntry.INTEGER_DATA_TYPE) + "=Integer",
						String.valueOf(CatalogEntry.NUMERIC_DATA_TYPE) + "=Numeric",
						String.valueOf(CatalogEntry.STRING_DATA_TYPE) + "=String",
						String.valueOf(CatalogEntry.LARGE_STRING_DATA_TYPE) + "=Large String"));
		return r;
	}

	@Provides
	@Inject
	@Singleton
	@Named(Trigger.CATALOG)
	public CatalogDescriptor catalogActionTrigger(@Named(CatalogDescriptor.CATALOG_ID) String image,@Named(Trigger.CATALOG) Class clazz,
												  @Named("catalog.storage.trigger") String catalogPluginStorage,
												  @Named("catalog.storage") String defaultStorage,
			CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(clazz, Trigger.CATALOG,
				"Catalog Trigger", -194949, null);
		r.setStorage(Arrays.asList(defaultStorage, catalogPluginStorage));
		r.setImage(image);
		r.setClazz(clazz);
		return r;
	}

    @Provides
    @Inject
    @Singleton
    @Named(CatalogActionBroadcast.CATALOG)
    public CatalogDescriptor eventCatalog(@Named(CatalogActionBroadcast.CATALOG) Class clazz,
                                          CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(clazz, CatalogActionBroadcast.CATALOG,
                "Catalog Contract", -13939395, null);
        r.setClazz(clazz);
        return r;
    }

    @Provides
    @Inject
    @Singleton
    @Named(CatalogActionFiltering.CATALOG)
    public CatalogDescriptor actionCommit(@Named(CatalogActionFiltering.CATALOG) Class clazz,
                                           CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(CatalogActionFilteringImpl.class, CatalogActionFiltering.CATALOG,
                "Catalog Commit", -13939394, null);
        r.setClazz(clazz);
        return r;
    }

	@Provides
	@Inject
	@Singleton
	@Named(CatalogActionRequest.CATALOG)
	public CatalogDescriptor actionRequest(@Named(CatalogActionRequest.CATALOG) Class clazz,
			CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(CatalogActionRequestImpl.class, CatalogActionRequest.CATALOG,
				"Catalog Request", -13939393, null);
		r.setClazz(clazz);
		return r;
	}

}
