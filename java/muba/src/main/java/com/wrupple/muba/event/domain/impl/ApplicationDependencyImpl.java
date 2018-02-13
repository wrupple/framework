package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.ApplicationDependency;

public class ApplicationDependencyImpl extends CatalogEntryImpl implements ApplicationDependency {

    private String discriminator;

    @Override
    public String getDiscriminator() {
        return discriminator;
    }


    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }
}
