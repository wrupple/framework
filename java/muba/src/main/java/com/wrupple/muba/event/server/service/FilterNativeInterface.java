package com.wrupple.muba.event.server.service;

import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterCriteria;
import com.wrupple.muba.event.domain.Instrospection;

import java.util.List;
import java.util.Map;

/**
 * Created by rarl on 7/06/17.
 */
public interface FilterNativeInterface {

    boolean matchAgainstFilters(CatalogEntry entry, List<FilterCriteria> filters, Map<String, FieldDescriptor> filterableFields, Instrospection instrospection);

    public  boolean jsMatch(String pathing, CatalogEntry o, List<Object> values, int valueIndex, Instrospection instrospection);

}
