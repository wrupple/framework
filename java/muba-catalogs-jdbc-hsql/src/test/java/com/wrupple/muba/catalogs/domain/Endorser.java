package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

import java.util.List;

/**
 * Created by japi on 18/08/17.
 */
public class Endorser extends CatalogEntryImpl {
    public static final String CATALOG = "Endorser";

    @CatalogField(ephemeral = true)
    @CatalogValue(foreignCatalog = Credit.CATALOG)
    private List<Credit> creditsValues;

    public List<Credit> getCreditsValues() {
        return creditsValues;
    }

    public void setCreditsValues(List<Credit> creditsValues) {
        this.creditsValues = creditsValues;
    }

    @Override
    public String getCatalogType() {
        return getClass().getSimpleName();
    }
}
