package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.HasCatalogId;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.catalogs.server.service.impl.SameEntityLocalizationStrategy;

public abstract class DataJoiner implements Command {

	protected static final Logger log = LoggerFactory.getLogger(DataJoiner.class);

	static class JoinQueryKey {
		final String catalog;
		final String field;

		public JoinQueryKey(String catalog, String field) {
			super();
			this.catalog = catalog;
			this.field = field;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
			result = prime * result + ((field == null) ? 0 : field.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			JoinQueryKey other = (JoinQueryKey) obj;
			if (catalog == null) {
				if (other.catalog != null)
					return false;
			} else if (!catalog.equals(other.catalog))
				return false;
			if (field == null) {
				if (other.field != null)
					return false;
			} else if (!field.equals(other.field))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "catalog=" + catalog + ", field=" + field;
		}

	}

	protected final CatalogEvaluationDelegate axs;
	private final SameEntityLocalizationStrategy sameEntityStrategy;
	private final DiscriminateEntriesImpl separateEntityStrategy;

	@Inject
	public DataJoiner(DiscriminateEntriesImpl separateEntityStrategy, SameEntityLocalizationStrategy sameEntityStrategy,
			CatalogEvaluationDelegate propertyAccessor) {
		super();
		this.sameEntityStrategy = sameEntityStrategy;
		this.axs = propertyAccessor;
		this.separateEntityStrategy = separateEntityStrategy;
	}

	protected Map<JoinQueryKey, Set<Object>> createFilterMap(String[][] joins, CatalogActionContext context) {
		Map<JoinQueryKey, Set<Object>> regreso = new HashMap<JoinQueryKey, Set<Object>>(joins.length);
		int size = context.getResults().size();
		String catalogId, foreignField;
		JoinQueryKey key;
		for (String[] statement : joins) {
			catalogId = statement[0];
			foreignField = statement[1];
			key = new JoinQueryKey(catalogId, foreignField);
			if (!regreso.containsKey(key)) {
				log.trace("[ALLOCATING KEY NAMESPACE FOR JOINED RESULT] {}", key);
				regreso.put(key, new HashSet<Object>(size));
			}
		}
		return regreso;
	}

	protected void joinWithGivenJoinData(List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog,
			String[][] joins, CatalogActionContext context, Map<JoinQueryKey, Set<Object>> filterMap, Session session)
			throws Exception {

		if (log.isInfoEnabled()) {
			log.trace("[BUILD RESULT SET] {} {} ", mainCatalog.getCatalog(), Arrays.deepToString(joins));
		}
		context.setEntry(null);
		// for each join a separate result set is added to the response

		String[] joinSentence;
		String catalogId;
		String foreignField;
		String localField;
		Set<Object> fieldValues;
		JoinQueryKey key;
		for (int i = 0; i < joins.length; i++) {
			joinSentence = joins[i];
			// join statement
			catalogId = joinSentence[0];
			foreignField = joinSentence[1];
			localField = grabLocalJoinField(joinSentence, foreignField);
			key = new JoinQueryKey(catalogId, foreignField);
			fieldValues = filterMap.get(key);

			log.trace("[GATHERING VALUES FOR JOIN] {}/{}", key, localField);
			gatherFieldValues(localField, mainResults, mainCatalog, session, fieldValues, context);

			log.trace("[PROCESS JOIN] {}", key);
			processJoin(key.catalog, key.field, context, session, fieldValues, mainResults, mainCatalog);

		}
	}

	private void processJoin(String catalogId, String foreignField, CatalogActionContext context, Session session,
			Set<Object> fieldValues, List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog) throws Exception {

		/*
		 * Gather join results for statement
		 */

		CatalogDescriptor catalog = context.getCatalogManager().getDescriptorForName(catalogId, context);

		List<CatalogEntry> currentMatchingEntries = getjoinCandidates(mainResults, context, catalog, foreignField,
				fieldValues);

		if (currentMatchingEntries == null || currentMatchingEntries.isEmpty()) {
			return;
		} else {
			workJoinData(mainResults, mainCatalog, currentMatchingEntries, catalog, context, session);

		}
	}

