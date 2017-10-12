package com.wrupple.muba.event.domain.reserved;

import com.wrupple.muba.event.domain.CatalogEntry;

import java.util.Collection;
import java.util.List;

/**
 * Created by japi on 12/10/17.
 */
public interface HasResults<R> {
    /**
     * @return
     */
    public <T extends R> List<T> getResults();

    public <T extends R> void setResults(List<T> discriminated);
}
