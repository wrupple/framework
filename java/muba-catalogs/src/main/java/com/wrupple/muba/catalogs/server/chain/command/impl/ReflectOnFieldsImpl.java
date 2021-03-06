package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogRelation;
import com.wrupple.muba.catalogs.domain.DataJoinContext;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.FieldSynthesizer;
import org.apache.commons.chain.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

/**
 * Created by japi on 5/05/18.
 */
@Singleton
public class ReflectOnFieldsImpl implements Command<DataJoinContext> {

    protected static final Logger log = LogManager.getLogger(ReflectOnFieldsImpl.class);
    private final FieldSynthesizer synthesizer;

    private final FieldAccessStrategy access;
    private final CatalogKeyServices keydelegate;

    @Inject
    public ReflectOnFieldsImpl(FieldSynthesizer synthesizer, FieldAccessStrategy access, CatalogKeyServices keydelegate) {
        this.synthesizer = synthesizer;
        this.access = access;
        this.keydelegate = keydelegate;
    }

    @Override
    public boolean execute(DataJoinContext context) throws Exception {
        CatalogRelation relation = context.getWorkingRelation();
        CatalogDescriptor mainCatalog = context.getMain().getCatalogDescriptor();
        CatalogDescriptor joinCatalog = relation.getForeignCatalogValue();
        log.trace("Working Catalog Graph of {} and {}", mainCatalog.getDistinguishedName(),
                joinCatalog.getDistinguishedName());
        Collection<FieldDescriptor> fields = mainCatalog.getFieldsValues();
        List<CatalogEntry> mainResults = context.getResults();
        CatalogEntry sample = mainResults.get(0);

        Collection<Object> needs;
        Object need;
        Collection<CatalogEntry> matches;
        CatalogEntry match;
        String reservedField;
        Instrospection instrospection = context.getIntrospectionSession();
        List<CatalogEntry> joins = relation.getResults();
        if(joins!=null&&!joins.isEmpty()){
            Map<Object, CatalogEntry> key = null;
            for (FieldDescriptor field : fields) {
                if ( keydelegate.isJoinableValueField(field)
                        && field.getCatalog().equals(joinCatalog.getDistinguishedName())) {
                    if (field.isKey()) {
                        if (field.isMultiple()) {
                            reservedField = field.getDistinguishedName() + CatalogEntry.MULTIPLE_FOREIGN_KEY;
                            if (access.isWriteableProperty(reservedField, sample, instrospection)) {
                                log.trace("Working field {}", field.getDistinguishedName());
                                for (CatalogEntry e : mainResults) {
                                    matches = (Collection<CatalogEntry>) access.getPropertyValue(reservedField, e, null, instrospection);
                                    if(matches==null){
                                        needs = (Collection<Object>) access.getPropertyValue(field, e, null, instrospection);
                                        if (needs != null) {
                                            if (key == null) {
                                                key = mapJoins(new HashMap<Object, CatalogEntry>(joins.size()), joins);
                                            }
                                            matches = new ArrayList<CatalogEntry>(needs.size());
                                            for (Object required : needs) {
                                                match = key.get(required);
                                                matches.add(match);
                                            }
                                            access.setPropertyValue(reservedField, e, matches, instrospection);
                                        }
                                    }
                                }

                            }
                        } else {
                            reservedField = field.getDistinguishedName() + CatalogEntry.FOREIGN_KEY;
                            if (access.isWriteableProperty(reservedField, sample, instrospection)) {
                                log.trace("Working one to many relationship {}", field.getDistinguishedName());
                                if (key == null) {
                                    key = mapJoins(new HashMap<Object, CatalogEntry>(joins.size()), joins);
                                }
                                for (CatalogEntry e : mainResults) {
                                    match = (CatalogEntry) access.getPropertyValue(reservedField, e, null, instrospection);
                                    if(match==null){
                                        need = access.getPropertyValue(field, e, null, instrospection);
                                        match = key.get(need);
                                        access.setPropertyValue(reservedField, e, match, instrospection);
                                    }
                                }

                            }
                        }

                    } else if (field.isGenerated()&&!keydelegate.isLocalJoinField(field,mainCatalog)) {
                        if (field.getSentence() == null||field.getSentence().isEmpty()) {
                            log.trace("Working many to one relationship {}", field.getDistinguishedName());
                            reservedField =  keydelegate.getFieldWithForeignType(joinCatalog,mainCatalog.getDistinguishedName());
                            FieldDescriptor foreignField = joinCatalog.getFieldDescriptor(reservedField);
                            Object temp;
                            Object contents;
                            for (CatalogEntry e : mainResults) {
                                contents = access.getPropertyValue(field,e, null, instrospection);
                                if(contents==null){
                                    matches=null;
                                    need = e.getId();
                                    for (CatalogEntry i : joins) {
                                        temp = access.getPropertyValue(foreignField, i, null, instrospection);
                                        if (need.equals(temp)) {
                                            if(field.isMultiple()){
                                                if (matches == null) {
                                                    matches = new ArrayList<CatalogEntry>();
                                                }
                                                matches.add(i);
                                            }else{
                                                access.setPropertyValue(field.getDistinguishedName(), e, i, instrospection);
                                                break;
                                            }
                                        }
                                    }
                                    access.setPropertyValue(field.getDistinguishedName(), e, matches, instrospection);
                                }
                            }
                        } else {
                            for (CatalogEntry e : mainResults) {
                                Object criteriaValue = synthesizer.synthethizeFieldValue(field.getSentence().listIterator(), context, e, mainCatalog, field, instrospection,context.getMain().getRuntimeContext().getServiceBus());
                                access.setPropertyValue(field.getDistinguishedName(), e, criteriaValue, instrospection);
                            }
                        }
                    }
                }
            }
        }
        return CONTINUE_PROCESSING;
    }




    private  Map<Object, CatalogEntry> mapJoins(HashMap<Object, CatalogEntry> hashMap, List<CatalogEntry> entries) {
        for (CatalogEntry e : entries) {
            hashMap.put(e.getId(), e);
        }
        return hashMap;
    }
}
