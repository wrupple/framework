package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogFieldSentence;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;
import com.wrupple.muba.event.server.service.impl.NaturalLanguageInterpretImpl;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Created by japi on 25/07/17.
 */
public class RiderBooking extends ManagedObjectImpl {

    private Long location;
    @ForeignKey(foreignCatalog = Driver.CATALOG)
    private Long driver;
    @CatalogValue(foreignCatalog = Driver.CATALOG)
    private Driver driverValue;
    @CatalogField(generated = true)
    @CatalogFieldSentence(formula={NaturalLanguageInterpretImpl.ASSIGNATION,"driver","location","-","location"})
    @Min(value = 0)
    @Max(value = 100)
    private Integer bookingDistance;


    @Override
    public String getCatalogType() {
        return RiderBooking.class.getSimpleName();
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

    public Integer getBookingDistance() {
        return bookingDistance;
    }

    public void setBookingDistance(Integer bookingDistance) {
        this.bookingDistance = bookingDistance;
    }
}
