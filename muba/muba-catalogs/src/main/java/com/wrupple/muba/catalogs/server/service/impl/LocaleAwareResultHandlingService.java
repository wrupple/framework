package com.wrupple.muba.catalogs.server.service.impl;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.domain.VegetateColumnResultSet;
import com.wrupple.muba.catalogs.server.chain.command.impl.DiscriminateEntries;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.ResultHandlingService;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.server.chain.command.I18nProcessing.DistributiedLocalizedEntry;
import com.wrupple.vegetate.server.services.impl.FilterDataUtils;

public class LocaleAwareResultHandlingService implements ResultHandlingService {
	protected static final Logger log = LoggerFactory.getLogger(LocaleAwareResultHandlingService.class);

	private static class JoinQueryKey {
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

	private final CatalogPropertyAccesor propertyAccessor;
	private final SameEntityLocalizationStrategy sameEntityStrategy;
	private final DiscriminateEntries separateEntityStrategy;
	private final DatabasePlugin database;

	@Inject
	public LocaleAwareResultHandlingService(DiscriminateEntries separateEntityStrategy, SameEntityLocalizationStrategy sameEntityStrategy,
			CatalogPropertyAccesor propertyAccessor,DatabasePlugin database) {
		super();
		this.database=database;
		this.sameEntityStrategy = sameEntityStrategy;
		this.propertyAccessor = propertyAccessor;
		this.separateEntityStrategy = separateEntityStrategy;
	}

	@Override
	public VegetateColumnResultSet createResultSet(boolean summaryFieldsOnly, CatalogExcecutionContext context) throws Exception {
		List<CatalogEntry> list = context.getResults();
		VegetateColumnResultSet resultSet = createResultSet(list, context.getCatalogDescriptor(), context.getCatalog(),
				context.getRequest().getStorageManager().spawn(context));
		resultSet.setCursor(context.getFilter().getCursor());
		return resultSet;
	}

	@Override
	public List<VegetateColumnResultSet> implicitJoin(CatalogExcecutionContext context) throws Exception {
		if (context.getResults() == null || context.getResults().isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		CatalogDescriptor descriptor = context.getCatalogDescriptor();
		String[][] joins = ImplicitJoinUtils.getJoins(database, null, descriptor, null, context.getDomain(), null);
		Map<JoinQueryKey, Set<Object>> filterMap = createFilterMap(joins, context);
		return joinWithGivenJoinData(context.getResults(), context.getCatalogDescriptor(), joins, context.getRequest().getStorageManager().spawn(context),
				filterMap);
	}

	@Override
	public List<VegetateColumnResultSet> explicitJoin(CatalogExcecutionContext context) throws Exception {
		if (context.getResults() == null || context.getResults().isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		FilterData filter = context.getFilter();
		String[][] joins = filter.getJoins();
		Map<JoinQueryKey, Set<Object>> filterMap = createFilterMap(joins, context);
		return joinWithGivenJoinData(context.getResults(), context.getCatalogDescriptor(), joins, context, filterMap);
	}

	private Map<JoinQueryKey, Set<Object>> createFilterMap(String[][] joins, CatalogExcecutionContext context) {
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

	private List<VegetateColumnResultSet> joinWithGivenJoinData(List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog, String[][] joins,
			CatalogExcecutionContext context, Map<JoinQueryKey, Set<Object>> filterMap) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("[BUILD RESULT SET] {} {} ", mainCatalog.getCatalogId(), Arrays.deepToString(joins));
		}
		context.setEntry(null);
		// for each join a separate result set is added to the response

		ArrayList<VegetateColumnResultSet> regreso = new ArrayList<VegetateColumnResultSet>(filterMap.size());
		Session session = propertyAccessor.newSession(null);
		List<CatalogEntry> results = context.getResults();
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		VegetateColumnResultSet currentResultSet;
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
			gatherFieldValues(localField, results, catalog, regreso, session, fieldValues);

			currentResultSet = processJoin(key, regreso, context, session, fieldValues, mainResults, mainCatalog);

			if (currentResultSet != null) {
				regreso.add(currentResultSet);
			}
		}
		regreso.trimToSize();
		return regreso;
	}

	private VegetateColumnResultSet processJoin(JoinQueryKey key, List<VegetateColumnResultSet> joinsBuiltThusFar, CatalogExcecutionContext context,
			Session session, Set<Object> fieldValues, List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog) throws Exception {

		String catalogId = key.catalog;
		String foreignField = key.field;

		/*
		 * Gather join results for statement
		 */
		log.trace("[PROCESS JOIN] {}", key);
		CatalogDescriptor catalog = database.getDescriptorForName(catalogId, context);

		List<CatalogEntry> currentMatchingEntries = getjoinCandidates(mainResults, mainCatalog, context, session, catalog, foreignField, joinsBuiltThusFar,
				fieldValues);

		if (currentMatchingEntries == null || currentMatchingEntries.isEmpty()) {
			return null;
		} else {
			VegetateColumnResultSet regreso = createResultSet(currentMatchingEntries, catalog, catalogId, context);
			regreso.setIdAsString(catalogId);
			return regreso;
		}
	}

