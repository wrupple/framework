package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.catalogs.server.chain.command.ExplicitDataJoin;
import com.wrupple.muba.catalogs.server.service.CatalogReaderInterceptor;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyReaders;
import com.wrupple.muba.catalogs.server.service.QueryReaders;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.event.domain.Instrospection;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Singleton
public class CatalogReadTransactionImpl implements CatalogReadTransaction {

	protected static final Logger log = LoggerFactory.getLogger(CatalogReadTransactionImpl.class);

	public interface JoinCondition {
		boolean match(CatalogEntry o);
	}

	private final CompleteCatalogGraph graphJoin;

	private final CatalogReaderInterceptor queryRewriter;

	private final ExplicitDataJoin join;

	// DataReadCommandImpl
	private final PrimaryKeyReaders primaryKeyers;
	// DataQueryCommandImpl
	private final QueryReaders queryers;

	private int MIN_TREE_LEVELS;

	@Inject
	public CatalogReadTransactionImpl(@Named("catalog.read.preloadCatalogGraph") Integer minLevelsDeepOfhierarchy,
			QueryReaders queryers, PrimaryKeyReaders primaryKeyers, CompleteCatalogGraph graphJoin,
			ExplicitDataJoin join, CatalogReaderInterceptor queryRewriter) {
		this.graphJoin = graphJoin;
		this.join = join;
		this.MIN_TREE_LEVELS = minLevelsDeepOfhierarchy;
		this.queryers = queryers;
		this.primaryKeyers = primaryKeyers;
		this.queryRewriter = queryRewriter;

	}

	/*
	 * READING ACTION (lucky!)?
	 */
	@Override
	public boolean execute(Context x) throws Exception {
		log.trace("[START READ]");
		CatalogActionContext context = (CatalogActionContext) x;
		String catalogId = (String) context.getRequest().getCatalog();
		if (catalogId == null) {
			log.trace("[GET AVAILABLE CATALOG_TIMELINE LIST]");
			// list all domain catalogs
			context.setResults(context.getCatalogManager().getAvailableCatalogs(context));
			return CONTINUE_PROCESSING;
		}
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		CatalogResultCache cache = context.getCatalogManager().getCache(context.getCatalogDescriptor(), context);

		Object targetEntryId = context.getRequest().getEntry();
        Instrospection instrospection = context.getCatalogManager().access().newSession(null);
        if (targetEntryId == null) {
			FilterData filter = context.getRequest().getFilter();
			applySorts(filter, catalog.getAppliedSorts());
			applyCriteria(filter, catalog, catalog.getAppliedCriteria(), context, instrospection);
			List<CatalogEntry> result = null;
			FilterCriteria keyCriteria = filter.fetchCriteria(CatalogEntry.ID_FIELD);
			if (!filter.isConstrained() || keyCriteria == null) {
				result = read(filter, catalog, context, cache, instrospection);
			} else {
				List<Object> keys = keyCriteria.getValues();
				if (keys == null) {
					context.getRuntimeContext().addWarning("malformed criteria");
				} else {

					if (filter.getCursor() == null) {
						int ammountOfKeys = keys.size();
						// only if theres still some unsatisfied idś in criteria
						if (filter.getStart() < ammountOfKeys) {
							result = read(filter, catalog, context, cache, instrospection);
						}
					} else {
						result = read(filter, catalog, context, cache, instrospection);
					}
				}
			}
			if (result != null && result.isEmpty()) {
				result = null;
			}

			log.trace("[RESULT ] {}", result);
			context.setResults(result);

			String[][] joins = filter.getJoins();
			if (joins != null && joins.length > 0) {
				join.execute(context);
			} else if (MIN_TREE_LEVELS > 0 || context.getRequest().getFollowReferences()) {// interceptor
																				// decides
																				// to
																				// read
																				// graph
				graphJoin.execute(context);
			}
		} else {
			CatalogEntry originalEntry = read(targetEntryId, catalog, context, cache, instrospection);

			context.setResults(Collections.singletonList(originalEntry));

			if ( context.getRequest().getFollowReferences()) {
				graphJoin.execute(context);
			}
			log.trace("[RESULT ] {}", originalEntry);
		}

		return CONTINUE_PROCESSING;
	}

	public List<CatalogEntry> read(FilterData filterData, CatalogDescriptor catalog, CatalogActionContext context,
			CatalogResultCache cache, Instrospection instrospection) throws Exception {
		List<CatalogEntry> regreso;
		filterData = queryRewriter.interceptQuery(filterData, context, catalog);

		if (cache == null) {
			regreso = doRead(filterData, catalog, context, instrospection);
		} else {
			regreso = cache.satisfy(context, catalog.getDistinguishedName(), filterData);
			if (regreso == null) {
				regreso = doRead(filterData, catalog, context, instrospection);
				if (regreso != null) {
					if (log.isInfoEnabled()) {
						log.trace("Saving {} query result(s) in cache {} list {}", regreso.size(),
								catalog.getDistinguishedName(), filterData);
					}
					cache.put(context, catalog.getDistinguishedName(), regreso, filterData);
				}
			}
		}

		context.getRuntimeContext().getTransactionHistory().didRead(context, regreso,
				null/* no undo */);

		return regreso;
	}