	private void gatherFieldValues(String fieldId, List<CatalogEntry> results, CatalogDescriptor catalog,
			Session session, Set<Object> fieldValues, CatalogActionContext context) throws Exception {
		List<CatalogColumnResultSet> joinsThusFar = (List<CatalogColumnResultSet>) context
				.get(CompleteCatalogGraph.JOINED_DATA);
		int indexOfLastSeparator = fieldId.lastIndexOf('.');
		if (indexOfLastSeparator > 0) {
			// TODO this is a neglected implementation
			List<Object> regreso = null;
			String[] pathTokens = fieldId.split("\\.");
			if (log.isTraceEnabled()) {
				log.trace("[NESTED FIELD] {}", Arrays.toString(pathTokens));
			}
			String pathTOken;
			String foreignCatalog = null;
			for (int i = 0; i < pathTokens.length; i++) {
				pathTOken = pathTokens[i];
				log.trace("[CURRENT PATH TOKEN] {}", pathTOken);
				if (i == 0) {
					foreignCatalog = catalog.getFieldDescriptor(pathTOken).getCatalog();
					log.trace("[CHANGE FOREIGN CATALOG]  {}", foreignCatalog);
				} else {
					CatalogDescriptor summaryDescriptor;
					FieldDescriptor columnDescriptor;
					log.trace("[find result set FOR FOREIGN CATALOG] ");
					for (CatalogColumnResultSet summary : joinsThusFar) {
						summaryDescriptor = summary.getCatalogDescriptor();
						if (foreignCatalog.equals(summaryDescriptor.getCatalog())) {
							columnDescriptor = summaryDescriptor.getFieldDescriptor(pathTOken);

							if (i == (pathTokens.length - 1)) {
								log.trace("[REACHED LAST PATH TOKEN, READ VALUES AS FILTERS]");
								regreso = summary.getContents().get(pathTOken);
								if (columnDescriptor.isMultiple()) {
									Collection<?> value;
									for (Object v : regreso) {
										value = (Collection<?>) v;
										if (value != null) {
											for (Object o : value) {
												if (o != null) {
													if (fieldValues.add(o)) {
														log.trace("[JOIN DISCRIMINATOR] ", o);
													}
												}
											}
										}
									}
								} else {

									for (Object o : regreso) {
										if (o != null) {
											if (fieldValues.add(o)) {
												log.trace("[JOIN DISCRIMINATOR] ", o);
											}
										}
									}
								}
							}
							foreignCatalog = columnDescriptor.getCatalog();
							log.trace("[CHANGE FOREIGN CATALOG]  {}", foreignCatalog);
						}
					}
				}
				if (foreignCatalog == null) {
					throw new IllegalArgumentException("Join sentence contains broken field chain");
				}
			}
			throw new IllegalArgumentException("Join sentence contains broken field chain");
		} else {
			if (results == null || results.isEmpty()) {
				throw new RuntimeException("no results to join");
			} else {
				log.trace("[READ JOIN DISCRIMINATORS] ");
				putFieldValues(fieldId, results, catalog, session, fieldValues);
			}
		}
	}

	private void putFieldValues(String fieldId, List<CatalogEntry> results, CatalogDescriptor catalog, Session session,
			Set<Object> fieldValues) throws Exception {
		FieldDescriptor field = catalog.getFieldDescriptor(fieldId);
		if (field == null) {
		} else {
			// this filter building method is built expressly for joining key
			// values

			if (field.isMultiple()) {
				Collection<?> temp;
				for (CatalogEntry e : results) {
					temp = (Collection<?>) axs.getPropertyValue(catalog, field, e, null, session);
					if (temp != null) {
						for (Object o : temp) {
							if (o != null) {
								fieldValues.add(o);
							}
						}
					}

				}
			} else {

				Object value;
				for (CatalogEntry e : results) {
					value = axs.getPropertyValue(catalog, field, e, null, session);
					if (value != null) {
						fieldValues.add(value);
					}
				}
			}
		}

	}

