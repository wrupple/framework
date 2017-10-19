package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.catalogs.domain.CatalogActionCommit;
import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataWritingCommand;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.Writers;
import com.wrupple.muba.event.server.service.ActionsDictionary;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.wrupple.muba.catalogs.domain.CatalogEvent.WRITE_ACTION;

@Singleton
public class CatalogUpdateTransactionImpl extends CatalogTransaction implements CatalogUpdateTransaction {

	protected static final Logger log = LoggerFactory.getLogger(CatalogUpdateTransactionImpl.class);

	private final Writers writers;
	private final ActionsDictionary dictionary;
	private final EntrySynthesizer snth;
	private final FieldAccessStrategy access;

	@Inject
	public CatalogUpdateTransactionImpl(ActionsDictionary dictionary, Provider<CatalogActionCommit> catalogActionCommitProvider,
                                        Writers writers, EntrySynthesizer snth, FieldAccessStrategy access) {
		super(catalogActionCommitProvider);
		this.writers = writers;
		this.dictionary=dictionary;
        this.snth = snth;
        this.access = access;
    }

	@Override
	public boolean execute(Context c) throws Exception {
		log.trace("<{}>",this.getClass().getSimpleName());
		CatalogActionContext context = (CatalogActionContext) c;
		dictionary.getRead().execute(context);

		CatalogEntry originalEntry = context.getResult();

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
		if (snth.evaluateGreatAncestor(context,catalog,null)!= null && !catalog.getConsolidated()) {
            instrospection = access.newSession(originalEntry);
            // we are certain this catalog has a parent, otherwise this DAO
			// would
			// not be called
			CatalogEntry updatedEntry = (CatalogEntry) context.getRequest().getEntryValue();
			parentCatalog = context.getDescriptorForKey(catalog.getParent());
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

		CatalogResultCache cache = context.getCache(context.getCatalogDescriptor(), context);
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
