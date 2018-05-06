package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.reserved.HasResults;
import org.apache.commons.chain.impl.ContextBase;

import java.util.*;

/**
 * Created by japi on 5/05/18.
 */
public class DataJoinContext extends ContextBase {
    private final CatalogActionContext main;
    private List<CatalogRelation> joins;
    private Map<FieldFromCatalog,Set<Object>> fieldValueMap;
    private final Instrospection introspectionSession;
    private CatalogRelation workingRelation;
    private boolean buildResultSet;

    public DataJoinContext(CatalogActionContext main,Instrospection introspectionSession) {
        this.main = main;
        this.introspectionSession=introspectionSession;
    }

    public CatalogActionContext getMain() {
        return main;
    }

    public List<CatalogRelation> getJoins() {
        return joins;
    }

    public void setJoins(List<CatalogRelation> joins) {
        this.joins = joins;
    }


    private Map<FieldFromCatalog, Set<Object>> createFilterMap() {
        Map<FieldFromCatalog, Set<Object>> regreso = new HashMap<FieldFromCatalog, Set<Object>>(joins.size());
        int size = main.getResults().size();
        FieldFromCatalog key;
        for (CatalogRelation statement : joins) {

            key = statement.getKey();
            if (!regreso.containsKey(key)) {
                regreso.put(key, new HashSet<Object>(size));
            }
        }
        return regreso;
    }

    public Map<FieldFromCatalog,Set<Object>> getFieldValueMap() {
        if(fieldValueMap==null&&joins!=null)
        {
            fieldValueMap=createFilterMap();
        }
        return fieldValueMap;
    }

    public Instrospection getIntrospectionSession() {
        return introspectionSession;
    }

    public CatalogRelation getWorkingRelation() {
        return workingRelation;
    }

    public void setWorkingRelation(CatalogRelation workingRelation) {
        this.workingRelation = workingRelation;
    }

    public boolean isBuildResultSet() {
        return buildResultSet;
    }

    public void setBuildResultSet(boolean buildResultSet) {
        this.buildResultSet = buildResultSet;
    }
}