	private void gatherFieldValues(String fieldId, List<CatalogEntry> results, CatalogDescriptor catalog, List<VegetateColumnResultSet> joinsThusFar,
			Session session, Set<Object> fieldValues) throws Exception {

		int indexOfLastSeparator = fieldId.lastIndexOf('.');
		if (indexOfLastSeparator > 0) {
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
					foreignCatalog = catalog.getFieldDescriptor(pathTOken).getForeignCatalogName();
					log.trace("[CHANGE FOREIGN CATALOG]  {}", foreignCatalog);
				} else {
					CatalogDescriptor summaryDescriptor;
					FieldDescriptor columnDescriptor;
					log.trace("[find result set FOR FOREIGN CATALOG] ");
					for (VegetateColumnResultSet summary : joinsThusFar) {
						summaryDescriptor = summary.getCatalogDescriptor();
						if (foreignCatalog.equals(summaryDescriptor.getCatalogId())) {
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
							foreignCatalog = columnDescriptor.getForeignCatalogName();
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

	private void putFieldValues(String fieldId, List<CatalogEntry> results, CatalogDescriptor catalog, Session session, Set<Object> fieldValues) {
		FieldDescriptor field = catalog.getFieldDescriptor(fieldId);
		if (field == null) {
		} else {
			// this filter building method is built expressly for joining key
			// values

			if (field.isMultiple()) {
				Collection<?> temp;
				for (CatalogEntry e : results) {
					temp = (Collection<?>) propertyAccessor.getPropertyValue(catalog, field, e, null, session);
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
					value = propertyAccessor.getPropertyValue(catalog, field, e, null, session);
					if (value != null) {
						fieldValues.add(value);
					}
				}
			}
		}

	}

	private List<CatalogEntry> getjoinCandidates(List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog, CatalogExcecutionContext context,
			Session session, CatalogDescriptor catalog, String foreignField, List<VegetateColumnResultSet> joinsBuiltThusFar, Set<Object> fieldValues)
			throws Exception {

		if (mainResults == null || mainResults.isEmpty()) {
			log.trace("[NO RESULTS]");
			return Collections.EMPTY_LIST;
		}

		// Find values to join
		FilterData currentQueryFilter = createJoinSubquery(context, foreignField, mainResults, mainCatalog, joinsBuiltThusFar, session, fieldValues);
		context.setCatalog(catalog.getCatalogId());
		List<CatalogEntry> currentMatchingEntries;
		if (currentQueryFilter == null) {
			currentMatchingEntries = Collections.EMPTY_LIST;
		} else {
			context.getRequest().getStorageManager().getRead().execute(context);
			currentMatchingEntries = context.getResults();
		}
		return currentMatchingEntries;
	}

	private FilterData createJoinSubquery(CatalogExcecutionContext context, String foreignField, List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog,
			List<VegetateColumnResultSet> joinsBuiltThusFar, Session session, Set<Object> fieldValues) throws Exception {

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

	private VegetateColumnResultSet createResultSet(List<CatalogEntry> list, CatalogDescriptor catalog, String currentCatalogId,
			CatalogExcecutionContext context) throws Exception {
		log.trace("[CREATE RESULT SET]");
		if (catalog == null) {
			catalog = database.getDescriptorForName(currentCatalogId, context);
		}

		Collection<FieldDescriptor> rawFields = catalog.getFieldsValues();
		if (list == null || list.isEmpty() || rawFields == null || rawFields.isEmpty()) {
			log.debug("[NO DATA]");
			return null;
		} else {
			List<DistributiedLocalizedEntry> localizedEntries = buildLocalizedResult(catalog, list, context);
			Collection<FieldDescriptor> fields = catalog.getFieldsValues();
			HashMap<String, List<Object>> contents = new HashMap<String, List<Object>>(fields.size());
			VegetateColumnResultSet regreso = new VegetateColumnResultSet();
			regreso.setIdAsString(catalog.getCatalogId());
			regreso.setCatalogDescriptor(catalog);
			regreso.setContents(contents);
			List<Object>[] collectedValues = new List[fields.size()];
			String fieldId;
			List<Object> fieldContents;
			log.trace("[RESULT SET CREATED] {}", catalog.getCatalogId());
			// System.err.println(list);

			Session session = propertyAccessor.newSession(list.get(0));
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
						fieldValue = propertyAccessor.getPropertyValue(catalog, field, object, localizedObject, session);
					}
					fieldContents.add(fieldValue);
				}

			}
			return regreso;
		}

	}

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

	private List<DistributiedLocalizedEntry> buildLocalizedResult(CatalogDescriptor catalog, List<? extends CatalogEntry> result, CatalogExcecutionContext context)
			throws Exception {
		
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
			context.setCatalog(catalog.getCatalogId());
			context.setResults(result);
			 sameEntityStrategy.execute(context);
			 break;
		}
		 return context.getResults();

	}

	private FilterData createFilters(String locale, Object catalog) {

		FilterData filters = FilterDataUtils.createSingleFieldFilter(DistributiedLocalizedEntry.LOCALE_FIELD, locale);
		FilterCriteria catalogCriteria = FilterDataUtils.newFilterCriteria();
		catalogCriteria.pushToPath(HasCatalogId.FIELD);
		catalogCriteria.setOperator(FilterData.EQUALS);
		catalogCriteria.setValue(catalog);
		filters.addFilter(catalogCriteria);
		return filters;
	}
}
