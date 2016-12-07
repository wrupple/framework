package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.FilterDataOrdering;
import com.wrupple.muba.bootstrap.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogResultSet;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.catalogs.server.chain.command.ExplicitDataJoin;
import com.wrupple.muba.catalogs.server.service.CatalogReaderInterceptor;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyReaders;
import com.wrupple.muba.catalogs.server.service.QueryReaders;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin.Session;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;

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
		String catalogId = (String) context.getCatalog();
		if (catalogId == null) {
			log.trace("[GET AVAILABLE CATALOG LIST]");
			// list all domain catalogs
			context.setResults(context.getCatalogManager().getAvailableCatalogs(context));
			return CONTINUE_PROCESSING;
		}
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		CatalogResultCache cache = context.getCatalogManager().getCache(context.getCatalogDescriptor(), context);

		Object targetEntryId = context.getEntry();
		Session session = context.getCatalogManager().newSession(null);
		if (targetEntryId == null) {
			FilterData filter = context.getFilter();
			if (filter == null) {
				log.trace("[ASSEMBLE CATALOG DESCRIPTOR]");
				// get full catalog descriptor

				context.setResults(Collections.singletonList(catalog));
				return CONTINUE_PROCESSING;
			}
			applySorts(filter, catalog.getAppliedSorts());
			applyCriteria(filter, catalog, catalog.getAppliedCriteria(), context, session);
			List<CatalogEntry> result = null;
			FilterCriteria keyCriteria = filter.fetchCriteria(CatalogEntry.ID_FIELD);
			if (!filter.isConstrained() || keyCriteria == null) {
				result = read(filter, catalog, context, cache, session);
			} else {
				List<Object> keys = keyCriteria.getValues();
				if (keys == null) {
					context.getExcecutionContext().addWarning("malformed criteria");
				} else {

					if (filter.getCursor() == null) {
						int ammountOfKeys = keys.size();
						// only if theres still some unsatisfied idś in criteria
						if (filter.getStart() < ammountOfKeys) {
							result = read(filter, catalog, context, cache, session);
						}
					} else {
						result = read(filter, catalog, context, cache, session);
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
			} else if (MIN_TREE_LEVELS > 0 || context.containsKey(READ_GRAPH)) {// interceptor
																				// decides
																				// to
																				// read
																				// graph
				graphJoin.execute(context);
			}
		} else {
			CatalogEntry originalEntry = read(targetEntryId, catalog, context, cache, session);

			context.setResults(Collections.singletonList(originalEntry));

			if (context.containsKey(READ_GRAPH)) {
				graphJoin.execute(context);
			}
			log.trace("[RESULT ] {}", originalEntry);
		}

		return CONTINUE_PROCESSING;
	}

	public List<CatalogEntry> read(FilterData filterData, CatalogDescriptor catalog, CatalogActionContext context,
			CatalogResultCache cache, Session session) throws Exception {
		List<CatalogEntry> regreso;
		filterData = queryRewriter.interceptQuery(filterData, context, catalog);

		if (cache == null) {
			regreso = doRead(filterData, catalog, context, session);
		} else {
			regreso = cache.satisfy(context, catalog.getDistinguishedName(), filterData);
			if (regreso == null) {
				regreso = doRead(filterData, catalog, context, session);
				if (regreso != null) {
					if (log.isInfoEnabled()) {
						log.trace("Saving {} query result(s) in cache {} list {}", regreso.size(),
								catalog.getDistinguishedName(), filterData);
					}
					cache.put(context, catalog.getDistinguishedName(), regreso, filterData);
				}
			}
		}

		context.getTransactionHistory().didRead(context, regreso,
				null/* no undo */);

		return regreso;
	}

	public CatalogEntry read(Object targetEntryId, CatalogDescriptor catalog, CatalogActionContext context,
			CatalogResultCache cache, Session session) throws Exception {
		CatalogEntry regreso;
		if (cache == null) {
			regreso = doRead(targetEntryId, catalog, context, session);
		} else {
			regreso = cache.get(context, catalog.getDistinguishedName(), targetEntryId);
			if (regreso == null) {
				regreso = doRead(targetEntryId, catalog, context, session);
				if (regreso != null) {
					cache.put(context, catalog.getDistinguishedName(), regreso);
				}
			}
		}
		context.getTransactionHistory().didRead(context, regreso,
				null/* no undo */);
		queryRewriter.interceptResult(regreso, context, catalog);
		return regreso;
	}

	public CatalogEntry readVanityId(String vanityId, CatalogDescriptor catalog, CatalogActionContext context,
			CatalogResultCache cache, Session session) throws Exception {
		CatalogEntry regreso = null;
		if (context.getCatalogManager().isPrimaryKey(vanityId)) {
			// almost certainly an Id
			Object primaryId = context.getCatalogManager().decodePrimaryKeyToken(vanityId);
			regreso = read(primaryId, catalog, context, cache, session);
		}

		if (regreso == null) {
			FilterData filter;
			List<CatalogEntry> results;
			// if(catalog.getFieldDescriptor(HasDistinguishedName.FIELD)==null){
			filter = FilterDataUtils.createSingleFieldFilter(HasDistinguishedName.FIELD, vanityId);
			results = doRead(filter, catalog, context, session);
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
			Session session) throws Exception {
		log.trace("DATASTORE QUERY");
		context.setFilter(filterData);
		Command command = queryers.getCommand(String.valueOf(catalog.getStorage()));
		command.execute(context);

		if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {

			// read child Results
			List<CatalogEntry> children = context.getResults();
			if (children != null && !children.isEmpty()) {

				CatalogActionContext readContext = context.getCatalogManager().spawn(context);
				processChildren(children, readContext, catalog, session);
				return children;
			} else {
				return Collections.EMPTY_LIST;
			}
		}

		return context.getResults();
	}

	private CatalogEntry doRead(Object targetEntryId, CatalogDescriptor catalog, CatalogActionContext context,
			Session session) throws Exception {
		log.trace("DATASTORE READ");
		context.setEntry(targetEntryId);

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
			Object parentEntityId = context.getCatalogManager().getAllegedParentId(childEntity, session);
			// delegate deeper inheritance to another instance of an
			// AncestorAware
			// DAO

			log.trace("[PROCESSING CHILD ENTRY]");
			context.getCatalogManager().processChild(childEntity,
					context.getCatalogManager().getDescriptorForKey(parentCatalogId, context), parentEntityId,
					context.getCatalogManager().spawn(context), catalog, session);
		}
		return context.getEntryResult();

	}

	protected void processChildren(List<CatalogEntry> children, CatalogActionContext readContext,
			CatalogDescriptor catalog, Session session) throws Exception {
		// we are certain this catalog has a parent, otherwise this DAO would
		// not be called
		Long parentCatalogId = catalog.getParent();
		CatalogDescriptor parent = readContext.getCatalogManager().getDescriptorForKey(parentCatalogId, readContext);
		Object parentEntityId;
		for (CatalogEntry childEntity : children) {
			parentEntityId = readContext.getCatalogManager().getAllegedParentId(childEntity, session);
			readContext.getCatalogManager().processChild(childEntity, parent, parentEntityId, readContext, catalog,
					session);
		}
	}

	private void applyCriteria(FilterData filter, CatalogDescriptor catalog,
			List<? extends FilterCriteria> appliedCriteria, CatalogActionContext context, Session session)
			throws Exception {
		if (appliedCriteria != null) {
			for (FilterCriteria criteria : appliedCriteria) {
				if (criteria.getEvaluate()) {
					String operator = criteria.getOperator();
					Object criteriaValue = context.getCatalogManager()
							.synthethizeFieldValue((String) criteria.getValue(), context);
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
