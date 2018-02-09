package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;

/**
 * Created by japi on 25/07/17.
 */
public class DriverBooking extends ManagedObjectImpl {

    private Long location;
    @ForeignKey(foreignCatalog = Driver.CATALOG)
    private Long driver;
    @CatalogValue(foreignCatalog = Driver.CATALOG)
    private Driver driverValue;


    @Override
    public String getCatalogType() {
        return "DriverBooking";
    }

    public Long getLocation() {
        return location;
    }

    public void setLocation(Long location) {
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
