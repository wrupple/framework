package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.CatalogEntryImpl;

/**
 * Created by japi on 26/07/17.
 */
public class Driver extends CatalogEntryImpl {
    public static final String CATALOG = "Driver";

    private int location;

    private boolean available;


    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
