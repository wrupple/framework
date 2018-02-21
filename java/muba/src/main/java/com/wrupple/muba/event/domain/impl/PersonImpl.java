package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.Person;

public class PersonImpl extends CatalogEntryImpl implements Person {
    private String locale;

    @Override
    public String getCatalogType() {
        return Person.CATALOG;
    }


    @Override
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
