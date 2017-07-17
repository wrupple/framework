package com.wrupple.muba.catalogs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.inject.Singleton;
import com.wrupple.muba.catalogs.server.service.impl.*;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
import com.wrupple.muba.catalogs.shared.service.FilterNativeInterface;
import com.wrupple.muba.catalogs.shared.service.ObjectNativeInterface;
import com.wrupple.muba.catalogs.shared.service.impl.JavaFilterNativeInterfaceImpl;
import com.wrupple.muba.catalogs.shared.service.impl.JavaObjectNativeInterface;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.chain.CatalogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasAccesablePropertyValues;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.reserved.HasStakeHolder;
import com.wrupple.muba.catalogs.domain.CatalogActionTrigger;
import com.wrupple.muba.catalogs.domain.CatalogActionTriggerImpl;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogPeer;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.domain.Constraint;
import com.wrupple.muba.catalogs.domain.ConstraintImpl;
import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.muba.catalogs.domain.ContentRevision;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.LocalizedString;
import com.wrupple.muba.catalogs.domain.NamespaceContext;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntityImpl;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.CatalogActionTriggerHandler;
import com.wrupple.muba.catalogs.server.chain.command.CommitCatalogAction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDeleteTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.catalogs.server.chain.command.EntryDeleteTrigger;
import com.wrupple.muba.catalogs.server.chain.command.ExplicitDataJoin;
import com.wrupple.muba.catalogs.server.chain.command.FieldDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.chain.command.GarbageCollection;
import com.wrupple.muba.catalogs.server.chain.command.ImplicitDataJoin;
import com.wrupple.muba.catalogs.server.chain.command.IncreaseVersionNumber;
import com.wrupple.muba.catalogs.server.chain.command.PublishEvents;
import com.wrupple.muba.catalogs.server.chain.command.RestoreTrash;
import com.wrupple.muba.catalogs.server.chain.command.Timestamper;
import com.wrupple.muba.catalogs.server.chain.command.TrashDeleteTrigger;
import com.wrupple.muba.catalogs.server.chain.command.UpdateTreeLevelIndex;
import com.wrupple.muba.catalogs.server.chain.command.WritePublicTimelineEventDiscriminator;
import com.wrupple.muba.catalogs.server.chain.command.impl.CommitCatalogActionImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogCreateTransactionImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogDeleteTransactionImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogDescriptorUpdateTriggerImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogEngineImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogReadTransactionImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogRequestInterpretImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogUpdateTransactionImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CompleteCatalogGraphImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.EntryDeleteTriggerImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.ExplicitDataJoinImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.FieldDescriptorUpdateTriggerImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.GarbageCollectionImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.ImplicitDataJoinImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.IncreaseVersionNumberImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.PublishEventsImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.RestoreTrashImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.TimestamperImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.TrashDeleteTriggerImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.UpdateTreeLevelIndexImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.WritePublicTimelineEventDiscriminatorImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogPeerImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogServiceManifestImpl;
import com.wrupple.muba.catalogs.server.domain.FieldDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.catalogs.DistributiedLocalizedEntryDescriptor;
import com.wrupple.muba.catalogs.server.domain.catalogs.LocalizedStringDescriptor;
import com.wrupple.muba.catalogs.server.domain.catalogs.TrashDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogActionRequestValidator;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogKeyConstraintValidator;
import com.wrupple.muba.catalogs.server.service.CatalogNormalizationConstraintValidator;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.Deleters;
import com.wrupple.muba.catalogs.server.service.EntryCreators;
import com.wrupple.muba.catalogs.server.service.LargeStringFieldDataAccessObject;
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
		 * workarounds / replacement classes
		 */
		ConvertUtils.register(new LongConverter(null), Long.class);
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		//${my.service.invocation}
		String rawPattern = "\\$\\{([A-Za-z0-9]+\\.){0,}[A-Za-z0-9]+\\}";
		// 2014-01-18T00:35:03.463Z
		String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

		bind(String.class).annotatedWith(Names.named("template.token.splitter")).toInstance("\\.");
		Pattern pattern = Pattern.compile(rawPattern);
		bind(Pattern.class).annotatedWith(Names.named("template.pattern")).toInstance(pattern);

		bind(String.class).annotatedWith(Names.named("catalog.datePattern")).toInstance(datePattern);
		bind(DateFormat.class).toInstance(new SimpleDateFormat(datePattern));
		bind(String.class).annotatedWith(Names.named("catalog.domainField")).toInstance(CatalogDescriptor.DOMAIN_TOKEN);
		bind(Boolean.class).annotatedWith(Names.named("catalog.createablePrimaryKeys")).toInstance(false);
		bind(Boolean.class).annotatedWith(Names.named("catalog.followGraph")).toInstance(true);
		bind(Integer.class).annotatedWith(Names.named("catalog.read.preloadCatalogGraph")).toInstance(0);

		bind(PersistentCatalogEntity.class).to(PersistentCatalogEntityImpl.class);
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
		bind(PersistentCatalogEntity.class).to(PersistentCatalogEntityImpl.class);
		/*
		 * CONFIGURATION
		 */

		bind(NamespaceContext.class).to(NamespaceContextImpl.class);

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
		bind(CatalogEngine.class).to(CatalogEngineImpl.class);
		bind(CatalogRequestInterpret.class).to(CatalogRequestInterpretImpl.class);
		bind(CommitCatalogAction.class).to(CommitCatalogActionImpl.class);

		bind(CatalogCreateTransaction.class).to(CatalogCreateTransactionImpl.class);
		bind(CatalogReadTransaction.class).to(CatalogReadTransactionImpl.class);
		bind(CatalogUpdateTransaction.class).to(CatalogUpdateTransactionImpl.class);
		bind(CatalogDeleteTransaction.class).to(CatalogDeleteTransactionImpl.class);

		bind(PublishEvents.class).to(PublishEventsImpl.class);

		bind(CatalogActionTriggerHandler.class).to(CatalogActionTriggerHandlerImpl.class);

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

		bind(LargeStringFieldDataAccessObject.class).to(LargeStringFieldDataAccessObjectImpl.class);

		bind(CatalogKeyConstraintValidator.class).to(CatalogKeyConstraintValidatorImpl.class);
		bind(CatalogServiceManifest.class).to(CatalogServiceManifestImpl.class);

		bind(SystemCatalogPlugin.class).to(CatalogManagerImpl.class);
		bind(CatalogDescriptorBuilder.class).to(CatalogDescriptorBuilderImpl.class);

		bind(CatalogNormalizationConstraintValidator.class).to(CatalogNormalizationConstraintValidatorImpl.class);
		bind(CatalogActionRequestValidator.class).to(CatalogActionRequestValidatorImpl.class);

		/*
		Runtime
		 */
		bind(ObjectNativeInterface.class).to(JavaObjectNativeInterface.class);
		bind(FieldAccessStrategy.class).to(JavaFieldAccessStrategy.class);
		bind(FilterNativeInterface.class).to(JavaFilterNativeInterfaceImpl.class);
	}

	/*
	 * CONFIGURATION
	 */

	@Provides
	@Singleton
	@Inject
	@Named(Constraint.CATALOG_ID)
	public CatalogDescriptor constraint(CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(ConstraintImpl.class, Constraint.CATALOG_ID, "Constraints", -1911192,
				null);
		return r;
	}

	@Provides
	@Singleton
	@Inject
	@Named(CatalogPeer.CATALOG)
	public CatalogDescriptor catalogPeer(CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(CatalogPeerImpl.class, CatalogPeer.CATALOG, "Peers", -1911193, null);
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
	@Named(CatalogActionRequest.CATALOG)
	public CatalogDescriptor actionRequest(@Named(CatalogActionRequest.CATALOG) Class clazz,
			CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(CatalogActionRequestImpl.class, CatalogActionRequest.CATALOG,
				"Catalog Action", -13939393, null);
		r.setClazz(clazz);
		return r;
	}

}
