package com.wrupple.muba.catalogs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.inject.Singleton;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.domain.CatalogEvent;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.domain.*;
import com.wrupple.muba.catalogs.server.service.impl.*;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.LongConverter;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.domain.HostImpl;
import com.wrupple.muba.catalogs.server.domain.catalogs.DistributiedLocalizedEntryDescriptor;
import com.wrupple.muba.catalogs.server.domain.catalogs.LocalizedStringDescriptor;
import com.wrupple.muba.catalogs.server.domain.catalogs.TrashDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogActionRequestValidator;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.server.service.KeyDomainValidator;
import com.wrupple.muba.event.server.service.CatalogNormalizationConstraintValidator;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.Deleters;
import com.wrupple.muba.catalogs.server.service.EntryCreators;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyReaders;
import com.wrupple.muba.catalogs.server.service.QueryReaders;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.catalogs.server.service.UserCatalogPlugin;
import com.wrupple.muba.catalogs.server.service.Writers;

/**
 * @author japi
 *
 */
public class CatalogModule extends AbstractModule {

	@Override
	protected void configure() {

	    /*
	     * Event Handlers
	     */
        bind(CatalogServiceManifest.class).to(CatalogServiceManifestImpl.class);
        bind(CatalogActionFilterManifest.class).to(CatalogActionFilterManifestImpl.class);
        bind(CatalogIntentListenerManifest.class).to(CatalogIntentListenerManifestImpl.class);

        bind(CatalogEngine.class).to(CatalogEngineImpl.class);
        bind(CatalogActionFilterEngine.class).to(CatalogActionFilterEngineImpl.class);
        bind(CatalogEventHandler.class).to(CatalogEventHandlerImpl.class);

        bind(CatalogRequestInterpret.class).to(CatalogRequestInterpretImpl.class);
        bind(CatalogActionFilterInterpret.class).to(CatalogActionFilterInterpretImpl.class);
        bind(CatalogEventInterpret.class).to(CatalogEventInterpretImpl.class);
		/*
		 * workarounds / replacement classes
		 */
		ConvertUtils.register(new LongConverter(null), Long.class);
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		//${my.service.invocation}
		String rawPattern = "\\$\\{([A-Za-z0-9]+\\.){0,}[A-Za-z0-9]+\\}";
		// 2014-01-18T00:35:03.463Z
		String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

		bind(Integer.class).annotatedWith(Names.named("catalog.storage.secure")).toInstance(6);
		bind(String.class).annotatedWith(Names.named("template.token.splitter")).toInstance("\\.");
		Pattern pattern = Pattern.compile(rawPattern);
		bind(Pattern.class).annotatedWith(Names.named("template.pattern")).toInstance(pattern);

		bind(String.class).annotatedWith(Names.named("catalog.datePattern")).toInstance(datePattern);
		bind(DateFormat.class).toInstance(new SimpleDateFormat(datePattern));
		bind(String.class).annotatedWith(Names.named("catalog.domainField")).toInstance(CatalogDescriptor.DOMAIN_TOKEN);
		bind(Boolean.class).annotatedWith(Names.named("catalog.createablePrimaryKeys")).toInstance(false);
		bind(Boolean.class).annotatedWith(Names.named("catalog.followGraph")).toInstance(true);
		bind(Integer.class).annotatedWith(Names.named("catalog.read.preloadCatalogGraph")).toInstance(0);


		bind(UserCatalogPlugin.class).to(UserCatalogPluginImpl.class);

		bind(String.class).annotatedWith(Names.named("catalog.ancestorKeyField")).toInstance("superAncestorHIdentity");
		bind(String.class).annotatedWith(Names.named("catalog.timeline.entryDiscriminator"))
				.toInstance("childHIdentity");
		bind(String.class).annotatedWith(Names.named("catalog.timeline.typeDiscriminator")).toInstance("childHType");

		bind(Class.class).annotatedWith(Names.named(DistributiedLocalizedEntry.CATALOG))
				.toInstance(HasAccesablePropertyValues.class);
		bind(Class.class).annotatedWith(Names.named(Constraint.CATALOG_ID)).toInstance(ConstraintImpl.class);
		bind(Class.class).annotatedWith(Names.named(LocalizedString.CATALOG))
				.toInstance(HasAccesablePropertyValues.class);
		bind(Class.class).annotatedWith(Names.named(Trash.CATALOG)).toInstance(HasAccesablePropertyValues.class);
		bind(Class.class).annotatedWith(Names.named(CatalogActionTrigger.CATALOG))
				.toInstance(CatalogActionTriggerImpl.class);
		bind(Class.class).annotatedWith(Names.named(FieldDescriptor.CATALOG_ID)).toInstance(FieldDescriptorImpl.class);
		bind(Class.class).annotatedWith(Names.named(CatalogDescriptor.CATALOG_ID))
				.toInstance(CatalogDescriptorImpl.class);
        bind(Class.class).annotatedWith(Names.named(CatalogActionRequest.CATALOG))
                .toInstance(CatalogActionRequestImpl.class);
        bind(Class.class).annotatedWith(Names.named(CatalogActionCommit.CATALOG))
                .toInstance(CatalogActionCommitImpl.class);
        bind(Class.class).annotatedWith(Names.named(CatalogEvent.CATALOG))
                .toInstance(CatalogEventImpl.class);

		/*
		 * CONFIGURATION
		 */

		bind(NamespaceContext.class).to(NamespaceContextImpl.class);
        bind(CatalogActionCommit.class).to(CatalogActionCommitImpl.class);
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
		bind(EntryCreators.class).to(EntryCreatorsImpl.class);
		bind(PrimaryKeyReaders.class).to(PrimaryKeyReadersImpl.class);
		bind(QueryReaders.class).to(QueryReadersImpl.class);
		bind(Writers.class).to(WritersImpl.class);
		bind(Deleters.class).to(DeletersImpl.class);

		/*
		 * Commands
		 */

		bind(CommitCatalogAction.class).to(CommitCatalogActionImpl.class);

		bind(CatalogCreateTransaction.class).to(CatalogCreateTransactionImpl.class);
		bind(CatalogReadTransaction.class).to(CatalogReadTransactionImpl.class);
		bind(CatalogUpdateTransaction.class).to(CatalogUpdateTransactionImpl.class);
		bind(CatalogDeleteTransaction.class).to(CatalogDeleteTransactionImpl.class);

		bind(EntryDeleteTrigger.class).to(EntryDeleteTriggerImpl.class);
		bind(FieldDescriptorUpdateTrigger.class).to(FieldDescriptorUpdateTriggerImpl.class);
		bind(CatalogDescriptorUpdateTrigger.class).to(CatalogDescriptorUpdateTriggerImpl.class);

		bind(GarbageCollection.class).to(GarbageCollectionImpl.class);
		bind(TrashDeleteTrigger.class).to(TrashDeleteTriggerImpl.class);
		bind(RestoreTrash.class).to(RestoreTrashImpl.class);
		bind(IncreaseVersionNumber.class).to(IncreaseVersionNumberImpl.class);
		bind(Timestamper.class).to(TimestamperImpl.class);
		bind(UpdateTreeLevelIndex.class).to(UpdateTreeLevelIndexImpl.class);
		bind(WritePublicTimelineEventDiscriminator.class).to(WritePublicTimelineEventDiscriminatorImpl.class);
		/*
		 * Services
		 */
		bind(CompleteCatalogGraph.class).to(CompleteCatalogGraphImpl.class);
		bind(ExplicitDataJoin.class).to(ExplicitDataJoinImpl.class);
		bind(ImplicitDataJoin.class).to(ImplicitDataJoinImpl.class);

		bind(CatalogTriggerInterpret.class).to(CatalogTriggerInterpretImpl.class);

		bind(KeyDomainValidator.class).to(KeyDomainValidatorImpl.class);

		bind(SystemCatalogPlugin.class).to(CatalogManagerImpl.class);
		bind(CatalogDescriptorBuilder.class).to(CatalogDescriptorBuilderImpl.class);

		bind(CatalogNormalizationConstraintValidator.class).to(CatalogNormalizationConstraintValidatorImpl.class);
		bind(CatalogActionRequestValidator.class).to(CatalogActionRequestValidatorImpl.class);


	}

