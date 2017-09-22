package com.wrupple.muba.event.server.service;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Instrospection;

/**
 * Created by japi on 22/09/17.
 */
public interface IntrospectionStrategy {


    Instrospection newSession(CatalogEntry sample);

}
