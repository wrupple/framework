package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.catalogs.domain.CatalogActionCommit;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataWritingCommand;
import com.wrupple.muba.catalogs.server.domain.CatalogEventImpl;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.Writers;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.wrupple.muba.catalogs.domain.CatalogEvent.CREATE_ACTION;
import static com.wrupple.muba.catalogs.domain.CatalogEvent.DELETE_ACTION;
import static com.wrupple.muba.catalogs.domain.CatalogEvent.WRITE_ACTION;

@Singleton
public class CatalogUpdateTransactionImpl extends CatalogTransaction implements CatalogUpdateTransaction {
	protected static final Logger log = LoggerFactory.getLogger(CatalogUpdateTransactionImpl.class);


	

	private final Writers writers;

	@Inject
	public CatalogUpdateTransactionImpl(Provider<CatalogActionCommit> catalogActionCommitProvider,
			Writers writers) {
		super(catalogActionCommitProvider);
		this.writers = writers;
		
	}

	@Override
	public boolean execute(Context c) throws Exception {
		log.trace("<{}>",this.getClass().getSimpleName());
		CatalogActionContext context = (CatalogActionContext) c;
		context.getCatalogManager().getRead().execute(context);
		CatalogEntry originalEntry = context.getEntryResult();
		context.setOldValue(originalEntry);

		CatalogDescriptor catalog = context.getCatalogDescriptor();


		log.debug("<CatalogActionFilter>");
		preprocess(context,WRITE_ACTION);
		log.debug("</CatalogActionFilter>");

		DataWritingCommand dao = (DataWritingCommand) writers.getCommand(String.valueOf(catalog.getStorage()));
		CatalogEntry childEntity = null;
		Instrospection instrospection = null;
		CatalogDescriptor parentCatalog = null;
		Object parentEntityId = null;
		if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {
            instrospection = context.getCatalogManager().access().newSession(originalEntry);
            // we are certain this catalog has a parent, otherwise this DAO
			// would
			// not be called
			CatalogEntry updatedEntry = (CatalogEntry) context.getEntryValue();
			parentCatalog = context.getCatalogManager().getDescriptorForKey(catalog.getParent(), context);
			parentEntityId = context.getCatalogManager().getAllegedParentId(originalEntry, instrospection);

			// synthesize parent entity from all non-inherited, passing all
			// inherited field Values
			CatalogEntry updatedParentEntity = context.getCatalogManager().synthesizeCatalogObject(updatedEntry, catalog, false, instrospection,
					context);
			// delegate deeper inheritance to another instance of an
			// AncestorAware
			// DAO
			CatalogActionContext childContext = context.getCatalogManager().spawn(context);
			CatalogEntry originalParentEntity = context.getCatalogManager().readEntry(parentCatalog, parentEntityId, childContext);
			childContext.setEntry(originalParentEntity.getId());
			childContext.setEntryValue(updatedParentEntity);
			context.getCatalogManager().getWrite().execute(childContext);
			updatedParentEntity = context.getEntryResult();

			// synthesize childEntity (Always will be Entity Kind) ignoring all
			// inheritedFields
			childEntity = context.getCatalogManager().synthesizeCatalogObject(updatedEntry, catalog, true, instrospection, context);
		}
		dao.execute(context);

		if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {
			// add inherited values to child Entity (result)
			context.getCatalogManager().processChild(childEntity, parentCatalog, parentEntityId,
					context.getCatalogManager().spawn(context), catalog, instrospection);
		}
		CatalogEntry ress = context.getEntryResult();
		context.getTransactionHistory().didUpdate(context,ress , context.getOldValue(), dao);

		CatalogResultCache cache = context.getCatalogManager().getCache(context.getCatalogDescriptor(), context);
		if (cache != null) {
			cache.update(context, catalog.getDistinguishedName(), context.getOldValue(), ress);
		}
		ress = context.getEntryResult();
		log.debug("<CatalogActionEvent-Broadcast>");
		postProcess(context,catalog.getDistinguishedName(),WRITE_ACTION,ress);
		log.debug("</CatalogActionEvent-Broadcast>");



		log.trace("</{}>",this.getClass().getSimpleName());
		return CONTINUE_PROCESSING;
	}

}
