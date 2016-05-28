package com.wrupple.muba.catalogs.server.service;

import java.io.IOException;
import java.util.Map;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.server.chain.command.I18nProcessing.DistributiedLocalizedEntry;

public interface CatalogPropertyAccesor {


	public interface Session {
		void resample(CatalogEntry sample);
	}
	
	Session newSession(CatalogEntry sample);
	
	Object getPropertyValue(CatalogDescriptor catalog,com.wrupple.vegetate.domain.FieldDescriptor field,CatalogEntry entry, DistributiedLocalizedEntry localizedObject,Session session);
	
	void setPropertyValue(CatalogDescriptor catalog,FieldDescriptor field,CatalogEntry entry,Object value,Session session);
	
	public CatalogEntry synthesize(CatalogDescriptor catalog) throws Exception;

	CatalogEntry synthethize(CatalogDescriptor catalog, CatalogEntry seed, CatalogDescriptor seedCatalog, CatalogEntry newSourceEntry, CatalogEntry oldSourceEntry, CatalogExcecutionContext context,Map<String,String> properties) throws Exception;

	Object synthethizeFieldValue(String expression, CatalogEntry entry, CatalogEntry old, CatalogDescriptor catalog, CatalogExcecutionContext context,
			Session session, FieldDescriptor field) throws IOException;

	void evaluate(String expression, CatalogEntry entry, CatalogEntry old, CatalogExcecutionContext context);

	<T extends CatalogEntry> T catalogCopy(CatalogDescriptor catalog, T entry) throws Exception;
}
