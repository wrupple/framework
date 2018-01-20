package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

public class ContainerStateImpl extends CatalogEntryImpl implements ContainerState {

    public ContainerStateImpl() {
        setDomain(PUBLIC_ID);
    }

    public ContainerStateImpl(Long domain) {
        setDomain(domain);
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }


}
