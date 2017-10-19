package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.event.domain.reserved.HasProperties;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;

import java.util.List;

/**
 * Created by japi on 19/10/17.
 */
public interface Service extends HasCatalogId,HasProperties,HasStakeHolder,HasDistinguishedName {


    /**
     * Note: all services that wish to conform to  security should declare it's first token to be CatalogEntry.DOMAIN_FIELD
     *
     * @return
     */
    List<String> getGrammar();


}
