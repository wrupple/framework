package com.wrupple.muba.catalogs.shared.service;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

import java.util.List;

/**
 * Created by rarl on 7/06/17.
 */
public interface FilterNativeInterface {

    boolean matchAgainstFilters(CatalogEntry entry, List<FilterCriteria> filters, CatalogDescriptor descriptor, FieldAccessStrategy.Session session);

    public  boolean jsMatch(String pathing, CatalogEntry o, List<Object> values, int valueIndex, FieldAccessStrategy.Session session);

}
