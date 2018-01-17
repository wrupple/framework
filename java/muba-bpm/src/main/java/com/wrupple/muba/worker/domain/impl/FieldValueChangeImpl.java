package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import com.wrupple.muba.worker.domain.FieldValueChange;

/**
 * Created by japi on 4/08/17.
 */
public class FieldValueChangeImpl extends CatalogEntryImpl implements FieldValueChange {
    private String oldValue, value, entry,catalog;

    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    @Override
    public String getEntry() {
        return entry;
    }

    @Override
    public void setEntry(Object id) {
        setEntry((String)id);
    }

    @Override
    public Object getEntryValue() {
        return null;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }
}
