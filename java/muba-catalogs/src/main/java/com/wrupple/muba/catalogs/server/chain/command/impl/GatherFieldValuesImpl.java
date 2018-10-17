package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogRelation;
import com.wrupple.muba.catalogs.domain.DataJoinContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
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
        List<CatalogEntry> results = context.getResults();

        if (context.getMain().getResults() == null || context.getMain().getResults().isEmpty()) {
            throw new RuntimeException("no results to join");
        } else {
            CatalogDescriptor catalog = context.getMain().getCatalogDescriptor();
            CatalogRelation relation = context.getWorkingRelation();
            String fieldId = relation.getLocalField();
            String reservedField;
            FieldDescriptor field = catalog.getFieldDescriptor(fieldId);
            if (field == null) {
                throw new IllegalArgumentException("No such field "+fieldId+" in "+catalog.getDistinguishedName());
            } else {
                // this filter building method is built expressly for joining key (unique) values
                Set<Object> fieldValues = context.getFieldValueMap().get(relation.getKey());

                if (field.isMultiple()) {
                    reservedField = field.getFieldId() + CatalogEntry.MULTIPLE_FOREIGN_KEY;
                    Collection<?> temp;
                    Collection<CatalogEntry> matches;

                    for (CatalogEntry e : results) {
                        if(access.isWriteableProperty(reservedField,e,context.getIntrospectionSession())) {
                            matches = (Collection<CatalogEntry>) access.getPropertyValue(reservedField,e,null,context.getIntrospectionSession());
                            if(matches!=null&&matches.isEmpty()){
                                matches=null;
                                access.setPropertyValue(reservedField,e,matches,context.getIntrospectionSession());
                            }
                        }else{
                            matches=null;
                        }
                        if(matches==null||context.isBuildResultSet()){
                            temp = (Collection<?>) access.getPropertyValue(field, e, null, context.getIntrospectionSession());
                            if (temp != null) {
                                for (Object o : temp) {
                                    if (o != null) {
                                        fieldValues.add(o);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Object value;
                    CatalogEntry result;
                    reservedField = field.getFieldId() + CatalogEntry.FOREIGN_KEY;
                    for (CatalogEntry e : results) {
                        if(access.isWriteableProperty(reservedField,e,context.getIntrospectionSession())){
                            result = (CatalogEntry) access.getPropertyValue(reservedField,e,null,context.getIntrospectionSession());
                        }else{
                            result = null;
                        }

                        if(result==null||context.isBuildResultSet()){
                            value = access.getPropertyValue(field, e, null, context.getIntrospectionSession());

                            if (value != null) {
                                fieldValues.add(value);
                            }
                        }
                    }
                }
            }
        }
        return CONTINUE_PROCESSING;
    }



}
