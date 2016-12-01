package com.wrupple.muba.bootstrap.domain.reserved;

public interface HasCatalogId {
	
	String CATALOG_FIELD = "catalog";

	Object getCatalog();
	
	
	void setCatalog(String catalog);

}
