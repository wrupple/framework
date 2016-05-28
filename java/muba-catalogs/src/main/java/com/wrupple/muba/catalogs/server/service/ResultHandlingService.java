package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.catalogs.domain.VegetateColumnResultSet;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;

public interface ResultHandlingService {

	VegetateColumnResultSet createResultSet(boolean summaryFieldsOnly, CatalogExcecutionContext context) throws Exception;

	List<VegetateColumnResultSet> explicitJoin(CatalogExcecutionContext context) throws Exception;

	List<VegetateColumnResultSet> implicitJoin(CatalogExcecutionContext context) throws Exception;
}
