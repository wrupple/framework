package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.CatalogEntry;

public interface Instrospection {
    void resample(CatalogEntry sample);

    boolean isAccesible();

    void setAccesible(boolean b);
}
