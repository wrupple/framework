package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogRelation;
import com.wrupple.muba.catalogs.domain.DataJoinContext;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import org.apache.commons.chain.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.wrupple.muba.event.server.service.impl.FilterDataUtils.newFilterCriteria;
import static com.wrupple.muba.event.server.service.impl.FilterDataUtils.newFilterData;

/**
 * Created by japi on 5/05/18.
 */
public class ProcessJoinsImpl implements Command<DataJoinContext> {
    protected static final Logger log = LogManager.getLogger(ProcessJoinsImpl.class);

    @Override
    public boolean execute(DataJoinContext context) throws Exception {
        /*
		 * Gather join results for statement
		 */
        CatalogRelation relation = context.getWorkingRelation();
        String catalogId = relation.getForeignCatalog();
        CatalogDescriptor catalog = context.getMain().getDescriptorForName(catalogId);
        relation.setForeignCatalogValue(catalog);
        if(catalog==null){
            throw new NullPointerException("No such catalog "+catalogId);
        }
        List<CatalogEntry> mainResults = context.getMain().getResults();
        String foreignField = context.getWorkingRelation().getForeignField();
        Set<Object> fieldValues = context.getFieldValueMap().get(relation.getKey());
        List<CatalogEntry> currentMatchingEntries = getjoinCandidates(mainResults, context.getMain(), catalog, foreignField,
                fieldValues);

        context.getWorkingRelation().setResults(currentMatchingEntries);
        if (currentMatchingEntries == null || currentMatchingEntries.isEmpty()) {
            return PROCESSING_COMPLETE;
        } else {
            //workJoinData(mainResults, mainCatalog, currentMatchingEntries, catalog, context, instrospection);
            return CONTINUE_PROCESSING;
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
            CatalogResultCache cache = context.getCache(catalog, context);
            currentMatchingEntries = cache.satisfy(context,catalog,currentQueryFilter);
            if (currentMatchingEntries == null) {
                currentMatchingEntries = context.triggerRead(catalog.getDistinguishedName(),currentQueryFilter);
            }
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

}
