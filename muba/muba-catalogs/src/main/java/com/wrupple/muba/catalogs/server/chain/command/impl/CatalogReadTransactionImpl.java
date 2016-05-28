package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.HasVanityId;
import com.wrupple.muba.catalogs.domain.VegetateColumnResultSet;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.ResultHandlingService;
import com.wrupple.muba.catalogs.server.service.impl.CatalogCommandImpl;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FilterCriteria;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.chain.CatalogManager;
import com.wrupple.vegetate.server.chain.command.CatalogReadTransaction;
import com.wrupple.vegetate.server.services.PrimaryKeyEncodingService;
import com.wrupple.vegetate.server.services.impl.FilterDataUtils;

@Singleton
public class CatalogReadTransactionImpl extends CatalogCommandImpl implements CatalogReadTransaction {

	public interface JoinCondition {
		boolean match(CatalogEntry o);
	}

	private final ResultHandlingService resultService;

	private final String publicParameter;

	private final PrimaryKeyEncodingService pkes;

	@Inject
	public CatalogReadTransactionImpl(@Named("catalog.publicDiscriminator") String publicParameter, ResultHandlingService genericSummaryBuilder,
			CatalogQueryRewriter queryRewriter, Provider<CatalogResultCache> cacheProvider, Provider<CatalogActionTriggerHandler> trigererProvider,
			CatalogPropertyAccesor accessor, DatabasePlugin daoFactory, PrimaryKeyEncodingService pkes) {
		super(queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
		this.resultService = genericSummaryBuilder;
		this.publicParameter = publicParameter;
		this.pkes=pkes;
	}

	@Override
	public boolean execute(Context x) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) x;
		/*
		 * READING ACTION (lucky!)
		 */
		CatalogDataAccessObject<CatalogEntry> dao = getOrAssembleDataSource(context.getCatalogDescriptor(), context, CatalogEntry.class);
		String targetEntryId = (String) context.get(CatalogActionRequest.CATALOG_ENTRY_PARAMETER);
		if (targetEntryId == null) {
			FilterData filter = context.getFilter();
			if (filter == null) {
				throw new IllegalArgumentException("Neither filter data, nor target entry id was specified for this read operation");
			}

			List<CatalogEntry> result = null;
			FilterCriteria keyCriteria = filter.fetchCriteria(CatalogEntry.ID_FIELD);
			if (!filter.isConstrained() || keyCriteria == null) {
				result = dao.read(filter);
			} else {
				List<Object> keys = keyCriteria.getValues();
				if (keys == null) {
					context.getRequest().addWarning("malformed criteria");
				} else {

					if (filter.getCursor() == null) {
						int ammountOfKeys = keys.size();
						// only if theres still some unsatisfied idś in criteria
						if (filter.getStart() < ammountOfKeys) {
							result = dao.read(filter);
						}
					} else {
						result = dao.read(filter);
					}
				}
			}

			log.trace("[RESULT ] {}", result);
			context.addResuls(result);

			String[][] joins = filter.getJoins();
			if (joins != null && joins.length > 0) {

				List<VegetateColumnResultSet> joinData = resultService.explicitJoin(context);
				context.put(CatalogEngine.JOINED_DATA, joinData);
			}
		} else {
			CatalogEntry originalEntry = dao.read(targetEntryId);
			log.trace("[RESULT ] {}", originalEntry);
			context.addResult(originalEntry);
		}
		return CONTINUE_PROCESSING;
	}

	private CatalogEntry readVanityId(String vanityId, CatalogDescriptor catalog, CatalogExcecutionContext context) throws Exception {
		CatalogEntry regreso = null;
		/*
		 * move logi cto catalog read transaction or Query Rewriter
		 */
		CatalogDataAccessObject<CatalogEntry> dao = getDSM().getOrAssembleDataSource(catalog, context, CatalogEntry.class);
		if (pkes.isPrimaryKey(vanityId)) {
			// almost certainly an Id
			Object primaryId = pkes.decodePrimaryKeyToken(vanityId, catalog);
			regreso = dao.read(primaryId);
		}

		if (regreso == null) {
			FilterData filter;
			List<CatalogEntry> results;
			// if(catalog.getFieldDescriptor(HasVanityId.FIELD)==null){
			filter = FilterDataUtils.createSingleFieldFilter(HasVanityId.FIELD, vanityId);
			results = dao.read(filter);
			/*
			 * }else{ throw new illegalArgumentException }
			 */
			if (results != null && !results.isEmpty()) {
				regreso = results.get(0);
			}
		}

		return regreso;
	}


	/*
	 * public static String buildVanityToken(HasVanityId task) {
	 * 
	 * String name = task.getVanityId(); if (name == null) { name =
	 * task.getName(); if (name == null) { name = task.getIdAsString(); } else {
	 * //[^a-zA-Z0-9/] replace all except / name =
	 * name.replaceAll("[^a-zA-Z0-9]", "-"); } }
	 * 
	 * return name; }
	 * 
	 * public static String getNameFromVanityToken(String vanityToken){ return
	 * vanityToken.replace('-',' '); }
	 */

}
