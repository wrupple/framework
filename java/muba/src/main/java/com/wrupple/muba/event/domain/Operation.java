package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.impl.PathToken;

public interface Operation extends CatalogEntry {

    boolean isModeled();

    void appendOperand(Object obtainedData);

    PathToken getPath();
}
