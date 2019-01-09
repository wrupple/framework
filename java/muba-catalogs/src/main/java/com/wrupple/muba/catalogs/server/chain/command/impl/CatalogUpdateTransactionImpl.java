package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.catalogs.domain.CatalogActionFiltering;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorService;
import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataWritingCommand;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.Writers;
import com.wrupple.muba.event.domain.impl.CatalogReadRequestImpl;
import com.wrupple.muba.event.server.service.ActionsDictionary;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.wrupple.muba.catalogs.domain.CatalogActionBroadcast.WRITE_ACTION;

@Singleton
public class CatalogUpdateTransactionImpl  implements CatalogUpdateTransaction {

	protected static final Logger log = LogManager.getLogger(CatalogUpdateTransactionImpl.class);

	private final Writers writers;
	private final EntrySynthesizer snth;
	private final FieldAccessStrategy access;
	private final CatalogDescriptorService catalogService;

	@Inject
	public CatalogUpdateTransactionImpl(
										Writers writers, EntrySynthesizer snth, FieldAccessStrategy access, CatalogDescriptorService catalogService) {
		this.writers = writers;
        this.snth = snth;
        this.access = access;
		this.catalogService = catalogService;
	}

	@Override
	public boolean execute(CatalogActionContext context) throws Exception {
        Object parentEntityId = context.getRequest().getEntry();
        CatalogDescriptor catalog = context.getCatalogDescriptor();
        CatalogReadRequestImpl requestOldValue = new CatalogReadRequestImpl(parentEntityId,catalog);
        requestOldValue.setFollowReferences(false);
        CatalogEntry originalEntry= context.getRuntimeContext().getServiceBus().fireEvent(requestOldValue,context.getRuntimeContext(),null);
        Instrospection instrospection = access.newSession(originalEntry);
        Object originalId = originalEntry.getId();
        CatalogEntry updatedEntry = (CatalogEntry) context.getRequest().getEntryValue();
        String keyField = catalog.getKeyField();
        access.setPropertyValue(keyField,updatedEntry,originalId,instrospection);
        updatedEntry.setDomain(originalEntry.getDomain());

        context.setOldValue(originalEntry);


		DataWritingCommand dao = (DataWritingCommand) writers.getCommand(String.valueOf(catalog.getStorage()));
		CatalogEntry childEntity = null;
		CatalogDescriptor parentCatalog = null;

		if (snth.evaluateGreatAncestor(context,catalog,null)!= null && !catalog.getConsolidated()) {

            // we are certain this catalog has a parent, otherwise this DAO
			// would
			// not be called
			parentCatalog = catalogService.getDescriptorForKey(catalog.getParent(),context);
			parentEntityId = snth.getAllegedParentId(originalEntry, instrospection,access);

			// synthesize parent entity from all non-inherited, passing all
			// inherited field Values
			CatalogEntry updatedParentEntity = snth.synthesizeCatalogObject(updatedEntry, catalog, false, instrospection,
					context);
			// delegate deeper getInheritance to another instance of an
			// AncestorAware
			// DAO
			//CatalogEntry originalParentEntity = context.getCatalogManager().readEntry(parentCatalog, parentEntityId, childContext);
			CatalogEntry originalParentEntity = context.triggerGet(parentCatalog.getDistinguishedName(),parentEntityId);

			updatedParentEntity = context.triggerWrite(parentCatalog.getDistinguishedName(),originalParentEntity.getId(),updatedParentEntity);

			// synthesize childEntity (Always will be Entity Kind) ignoring all
			// inheritedFields
			childEntity = snth.synthesizeCatalogObject(updatedEntry, catalog, true, instrospection, context);
		}
		dao.execute(context);

		if (snth.evaluateGreatAncestor(context,catalog,null)!= null && !catalog.getConsolidated()) {
			// add inherited values to child Entity (result)
            snth.processChildInheritance(childEntity, parentCatalog, parentEntityId,
					context, catalog, instrospection);
		}
		CatalogEntry ress = context.getEntryResult();
		context.getRuntimeContext().getTransactionHistory().didUpdate(context,ress , context.getOldValue(), dao);

		CatalogResultCache cache = context.getCache(catalog, context);
		if (cache != null) {
			cache.delete(context,catalog.getDistinguishedName(),originalId);
		}
		return CONTINUE_PROCESSING;
	}

}
