package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogFieldSentence;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.wrupple.muba.event.domain.Constraint.EVALUATING_VARIABLE;

/**
 * Created by japi on 25/07/17.
 */
public class RiderBooking extends ManagedObjectImpl {

    private Long location;
    @ForeignKey(foreignCatalog = Driver.CATALOG)
    private Long driver;
    @CatalogValue(foreignCatalog = Driver.CATALOG)
    private Driver driverValue;
    @CatalogField(ephemeral = true)
    @CatalogFieldSentence(formula={EVALUATING_VARIABLE,"driver","location","-","location"})
    @Min(value = 0)
    @Max(value = 100)
    private Integer driverDistance;


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

    public Integer getDriverDistance() {
        return driverDistance;
    }

    public void setDriverDistance(Integer driverDistance) {
        this.driverDistance = driverDistance;
    }
}
