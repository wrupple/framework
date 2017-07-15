package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogActionTriggerHandler;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataWritingCommand;
import com.wrupple.muba.catalogs.server.domain.CatalogChangeEventImpl;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.Writers;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy.Session;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CatalogUpdateTransactionImpl implements CatalogUpdateTransaction {
	protected static final Logger log = LoggerFactory.getLogger(CatalogUpdateTransactionImpl.class);

	private final CatalogActionTriggerHandler trigerer;

	

	private final Writers writers;

	@Inject
	public CatalogUpdateTransactionImpl( CatalogActionTriggerHandler trigerer,
			Writers writers) {
		this.trigerer = trigerer;
		this.writers = writers;
		
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		context.getCatalogManager().getRead().execute(context);
		CatalogEntry originalEntry = context.getEntryResult();
		context.setOldValue(originalEntry);

		CatalogDescriptor catalog = context.getCatalogDescriptor();
		context.setAction(CatalogActionRequest.WRITE_ACTION);
		trigerer.execute(context);

		DataWritingCommand dao = (DataWritingCommand) writers.getCommand(String.valueOf(catalog.getStorage()));
		CatalogEntry childEntity = null;
		Session session = null;
		CatalogDescriptor parentCatalog = null;
		Object parentEntityId = null;
		if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {
            session = context.getCatalogManager().access().newSession(originalEntry);
            // we are certain this catalog has a parent, otherwise this DAO
			// would
			// not be called
			CatalogEntry updatedEntry = (CatalogEntry) context.getEntryValue();
			parentCatalog = context.getCatalogManager().getDescriptorForKey(catalog.getParent(), context);
			parentEntityId = context.getCatalogManager().getAllegedParentId(originalEntry, session);

			// synthesize parent entity from all non-inherited, passing all
			// inherited field Values
			CatalogEntry updatedParentEntity = context.getCatalogManager().synthesizeCatalogObject(updatedEntry, catalog, false, session,
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
			childEntity = context.getCatalogManager().synthesizeCatalogObject(updatedEntry, catalog, true, session, context);
		}
		dao.execute(context);

		if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {
			// add inherited values to child Entity (result)
			context.getCatalogManager().processChild(childEntity, parentCatalog, parentEntityId,
					context.getCatalogManager().spawn(context), catalog, session);
		}
		CatalogEntry ress = context.getEntryResult();
		context.getTransactionHistory().didUpdate(context,ress , context.getOldValue(), dao);
		trigerer.postprocess(context, context.getExcecutionContext().getCaughtException());
		CatalogResultCache cache = context.getCatalogManager().getCache(context.getCatalogDescriptor(), context);
		if (cache != null) {
			cache.update(context, catalog.getDistinguishedName(), context.getOldValue(), ress);
		}
		 ress = context.getEntryResult();
		context.getRootAncestor().addBroadcastable(new CatalogChangeEventImpl((Long)context.getDomain(),
				(String) context.getCatalog(), CatalogActionRequest.WRITE_ACTION, ress));

		log.trace("[UPDATED] {}", context.getEntryResult());
		return CONTINUE_PROCESSING;
	}

}
