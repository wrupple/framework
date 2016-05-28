package com.wrupple.muba.catalogs.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.wrupple.muba.catalogs.domain.VegetateColumnResultSet;
import com.wrupple.muba.catalogs.server.service.CatalogEntryAssembler;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class CatalogEntryAssemblerImpl implements CatalogEntryAssembler {
	
	final CatalogPropertyAccesor access;
	
	@Inject
	public CatalogEntryAssemblerImpl(CatalogPropertyAccesor access) {
		this.access=access;
	}

	@Override
	public <T extends CatalogEntry> List<T> processResultSet(
			VegetateColumnResultSet resultSet, CatalogDescriptor catalog) throws Exception {
		HashMap<String, List<Object>> contents = resultSet.getContents();
		
		Collection<FieldDescriptor> fields = catalog.getFieldsValues();
		Set<String> containedFields = contents.keySet();
		T newEntry =null;
		List<Object> fieldContents;
		
		List<T> regreso = null;
		int size =0;
		
		//create empty entries, fill values as fields pass by
		for (String fieldId:containedFields) {
			fieldContents = contents.get(fieldId);
			if(fieldContents==null || fieldContents.isEmpty()){
				return null;
			}
			size = fieldContents.size();
			regreso = new ArrayList<T>(size);
			for(int i  = 0 ; i < size; i++){
				newEntry =(T) access.synthesize(catalog);
				regreso.add(newEntry);
			}
			break;
		}
		
		Object value;
		FieldDescriptor field;
		if(newEntry!=null){
			//at least one entry got created (logically)
			Session session = access.newSession(newEntry);
			for (String fieldId:containedFields) {
				field = catalog.getFieldDescriptor(fieldId);
				if(field!=null){
					//field contents
					fieldContents = contents.get(fieldId);
					if (fieldContents != null) {
						size = fieldContents.size();
						for (int j = 0; j < size; j++) {
							//entry to put field value in
							newEntry = regreso.get(j);
							value = fieldContents.get(j);
							access.setPropertyValue(catalog, field, newEntry, value,session);
						}
					}
				}
				
			}
		}
		
		return regreso;
	}
	

}
