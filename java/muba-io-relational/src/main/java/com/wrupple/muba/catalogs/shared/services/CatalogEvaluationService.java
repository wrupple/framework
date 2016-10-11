package com.wrupple.muba.catalogs.shared.services;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;

public interface CatalogEvaluationService {

	void evaluate(String expression, CatalogEntry entry, CatalogEntry old, CatalogActionContext context);

}
