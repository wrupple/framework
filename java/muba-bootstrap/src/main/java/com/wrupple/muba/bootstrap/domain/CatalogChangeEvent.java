package com.wrupple.muba.bootstrap.domain;

/**
 * 
 * acts kind of like a compressed version of vegetate's CatalogAction
 * request
 * 
 * @author japi
 *
 */
public interface CatalogChangeEvent extends CatalogIntent{

	String CATALOG = "CatalogChangeEvent";

    public final String DELETE_ACTION = "delete";

    public final String WRITE_ACTION = "write";

    public final String READ_ACTION = "read";

    public final String CREATE_ACTION = "new";

    Object getEntryValue();

}