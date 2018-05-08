package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.catalogs.server.chain.command.impl.BuildColumnResultSetImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorService;
import com.wrupple.muba.catalogs.server.service.ResultSetService;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static com.wrupple.muba.event.server.service.impl.FilterDataUtils.createSingleKeyFieldFilter;
import static com.wrupple.muba.event.server.service.impl.FilterDataUtils.newFilterCriteria;

/**
 * Created by japi on 5/05/18.
 */
@Singleton
public class ResultSetServiceImpl implements ResultSetService {

    protected static final Logger log = LogManager.getLogger(BuildColumnResultSetImpl.class);

    private final FieldAccessStrategy access;
    private final CatalogDescriptorService catalogService;

    @Inject
    public ResultSetServiceImpl(FieldAccessStrategy access, CatalogDescriptorService catalogService) {
        this.access = access;
        this.catalogService = catalogService;
    }

    public CatalogColumnResultSet createResultSet(List<CatalogEntry> foreignResults, CatalogDescriptor foreignCatalog,
                                                     String foreignCatalogId, CatalogActionContext context, Instrospection instrospection) throws Exception {
        log.trace("[CREATE RESULT SET]");
        if (foreignCatalog == null) {
            foreignCatalog = catalogService.getDescriptorForName(foreignCatalogId,context);
        }

        Collection<FieldDescriptor> rawFields = foreignCatalog.getFieldsValues();
        if (foreignResults == null || foreignResults.isEmpty() || rawFields == null || rawFields.isEmpty()) {
            log.debug("[NO DATA]");
            return null;
        } else {
            List<DistributiedLocalizedEntry> localizedEntries = buildLocalizedResult(foreignCatalog, foreignResults, context);
            Collection<FieldDescriptor> fields = foreignCatalog.getFieldsValues();
            HashMap<String, List<Object>> contents = new HashMap<String, List<Object>>(fields.size());
            CatalogColumnResultSet regreso = new CatalogColumnResultSet();
            regreso.setId(foreignCatalog.getDistinguishedName());
            regreso.setCatalogDescriptor(foreignCatalog);
            regreso.setContents(contents);
            List<Object>[] collectedValues = new List[fields.size()];
            String fieldId;
            List<Object> fieldContents;
            log.trace("[RESULT SET CREATED] {}", foreignCatalog.getDistinguishedName());
            // System.err.println(foreignResults);

            int j = 0;
            for (FieldDescriptor field : fields) {
                fieldId = field.getFieldId();
                fieldContents = new ArrayList<Object>(foreignResults.size());
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

            for (int i = 0; i < foreignResults.size(); i++) {
                object = foreignResults.get(i);
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
    private  List<DistributiedLocalizedEntry>  sameEntityStrategy_wrap(List<? extends CatalogEntry> result,String locale,Long catalogNumericId) throws Exception {
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

}
