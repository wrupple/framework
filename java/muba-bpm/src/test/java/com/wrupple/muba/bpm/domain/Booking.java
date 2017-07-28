package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntryImpl;
import com.wrupple.muba.catalogs.domain.annotations.CatalogKey;
import com.wrupple.muba.catalogs.domain.annotations.CatalogValue;

/**
 * Created by japi on 25/07/17.
 */
public class Booking extends CatalogEntryImpl {

    private int location;
    @CatalogKey(foreignCatalog = Driver.CATALOG)
    private Long driver;
    @CatalogValue(foreignCatalog = Driver.CATALOG)
    private Driver driverValue;


    @Override
    public String getCatalogType() {
        return "Booking";
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public Long getDriver() {
        return driver;
    }

    public void setDriver(Long driver) {
        this.driver = driver;
    }

    public Driver getDriverValue() {
        return driverValue;
    }

    public void setDriverValue(Driver driverValue) {
        this.driverValue = driverValue;
    }
}
