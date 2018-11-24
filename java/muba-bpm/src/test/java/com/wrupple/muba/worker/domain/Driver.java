package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import com.wrupple.muba.event.domain.impl.ContentNodeImpl;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;

/**
 * Created by japi on 26/07/17.
 */
public class Driver extends ManagedObjectImpl {
    public static final String CATALOG = "Driver";

    private Long location;

    private Boolean available;


    public Long getLocation() {
        return location;
    }

    public void setLocation(Long location) {
        this.location = location;
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    public Boolean getAvailable() {
        return available;
    }

    public Boolean isAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
