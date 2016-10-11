package com.wrupple.muba.catalogs.server.service;

import java.io.IOException;
import java.util.Map;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

public interface CatalogEvaluationDelegate {


	public interface Session {
		void resample(CatalogEntry sample);
	}
	
	Session newSession(CatalogEntry sample);
	
	Object getPropertyValue(CatalogDescriptor catalog,FieldDescriptor field,CatalogEntry entry, DistributiedLocalizedEntry localizedObject,Session session) throws RuntimeException;
	
	void setPropertyValue(CatalogDescriptor catalog,FieldDescriptor field,CatalogEntry entry,Object value,Session session) throws Exception;
	
	public CatalogEntry synthesize(CatalogDescriptor catalog) throws Exception;

	CatalogEntry synthethize(CatalogDescriptor catalog, CatalogEntry seed, CatalogDescriptor seedCatalog, CatalogEntry newSourceEntry, CatalogEntry oldSourceEntry, CatalogActionContext context,Map<String,String> properties) throws Exception;

	Object synthethizeFieldValue(String expression, CatalogEntry entry, CatalogEntry old, CatalogDescriptor catalog, CatalogActionContext context,
			Session session, FieldDescriptor field) throws IOException, Exception;

	<T extends CatalogEntry> T catalogCopy(CatalogDescriptor catalog, T entry) throws Exception;

	boolean isWriteableProperty(String string, CatalogEntry sample, Session session);

	void setPropertyValue(CatalogDescriptor mainCatalog, String reservedField, CatalogEntry e, Object value,
			Session session) throws Exception;



}