	public CatalogEntry read(Object targetEntryId, CatalogDescriptor catalog, CatalogActionContext context,
			CatalogResultCache cache, Instrospection instrospection) throws Exception {
		CatalogEntry regreso;
		if (cache == null) {
			regreso = doRead(targetEntryId, catalog, context, instrospection);
		} else {
			regreso = cache.get(context, catalog.getDistinguishedName(), targetEntryId);
			if (regreso == null) {
				regreso = doRead(targetEntryId, catalog, context, instrospection);
				if (regreso != null) {
					cache.put(context, catalog.getDistinguishedName(), regreso);
				}
			}
		}
		context.getRuntimeContext().getTransactionHistory().didRead(context, regreso,
				null/* no undo */);
		queryRewriter.interceptResult(regreso, context, catalog);
		return regreso;
	}

	public CatalogEntry readVanityId(String vanityId, CatalogDescriptor catalog, CatalogActionContext context,
			CatalogResultCache cache, Instrospection instrospection) throws Exception {
		CatalogEntry regreso = null;
		if (context.getCatalogManager().isPrimaryKey(vanityId)) {
			// almost certainly an Id
			Object primaryId = context.getCatalogManager().decodePrimaryKeyToken(vanityId);
			regreso = read(primaryId, catalog, context, cache, instrospection);
		}

		if (regreso == null) {
			FilterData filter;
			List<CatalogEntry> results;
			// if(catalog.getFieldDescriptor(HasDistinguishedName.FIELD)==null){
			filter = FilterDataUtils.createSingleFieldFilter(HasDistinguishedName.FIELD, vanityId);
			results = doRead(filter, catalog, context, instrospection);
			if (results == null || results.isEmpty()) {
				throw new IllegalArgumentException(vanityId);
			} else {
				regreso = results.get(0);
			}
		}
		queryRewriter.interceptResult(regreso, context, catalog);
		return regreso;
	}

	private List<CatalogEntry> doRead(FilterData filterData, CatalogDescriptor catalog, CatalogActionContext context,
			Instrospection instrospection) throws Exception {
		log.trace("DATASTORE QUERY");
		context.getRequest().setFilter(filterData);
		Command command = queryers.getCommand(String.valueOf(catalog.getStorage()));
		command.execute(context);

		if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {

			// read child Results
			List<CatalogEntry> children = context.getResults();
			if (children != null && !children.isEmpty()) {
				processChildren(children, context, catalog, instrospection);
				return children;
			} else {
				return Collections.EMPTY_LIST;
			}
		}

		return context.getResults();
	}

	private CatalogEntry doRead(Object targetEntryId, CatalogDescriptor catalog, CatalogActionContext context,
			Instrospection instrospection) throws Exception {
		log.trace("DATASTORE READ");
		context.getRequest().setEntry(targetEntryId);

		Command command = primaryKeyers.getCommand(String.valueOf(catalog.getStorage()));
		command.execute(context);

		if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {
			// read child entity
			CatalogEntry childEntity = context.getEntryResult();
			// we are certain this catalog has a parent, otherwise this DAO
			// would
			// not be called
			Long parentCatalogId = catalog.getParent();
			// aquire parent id
			Object parentEntityId = context.getCatalogManager().getAllegedParentId(childEntity, instrospection);
			// delegate deeper inheritance to another instance of an
			// AncestorAware
			// DAO

			log.trace("[PROCESSING CHILD ENTRY]");
			context.getCatalogManager().processChild(childEntity,
					context.getCatalogManager().getDescriptorForKey(parentCatalogId, context), parentEntityId,
					context, catalog, instrospection);
		}
		return context.getEntryResult();

	}

	protected void processChildren(List<CatalogEntry> children, CatalogActionContext context,
			CatalogDescriptor catalog, Instrospection instrospection) throws Exception {
		// we are certain this catalog has a parent, otherwise this DAO would
		// not be called
		Long parentCatalogId = catalog.getParent();
		CatalogDescriptor parent = context.getCatalogManager().getDescriptorForKey(parentCatalogId, context);
		Object parentEntityId;
		for (CatalogEntry childEntity : children) {
			parentEntityId = context.getCatalogManager().getAllegedParentId(childEntity, instrospection);
			context.getCatalogManager().processChild(childEntity, parent, parentEntityId, context, catalog,
                    instrospection);
		}
	}

	private void applyCriteria(FilterData filter, CatalogDescriptor catalog,
			List<? extends FilterCriteria> appliedCriteria, CatalogActionContext context, Instrospection instrospection)
			throws Exception {
		if (appliedCriteria != null) {
			for (FilterCriteria criteria : appliedCriteria) {
				if (criteria.getEvaluate()) {
					String operator = criteria.getOperator();
					Object criteriaValue = context.getCatalogManager()
							.synthethizeFieldValue(((String) criteria.getValue()).split(" "), context);
					criteria = FilterDataUtils.createSingleFieldFilter(criteria.getPath(), criteriaValue);
					criteria.setOperator(operator);
				}
				filter.addFilter(criteria);
			}
		}
	}

	private void applySorts(FilterData filter, List<FilterDataOrdering> appliedSorts) {
		if (appliedSorts != null) {
			for (FilterDataOrdering ordering : appliedSorts) {
				filter.addOrdering(ordering);
			}
		}
	}

	/*
	 * FIXME public static String buildVanityToken(HasDistinguishedName task) {
	 * 
	 * String name = task.getDistinguishedName(); if (name == null) { name =
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
