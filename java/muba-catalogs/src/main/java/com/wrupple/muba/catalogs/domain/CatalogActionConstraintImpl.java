package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

import java.util.List;

/**
 * Created by japi on 19/10/17.
 */
public abstract class CatalogActionConstraintImpl extends CatalogEntryImpl implements CatalogActionConstraint {

    @CatalogField(filterable = true)
    private String catalog;
    private String seed;
    private String entry;
    private String description;

    private List<String> properties,sentence;





    public String getSeed() {
        return seed;
    }
    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getCatalog() {
        return catalog;
    }
    public void setCatalog(String targetCatalogId) {
        this.catalog = targetCatalogId;
    }
    public List<String> getProperties() {
        return properties;
    }
    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }



    @Override
    public void setEntry(Object id) {
        setEntry((String)id);
    }

    @Override
    public Object getEntryValue() {

        return null;
    }



    @Override
    public List<String> getSentence() {
        return sentence;
    }

    public void setSentence(List<String> sentence) {
        this.sentence = sentence;
    }
}
