package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.catalogs.server.service.impl.LocalizedEntityWrapper;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.wrupple.muba.event.server.service.impl.FilterDataUtils.*;

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
	protected final EntrySynthesizer delegate;
	protected final CatalogKeyServices keydelegate;
	protected final FieldAccessStrategy access;

	@Inject
	public DataJoiner(EntrySynthesizer delegate, CatalogKeyServices keydelegate, FieldAccessStrategy access) {
		super();
		this.delegate = delegate;
		this.keydelegate = keydelegate;
		this.access = access;
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
				log.trace("[ALLOCATING KEY DOMAIN_FIELD FOR JOINED RESULT] {}", key);
				regreso.put(key, new HashSet<Object>(size));
			}
		}
		return regreso;
	}

	protected void joinWithGivenJoinData(List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog,
			String[][] joins, CatalogActionContext context, Map<JoinQueryKey, Set<Object>> filterMap, Instrospection instrospection)
			throws Exception {

		if (log.isInfoEnabled()) {
			log.trace("[BUILD RESULT SET] {} {} ", mainCatalog.getDistinguishedName(), Arrays.deepToString(joins));
		}
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
			gatherFieldValues(localField, mainResults, mainCatalog, instrospection, fieldValues, context);

			log.trace("[PROCESS JOIN] {}", key);
			processJoin(key.catalog, key.field, context, instrospection, fieldValues, mainResults, mainCatalog);

		}
	}

	private void processJoin(String catalogId, String foreignField, CatalogActionContext context, Instrospection instrospection,
			Set<Object> fieldValues, List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog) throws Exception {

		/*
		 * Gather join results for statement
		 */

		CatalogDescriptor catalog = context.getDescriptorForName(catalogId);
		if(catalog==null){
			throw new NullPointerException("No such catalog "+catalogId);
		}

		List<CatalogEntry> currentMatchingEntries = getjoinCandidates(mainResults, context, catalog, foreignField,
				fieldValues);

		if (currentMatchingEntries == null || currentMatchingEntries.isEmpty()) {
			return;
		} else {
			workJoinData(mainResults, mainCatalog, currentMatchingEntries, catalog, context, instrospection);

		}
	}

	private void gatherFieldValues(String fieldId, List<CatalogEntry> results, CatalogDescriptor catalog,
                                   Instrospection instrospection, Set<Object> fieldValues, CatalogActionContext context) throws Exception {
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
					log.trace("[CHANGE FOREIGN NUMERIC_ID]  {}", foreignCatalog);
				} else {
					CatalogDescriptor summaryDescriptor;
					FieldDescriptor columnDescriptor;
					log.trace("[find result set FOR FOREIGN NUMERIC_ID] ");
					for (CatalogColumnResultSet summary : joinsThusFar) {
						summaryDescriptor = summary.getCatalogDescriptor();
						if (foreignCatalog.equals(summaryDescriptor.getDistinguishedName())) {
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
							log.trace("[CHANGE FOREIGN NUMERIC_ID]  {}", foreignCatalog);
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
				putFieldValues(fieldId, results, catalog, instrospection, fieldValues);
			}
		}
	}

	private void putFieldValues(String fieldId, List<CatalogEntry> results, CatalogDescriptor catalog, Instrospection instrospection,
			Set<Object> fieldValues) throws Exception {
		FieldDescriptor field = catalog.getFieldDescriptor(fieldId);
		if (field == null) {
		} else {
			// this filter building method is built expressly for joining key
			// values

			if (field.isMultiple()) {
				Collection<?> temp;
				for (CatalogEntry e : results) {
                    temp = (Collection<?>) access.getPropertyValue(field, e, null, instrospection);
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
                    value = access.getPropertyValue(field, e, null, instrospection);
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

		List<CatalogEntry> currentMatchingEntries;
		if (currentQueryFilter == null) {
			currentMatchingEntries = Collections.EMPTY_LIST;
		} else {
			currentMatchingEntries = context.triggerRead(catalog.getDistinguishedName(),currentQueryFilter);
		}
		return currentMatchingEntries;
	}

	private FilterData createJoinSubquery(CatalogActionContext context, String foreignField, Set<Object> fieldValues)
			throws Exception {

		if (fieldValues == null || fieldValues.isEmpty()) {
			return null;
		} else {
			FilterData regreso = newFilterData();
			regreso.setConstrained(false);
			FilterCriteria criteria = newFilterCriteria();
			criteria.setOperator(FilterData.EQUALS);
			criteria.setValues(new ArrayList<Object>(fieldValues));
			criteria.pushToPath(foreignField);

			regreso.addFilter(criteria);

			return regreso;
		}
	}

	protected abstract void workJoinData(List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog,
			List<CatalogEntry> joins, CatalogDescriptor joinCatalog, CatalogActionContext context, Instrospection instrospection)
			throws Exception;
	/*
	 * protected void workJoinData(List<CatalogEntry> list, CatalogDescriptor
	 * catalog, String currentCatalogId, CatalogActionContext context) throws
	 * Exception { log.trace("[CREATE RESULT SET]"); if (catalog == null) {
	 * catalog =
	 * context.getDescriptorForName(currentCatalogId,
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
	 * regreso.setIdAsString(catalog.getDistinguishedName());
	 * regreso.setCatalogDescriptor(catalog); regreso.setContents(contents);
	 * List<Object>[] collectedValues = new List[fields.size()]; String fieldId;
	 * List<Object> fieldContents; log.trace("[RESULT SET CREATED] {}",
	 * catalog.getDistinguishedName()); // System.err.println(list);
	 * 
	 * Instrospection session = context.getCatalogManager().newSession(list.get(0)); int j = 0; for
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
	 * null; } else { fieldValue = context.getCatalogManager().getPropertyValue(catalog, field, object,
	 * localizedObject, session); } fieldContents.add(fieldValue); }
	 * 
	 * } regreso.setIdAsString(catalog.getDistinguishedName()); return ; }
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

            FilterCriteria catalogCriteria = newFilterCriteria();
            catalogCriteria.pushToPath(HasCatalogId.CATALOG_FIELD);
            catalogCriteria.setOperator(FilterData.EQUALS);
            catalogCriteria.setValue(catalog);
            FilterCriteria localeCriteria = newFilterCriteria();
            catalogCriteria.pushToPath(DistributiedLocalizedEntry.LOCALE_FIELD);
            catalogCriteria.setOperator(FilterData.EQUALS);
            catalogCriteria.setValue(catalog);

			log.debug("[build distributed localized results] ");
			return result.stream().map(entry->{
			            FilterData filter = createSingleKeyFieldFilter(catalog.getKeyField(), Collections.singletonList(entry.getId()));
                        filter.addFilter(localeCriteria);
			            filter.addFilter(catalogCriteria);
                        return filter;
			}
            ).map(filter->{
                try {
                    List<DistributiedLocalizedEntry> results = context.triggerRead(DistributiedLocalizedEntry.CATALOG,filter);
                    if(results!=null&&!results.isEmpty()){
                        return results.get(0);
                    }
                } catch (Exception e) {
                    log.error(filter.toString(),e);
                    context.getRuntimeContext().addWarning("${catalog.locale.read}");
                }
                return null;
            }).collect(Collectors.toList());
		case 0:
		default:
			return sameEntityStrategy_wrap(result,context.getRequest().getLocale(),(Long)catalog.getId());
		}

	}
    List<DistributiedLocalizedEntry>  sameEntityStrategy_wrap(List<? extends CatalogEntry> result,String locale,Long catalogNumericId) throws Exception {
		HasAccesablePropertyValues entry;
		LocalizedEntityWrapper localizedEntry;
		int size = result.size();
        List<DistributiedLocalizedEntry> regreso = new ArrayList<DistributiedLocalizedEntry>(size);
		log.trace("[WRAPPING RESULTS] {}/{}",size,locale);
		for (int i = 0; i < size; i++) {
			entry = (HasAccesablePropertyValues) result.get(i);

			localizedEntry = new LocalizedEntityWrapper(entry, locale, catalogNumericId);

			regreso.add(localizedEntry);
		}
		return regreso;
	}



	public static Map<Object, CatalogEntry> mapJoins(HashMap<Object, CatalogEntry> hashMap,
			List<CatalogEntry> entries) {
		for (CatalogEntry e : entries) {
			hashMap.put(e.getId(), e);
		}
		return hashMap;
	}

	protected CatalogColumnResultSet createResultSet(List<CatalogEntry> list, CatalogDescriptor catalog,
			String currentCatalogId, CatalogActionContext context, Instrospection instrospection) throws Exception {
		log.trace("[CREATE RESULT SET]");
		if (catalog == null) {
			catalog = context.getDescriptorForName(currentCatalogId);
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
			regreso.setId(catalog.getDistinguishedName());
			regreso.setCatalogDescriptor(catalog);
			regreso.setContents(contents);
			List<Object>[] collectedValues = new List[fields.size()];
			String fieldId;
			List<Object> fieldContents;
			log.trace("[RESULT SET CREATED] {}", catalog.getDistinguishedName());
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
                        fieldValue = access.getPropertyValue(field, object, localizedObject, instrospection);
                    }
					fieldContents.add(fieldValue);
				}
			}
			return regreso;
		}

	}

}
