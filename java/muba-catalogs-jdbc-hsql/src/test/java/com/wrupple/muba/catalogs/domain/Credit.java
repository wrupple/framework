package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

import javax.validation.constraints.NotNull;

public class Credit extends CatalogEntryImpl  {

    public static final String CATALOG = "Credit";
    @ForeignKey(foreignCatalog =  Endorser.CATALOG)
    @CatalogField(filterable = true)
    private Long endorser;

    @NotNull
    @CatalogValue(foreignCatalog = Endorser.CATALOG)
    private Endorser endorserValue;

    @Override
    public String getCatalogType() {
        return Credit.CATALOG;
    }

    public Endorser getEndorserValue() {
        return endorserValue;
    }

    public void setEndorserValue(Endorser endorserValue) {
        this.endorserValue = endorserValue;
    }

    public Long getEndorser() {
        return endorser;
    }

    public void setEndorser(Long endorser) {
        this.endorser = endorser;
    }
}
