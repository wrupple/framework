package com.wrupple.muba.bootstrap.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasCatalogId;

public interface ImplicitIntent extends HasCatalogId {

	String getOutputCatalog();
	
}
