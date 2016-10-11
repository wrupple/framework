package com.wrupple.muba.catalogs;

import java.util.Arrays;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionTrigger;
import com.wrupple.muba.catalogs.domain.CatalogActionTriggerImpl;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogPeer;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldConstraint;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.LocalizedString;
import com.wrupple.muba.catalogs.domain.NamespaceContext;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCommand;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.chain.command.EntryDeleteTrigger;
import com.wrupple.muba.catalogs.server.chain.command.FieldDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.chain.command.GarbageCollection;
import com.wrupple.muba.catalogs.server.chain.command.TrashDeleteTrigger;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogDescriptorUpdateTriggerImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogEngineImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogRequestInterpretImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.EntryDeleteTriggerImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.FieldDescriptorUpdateTriggerImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.GarbageCollectionImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.RestoreTrashImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.TrashDeleteTriggerImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogPeerImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogServiceManifestImpl;
import com.wrupple.muba.catalogs.server.domain.FieldDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.catalogs.DistributiedLocalizedEntryDescriptor;
import com.wrupple.muba.catalogs.server.domain.catalogs.FieldConstraintContract;
import com.wrupple.muba.catalogs.server.domain.catalogs.LocalizedStringDescriptor;
import com.wrupple.muba.catalogs.server.domain.catalogs.TrashDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogManager;
import com.wrupple.muba.catalogs.server.service.CatalogNormalizationConstraintValidator;
import com.wrupple.muba.catalogs.server.service.RestoreTrash;
import com.wrupple.muba.catalogs.server.service.impl.CatalogDescriptorBuilderImpl;
import com.wrupple.muba.catalogs.server.service.impl.CatalogManagerImpl;
import com.wrupple.muba.catalogs.server.service.impl.CatalogNormalizationConstraintValidatorImpl;
import com.wrupple.muba.catalogs.server.service.impl.NamespaceContextImpl;

/**
 * @author japi
 *
 */
public class CatalogModule extends DatabaseModule {
	// pluggable! FIXME delete all catalogs of a domain when domain is dropped
	// FIXME Clean entities with no corresponding catalog in namespace
	// https://cloud.google.com/appengine/docs/java/datastore/metadataqueries?csw=1#Namespace_Queries
	@Override
	protected void configure() {
		super.configure();

		/*
		 * CONFIGURATION
		 */

		bind(NamespaceContext.class).to(NamespaceContextImpl.class);

		bind(CatalogDescriptor.class).annotatedWith(Names.named(FieldConstraint.CATALOG_ID))
				.to(FieldConstraintContract.class);
		bind(CatalogDescriptor.class).annotatedWith(Names.named(DistributiedLocalizedEntry.CATALOG))
				.to(DistributiedLocalizedEntryDescriptor.class);
		bind(CatalogDescriptor.class).annotatedWith(Names.named(LocalizedString.CATALOG))
				.to(LocalizedStringDescriptor.class);
		bind(CatalogDescriptor.class).annotatedWith(Names.named(Trash.CATALOG)).to(TrashDescriptor.class);

		/*
		 * Commands
		 */
		bind(CatalogEngine.class).to(CatalogEngineImpl.class);
		bind(CatalogRequestInterpret.class).to(CatalogRequestInterpretImpl.class);
		bind(CatalogCommand.class).to(CatalogCommandImpl.class);

		bind(EntryDeleteTrigger.class).to(EntryDeleteTriggerImpl.class);
		bind(FieldDescriptorUpdateTrigger.class).to(FieldDescriptorUpdateTriggerImpl.class);
		bind(CatalogDescriptorUpdateTrigger.class).to(CatalogDescriptorUpdateTriggerImpl.class);
		bind(GarbageCollection.class).to(GarbageCollectionImpl.class);
		bind(TrashDeleteTrigger.class).to(TrashDeleteTriggerImpl.class);
		bind(RestoreTrash.class).to(RestoreTrashImpl.class);

		/*
		 * Services
		 */
		bind(CatalogServiceManifest.class).to(CatalogServiceManifestImpl.class);

		bind(CatalogManager.class).to(CatalogManagerImpl.class);
		bind(CatalogDescriptorBuilder.class).to(CatalogDescriptorBuilderImpl.class);

		bind(CatalogNormalizationConstraintValidator.class).to(CatalogNormalizationConstraintValidatorImpl.class);
	}

	/*
	 * CONFIGURATION
	 */

	@Provides
	@Inject
	@Named(CatalogPeer.CATALOG)
	public CatalogDescriptor catalogPeer(
			CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(CatalogPeerImpl.class, CatalogDescriptor.CATALOG_ID, "Catalogs",
				-1911191);
		return r;
	}

	
	@Provides
	@Inject
	@Named(CatalogDescriptor.CATALOG_ID)
	public CatalogDescriptor catalogDescriptor(@Named(CatalogDescriptor.CATALOG_ID) Class clazz,
			CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(CatalogDescriptorImpl.class, CatalogDescriptor.CATALOG_ID, "Catalogs",
				-191191);
		r.setClazz(clazz.getCanonicalName());
		return r;
	}

	@Provides
	@Inject
	@Named(FieldDescriptor.CATALOG_ID)
	public CatalogDescriptor catalogFieldDescriptor(@Named(FieldDescriptor.CATALOG_ID) Class clazz,
			CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(FieldDescriptorImpl.class, CatalogDescriptor.CATALOG_ID,
				"Catalog Fields", -1929292);
		r.setClazz(clazz.getCanonicalName());
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
	@Named(CatalogActionTrigger.CATALOG)
	public CatalogDescriptor catalogActionTrigger(@Named(CatalogActionTrigger.CATALOG) Class clazz,
			CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(CatalogActionTriggerImpl.class, CatalogActionTrigger.CATALOG,
				"Catalog Trigger", -194949);
		r.setClazz(clazz.getCanonicalName());
		return r;
	}

	@Provides
	@Inject
	@Named(CatalogActionRequest.CATALOG)
	public CatalogDescriptor actionRequest(@Named(CatalogActionRequest.CATALOG) Class clazz,
			CatalogDescriptorBuilder builder) {
		CatalogDescriptor r = builder.fromClass(CatalogActionRequestImpl.class, CatalogActionRequest.CATALOG,
				"Catalog Action", -13939393);
		r.setClazz(clazz.getCanonicalName());
		return r;
	}

}
