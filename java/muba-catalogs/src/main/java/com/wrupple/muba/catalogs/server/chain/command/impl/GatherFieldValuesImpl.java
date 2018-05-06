package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogRelation;
import com.wrupple.muba.catalogs.domain.DataJoinContext;
import com.wrupple.muba.catalogs.domain.FieldFromCatalog;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by japi on 5/05/18.
 */
@Singleton
public class GatherFieldValuesImpl implements Command<DataJoinContext> {

    private final FieldAccessStrategy access;

    @Inject
    public GatherFieldValuesImpl(FieldAccessStrategy access) {
        this.access = access;
    }



    @Override
    public boolean execute(DataJoinContext context) throws Exception {
        if (context.getMain().getResults() == null || context.getMain().getResults().isEmpty()) {
            throw new RuntimeException("no results to join");
        } else {
            CatalogDescriptor catalog = context.getMain().getCatalogDescriptor();
            CatalogRelation relation = context.getWorkingRelation();
            String fieldId = relation.getLocalField();
            FieldDescriptor field = catalog.getFieldDescriptor(fieldId);
            if (field == null) {
                throw new IllegalArgumentException("No such field "+fieldId+" in "+catalog.getDistinguishedName());
            } else {
                // this filter building method is built expressly for joining key (unique) values
                List<CatalogEntry> results = context.getMain().getResults();
                Set<Object> fieldValues = context.getFieldValueMap().get(relation.getKey());

                if (field.isMultiple()) {
                    Collection<?> temp;
                    for (CatalogEntry e : results) {
                        temp = (Collection<?>) access.getPropertyValue(field, e, null, context.getIntrospectionSession());
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
                        value = access.getPropertyValue(field, e, null, context.getIntrospectionSession());
                        if (value != null) {
                            fieldValues.add(value);
                        }
                    }
                }
            }

        }
        return CONTINUE_PROCESSING;
    }



}
