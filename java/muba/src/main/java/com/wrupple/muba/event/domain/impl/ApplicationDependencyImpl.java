package com.wrupple.muba.event.domain.impl;

import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
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