	/*
	 * CONFIGURATION
	 */

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
	public CatalogDescriptor catalogPeer(CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(HostImpl.class, Host.CATALOG, "Peers", -1911193, null);
		return r;
	}
    @Provides
	@Singleton
	@Inject
	@Named(ContentNode.CATALOG_TIMELINE)
	public CatalogDescriptor timeline(CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(ContentNodeImpl.class, ContentNode.CATALOG_TIMELINE, "Timeline Event", -1911192, null);
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
		field = new FieldDescriptorImpl().makeDefault("catalogId", "Catalog", "catalogPicker",
				CatalogEntry.STRING_DATA_TYPE);
		fields.put(field.getFieldId(), field);
		field = new FieldDescriptorImpl().makeDefault("catalogEntryId", "Entry", "genericCatalogEntryId",
				CatalogEntry.INTEGER_DATA_TYPE);
		fields.put(field.getFieldId(), field);
		field = new FieldDescriptorImpl().makeKey(HasStakeHolder.STAKE_HOLDER_FIELD, "By", Person.CATALOG, false);
		fields.put(field.getFieldId(), field);
		field = new FieldDescriptorImpl().makeDefault("value", "Value", "text", CatalogEntry.LARGE_STRING_DATA_TYPE);
		fields.put(field.getFieldId(), field);
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
	public CatalogDescriptor catalogDescriptor(@Named(CatalogDescriptor.CATALOG_ID) Class clazz,
			CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(CatalogDescriptorImpl.class, CatalogDescriptor.CATALOG_ID, "Catalogs",
				-191191, null);
		r.setClazz(clazz);
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
	@Named(CatalogActionTrigger.CATALOG)
	public CatalogDescriptor catalogActionTrigger(@Named(CatalogActionTrigger.CATALOG) Class clazz,
			CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(CatalogActionTriggerImpl.class, CatalogActionTrigger.CATALOG,
				"Catalog Trigger", -194949, null);
		r.setClazz(clazz);
		return r;
	}




    @Provides
    @Inject
    @Singleton
    @Named(CatalogEvent.CATALOG)
    public CatalogDescriptor eventCatalog(@Named(CatalogEvent.CATALOG) Class clazz,
                                          CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(CatalogEventImpl.class, CatalogEvent.CATALOG,
                "Catalog Event", -13939395, null);
        r.setClazz(clazz);
        return r;
    }

    @Provides
    @Inject
    @Singleton
    @Named(CatalogActionCommit.CATALOG)
    public CatalogDescriptor actionCommit(@Named(CatalogActionCommit.CATALOG) Class clazz,
                                           CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(CatalogActionCommitImpl.class, CatalogActionCommit.CATALOG,
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
