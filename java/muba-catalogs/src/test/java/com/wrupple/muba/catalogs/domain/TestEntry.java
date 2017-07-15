package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntryImpl;

/**
 * Created by japi on 14/07/17.
 */
public class TestEntry extends CatalogEntryImpl {

    private int number;

    @Override
    public String getCatalogType() {
        return "TestEntry";
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
