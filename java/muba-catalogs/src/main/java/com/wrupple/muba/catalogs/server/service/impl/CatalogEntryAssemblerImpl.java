package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionResult;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogEntryAssembler;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;

import javax.inject.Inject;
import java.util.*;

public class CatalogEntryAssemblerImpl implements CatalogEntryAssembler {


	private FieldAccessStrategy access;

	@Inject
	public CatalogEntryAssemblerImpl(FieldAccessStrategy cms) {
		this.access =cms;
		
	}
	@Override
	public List<CatalogEntry> processMultipleResponse(CatalogActionResult response, CatalogDescriptor catalogdescriptor)
			throws Exception {
		List<CatalogColumnResultSet> resultSets = (List<CatalogColumnResultSet>) response.getResponse();
		if (resultSets == null || resultSets.isEmpty()) {
			return null;
		} else {
			// TODO process joined result sets
			CatalogColumnResultSet mainResultSet = resultSets.get(0);
			List<CatalogEntry> regreso = processResultSet(mainResultSet, catalogdescriptor);
			return regreso;
		}
	}
	@Override
	public CatalogEntry processSingleResponse(CatalogActionResult response, CatalogDescriptor catalogdescriptor)
			throws Exception {
		List<CatalogColumnResultSet> resultSets = (List<CatalogColumnResultSet>) response.getResponse();
		if (resultSets == null || resultSets.isEmpty()) {
			return null;
		} else {
			// TODO process joined result sets
			CatalogColumnResultSet mainResultSet = resultSets.get(0);
			List<CatalogEntry> regreso = processResultSet(mainResultSet, catalogdescriptor);

			if (regreso == null || regreso.isEmpty()) {
				return null;
			} else {
				return regreso.get(0);
			}
		}
	}

	@Override
	public <T extends CatalogEntry> List<T> processResultSet(CatalogColumnResultSet resultSet,
			CatalogDescriptor catalog) throws Exception {
		HashMap<String, List<Object>> contents = resultSet.getContents();

		Collection<FieldDescriptor> fields = catalog.getFieldsValues();
		Set<String> containedFields = contents.keySet();
		T newEntry = null;
		List<Object> fieldContents;

		List<T> regreso = null;
		int size = 0;

		// create empty entries, fill values as fields pass by
		for (String fieldId : containedFields) {
			fieldContents = contents.get(fieldId);
			if (fieldContents == null || fieldContents.isEmpty()) {
				return null;
			}
			size = fieldContents.size();
			regreso = new ArrayList<T>(size);
			for (int i = 0; i < size; i++) {
                newEntry = (T) access.synthesize(catalog);
                regreso.add(newEntry);
			}
			break;
		}

		Object value;
		FieldDescriptor field;
		if (newEntry != null) {
			// at least one entry got created (logically)
            Instrospection instrospection = access.newSession(newEntry);
            for (String fieldId : containedFields) {
				field = catalog.getFieldDescriptor(fieldId);
				if (field != null) {
					// field contents
					fieldContents = contents.get(fieldId);
					if (fieldContents != null) {
						size = fieldContents.size();
						for (int j = 0; j < size; j++) {
							// entry to put field value in
							newEntry = regreso.get(j);
							value = fieldContents.get(j);
                            access.setPropertyValue(field, newEntry, value, instrospection);
                        }
					}
				}

			}
		}

		return regreso;
	}

}
