package com.wrupple.muba.event.domain;

public interface Operation extends CatalogEntry {

    boolean isModeled();

    void appendOperand(Object obtainedData);
}
