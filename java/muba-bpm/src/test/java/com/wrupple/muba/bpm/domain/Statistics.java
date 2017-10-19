package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.impl.ContentNodeImpl;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;

/**
 * Created by japi on 5/10/17.
 */
public class Statistics extends ContentNodeImpl implements HasCatalogId{

    public static final String CATALOG = "Statistics";
    public static final String COUNT_FIELD = "count";
    private String catalog;
    private Long count;

    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