	private List<CatalogEntry> getjoinCandidates(List<CatalogEntry> mainResults, CatalogActionContext context,
			CatalogDescriptor catalog, String foreignField, Set<Object> fieldValues) throws Exception {

		if (mainResults == null || mainResults.isEmpty()) {
			log.trace("[NO RESULTS]");
			return Collections.EMPTY_LIST;
		}

		// Find values to join
		FilterData currentQueryFilter = createJoinSubquery(context, foreignField, fieldValues);
		context.setCatalog(catalog.getCatalog());
		List<CatalogEntry> currentMatchingEntries;
		if (currentQueryFilter == null) {
			currentMatchingEntries = Collections.EMPTY_LIST;
		} else {
			context.getCatalogManager().getRead().execute(context);
			currentMatchingEntries = context.getResults();
		}
		return currentMatchingEntries;
	}

	private FilterData createJoinSubquery(CatalogActionContext context, String foreignField, Set<Object> fieldValues)
			throws Exception {

		if (fieldValues == null || fieldValues.isEmpty()) {
			return null;
		} else {
			FilterData regreso = FilterDataUtils.newFilterData();
			regreso.setConstrained(false);
			FilterCriteria criteria = FilterDataUtils.newFilterCriteria();
			criteria.setOperator(FilterData.EQUALS);
			criteria.setValues(new ArrayList<Object>(fieldValues));
			criteria.pushToPath(foreignField);

			regreso.addFilter(criteria);

			context.setFilter(regreso);
			return regreso;
		}
	}

	protected abstract void workJoinData(List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog,
			List<CatalogEntry> joins, CatalogDescriptor joinCatalog, CatalogActionContext context, Session session)
			throws Exception;
	/*
	 * protected void workJoinData(List<CatalogEntry> list, CatalogDescriptor
	 * catalog, String currentCatalogId, CatalogActionContext context) throws
	 * Exception { log.trace("[CREATE RESULT SET]"); if (catalog == null) {
	 * catalog =
	 * context.getCatalogManager().getDescriptorForName(currentCatalogId,
	 * context); }
	 * 
	 * Collection<FieldDescriptor> rawFields = catalog.getFieldsValues(); if
	 * (list == null || list.isEmpty() || rawFields == null ||
	 * rawFields.isEmpty()) { log.debug("[NO DATA]"); return ; } else {
	 * List<DistributiedLocalizedEntry> localizedEntries =
	 * buildLocalizedResult(catalog, list, context); Collection<FieldDescriptor>
	 * fields = catalog.getFieldsValues(); HashMap<String, List<Object>>
	 * contents = new HashMap<String, List<Object>>(fields.size());
	 * CatalogColumnResultSet regreso = new CatalogColumnResultSet();
	 * regreso.setIdAsString(catalog.getCatalog());
	 * regreso.setCatalogDescriptor(catalog); regreso.setContents(contents);
	 * List<Object>[] collectedValues = new List[fields.size()]; String fieldId;
	 * List<Object> fieldContents; log.trace("[RESULT SET CREATED] {}",
	 * catalog.getCatalog()); // System.err.println(list);
	 * 
	 * Session session = axs.newSession(list.get(0)); int j = 0; for
	 * (FieldDescriptor field : fields) { fieldId = field.getFieldId();
	 * fieldContents = new ArrayList<Object>(list.size()); collectedValues[j] =
	 * fieldContents; log.trace("[ALLOCATED SPACE FOR FIELD] {}", fieldId);
	 * contents.put(fieldId, fieldContents); j++; } if (contents.isEmpty()) {
	 * return ; }
	 * 
	 * Object fieldValue;
	 * 
	 * CatalogEntry object; DistributiedLocalizedEntry localizedObject;
	 * 
	 * for (int i = 0; i < list.size(); i++) { object = list.get(i); if
	 * (localizedEntries == null) { localizedObject = null; } else {
	 * localizedObject = localizedEntries.get(i); }
	 * 
	 * for (FieldDescriptor field : fields) { fieldContents =
	 * collectedValues[j]; if (field.isMasked()) { log.debug(
	 * "[NULLED VALUE OF MASKED FIELD] {}", field.getFieldId()); fieldValue =
	 * null; } else { fieldValue = axs.getPropertyValue(catalog, field, object,
	 * localizedObject, session); } fieldContents.add(fieldValue); }
	 * 
	 * } regreso.setIdAsString(catalog.getCatalog()); return ; }
	 * 
	 * }
	 */

	private String grabLocalJoinField(String[] join, String foreignField) {
		String localField;
		if (join.length < 2) {
			// natural join (Using field)
			localField = foreignField;
		} else {
			// standard join (On field==foreignField)
			localField = join[2];
		}
		return localField;
	}

