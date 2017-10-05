package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasCatalogKey;

/**
 * Created by japi on 4/08/17.
 */
public interface FieldValueChange extends CatalogEntry ,HasCatalogKey{
    final String CATALOG = "FieldValueChange";
    //name is fieldId
    String getOldValue();
    String getValue();
}