package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;

import java.util.List;

/**
 * Created by japi on 5/05/18.
 */
public class CatalogRelation {

    private String foreignCatalog;
    private CatalogDescriptor foreignCatalogValue;
    private String foreignField;
    private FieldDescriptor foreignFieldValue;
    private String localField;
    private FieldDescriptor localFieldValue;
    private List<CatalogEntry> results;

    public CatalogRelation() {
        super();
    }
    public CatalogRelation(String foreignCatalogId, String foreignField, String localField) {
        this();
        this.foreignCatalog=foreignCatalogId;
        this.foreignField=foreignField;
        this.localField=localField;
    }

    public String getForeignCatalog() {
        return foreignCatalog;
    }

    public void setForeignCatalog(String foreignCatalog) {
        this.foreignCatalog = foreignCatalog;
    }

    public CatalogDescriptor getForeignCatalogValue() {
        return foreignCatalogValue;
    }

    public void setForeignCatalogValue(CatalogDescriptor foreignCatalogValue) {
        this.foreignCatalogValue = foreignCatalogValue;
    }

    public String getForeignField() {
        return foreignField;
    }

    public void setForeignField(String foreignField) {
        this.foreignField = foreignField;
    }

    public FieldDescriptor getForeignFieldValue() {
        return foreignFieldValue;
    }

    public void setForeignFieldValue(FieldDescriptor foreignFieldValue) {
        this.foreignFieldValue = foreignFieldValue;
    }

    public String getLocalField() {
        return localField;
    }

    public void setLocalField(String localField) {
        this.localField = localField;
    }

    public FieldDescriptor getLocalFieldValue() {
        return localFieldValue;
    }

    public void setLocalFieldValue(FieldDescriptor localFieldValue) {
        this.localFieldValue = localFieldValue;
    }

    public FieldFromCatalog getKey() {
        return new FieldFromCatalog(foreignCatalog,foreignField);
    }

    public void setResults(List<CatalogEntry> results) {
        this.results = results;
    }

    public List<CatalogEntry> getResults() {
        return results;
    }
}