	private List<DistributiedLocalizedEntry> buildLocalizedResult(CatalogDescriptor catalog,
			List<? extends CatalogEntry> result, CatalogActionContext context) throws Exception {

		switch (catalog.getLocalization()) {
		case 1:
			FilterData membershipFactors = createFilters(context.getLocale(), context.getCatalogDescriptor().getId());
			log.debug("[build localized results] {}", membershipFactors);
			context.setCatalog(DistributiedLocalizedEntry.CATALOG);
			context.setResults(result);
			context.setFilter(membershipFactors);
			separateEntityStrategy.execute(context);
			break;
		case 0:
		default:
			context.setCatalog(catalog.getCatalog());
			context.setResults(result);
			sameEntityStrategy.execute(context);
			break;
		}
		return context.getResults();

	}

	private FilterData createFilters(String locale, Object catalog) {

		FilterData filters = FilterDataUtils.createSingleFieldFilter(DistributiedLocalizedEntry.LOCALE_FIELD, locale);
		FilterCriteria catalogCriteria = FilterDataUtils.newFilterCriteria();
		catalogCriteria.pushToPath(HasCatalogId.CATALOG_FIELD);
		catalogCriteria.setOperator(FilterData.EQUALS);
		catalogCriteria.setValue(catalog);
		filters.addFilter(catalogCriteria);
		return filters;
	}

	public static Map<Object, CatalogEntry> mapJoins(HashMap<Object, CatalogEntry> hashMap,
			List<CatalogEntry> entries) {
		for (CatalogEntry e : entries) {
			hashMap.put(e.getId(), e);
		}
		return hashMap;
	}

	protected CatalogColumnResultSet createResultSet(List<CatalogEntry> list, CatalogDescriptor catalog,
			String currentCatalogId, CatalogActionContext context, Session session) throws Exception {
		log.trace("[CREATE RESULT SET]");
		if (catalog == null) {
			catalog = context.getCatalogManager().getDescriptorForName(currentCatalogId, context);
		}

		Collection<FieldDescriptor> rawFields = catalog.getFieldsValues();
		if (list == null || list.isEmpty() || rawFields == null || rawFields.isEmpty()) {
			log.debug("[NO DATA]");
			return null;
		} else {
			List<DistributiedLocalizedEntry> localizedEntries = buildLocalizedResult(catalog, list, context);
			Collection<FieldDescriptor> fields = catalog.getFieldsValues();
			HashMap<String, List<Object>> contents = new HashMap<String, List<Object>>(fields.size());
			CatalogColumnResultSet regreso = new CatalogColumnResultSet();
			regreso.setIdAsString(catalog.getCatalog());
			regreso.setCatalogDescriptor(catalog);
			regreso.setContents(contents);
			List<Object>[] collectedValues = new List[fields.size()];
			String fieldId;
			List<Object> fieldContents;
			log.trace("[RESULT SET CREATED] {}", catalog.getCatalog());
			// System.err.println(list);

			int j = 0;
			for (FieldDescriptor field : fields) {
				fieldId = field.getFieldId();
				fieldContents = new ArrayList<Object>(list.size());
				collectedValues[j] = fieldContents;
				log.trace("[ALLOCATED SPACE FOR FIELD] {}", fieldId);
				contents.put(fieldId, fieldContents);
				j++;
			}
			if (contents.isEmpty()) {
				return null;
			}

			Object fieldValue;

			CatalogEntry object;
			DistributiedLocalizedEntry localizedObject;

			for (int i = 0; i < list.size(); i++) {
				object = list.get(i);
				if (localizedEntries == null) {
					localizedObject = null;
				} else {
					localizedObject = localizedEntries.get(i);
				}

				for (FieldDescriptor field : fields) {
					fieldContents = collectedValues[j];
					if (field.isMasked()) {
						log.debug("[NULLED VALUE OF MASKED FIELD] {}", field.getFieldId());
						fieldValue = null;
					} else {
						fieldValue = axs.getPropertyValue(catalog, field, object, localizedObject, session);
					}
					fieldContents.add(fieldValue);
				}

			}
			return regreso;
		}

	}

}
