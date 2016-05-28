package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.domain.VegetateColumnResultSet;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.ResultHandlingService;
import com.wrupple.muba.catalogs.server.service.impl.CatalogCommandImpl;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.server.chain.command.CatalogDeleteTransaction;

@Singleton
public class CatalogDeleteTransactionImpl extends CatalogCommandImpl implements CatalogDeleteTransaction {
	private final ResultHandlingService resultService;
	private boolean avoidPost;

	@Inject
	public CatalogDeleteTransactionImpl(ResultHandlingService resultService, DatabasePlugin daoFactory, CatalogPropertyAccesor accessor,
			CatalogQueryRewriter queryRewriter, Provider<CatalogResultCache> cacheProvider, Provider<CatalogActionTriggerHandler> trigererProvider) {
		super(queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
		this.resultService = resultService;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		log.trace("[DELETE START]");
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		Object targetEntryId = context.getEntry();
		CatalogDescriptor catalog = context.getCatalogDescriptor();

		FieldDescriptor trashableField = catalog.getFieldDescriptor(Trash.TRASH_FIELD);

		CatalogEntry originalEntry;
		if (trashableField != null && trashableField.getDataType() == CatalogEntry.BOOLEAN_DATA_TYPE && context.getDomainContext().isRecycleBinEnabled()) {
			originalEntry = trash(catalog, CatalogEntry.class, targetEntryId, trashableField, context);
		} else {
			CatalogDataAccessObject<CatalogEntry> dao = getOrAssembleDataSource(catalog, context, CatalogEntry.class);
			originalEntry = dao.delete(dao.read(targetEntryId));
		}

		context.addResult(originalEntry);

		if (!avoidPost) {
			VegetateColumnResultSet mainResultSet = resultService.createResultSet(false, context);
			context.put(CatalogEngine.WORKING_RESULT_SET, mainResultSet);
		}

		log.debug("[END] deleted: {}", originalEntry);
		return CONTINUE_PROCESSING;
	}

	@Override
	public void setDontBotherToPostProcess(boolean b) {
		this.avoidPost = b;
	}

}
