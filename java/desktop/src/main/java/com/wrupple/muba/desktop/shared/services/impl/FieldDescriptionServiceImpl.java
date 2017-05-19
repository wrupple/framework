package com.wrupple.muba.desktop.shared.services.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class FieldDescriptionServiceImpl implements FieldDescriptionService {

	@Inject
	public FieldDescriptionServiceImpl() {
		super();
	}

	@Override
	public Map<String, FieldDescriptor> getCreateDescriptors(CatalogDescriptor catalog) {
		return filterDescriptors('C', catalog);
	}

	@Override
	public Map<String, FieldDescriptor> getFilterDescriptors(CatalogDescriptor catalog) {
		return filterDescriptors('F', catalog);
	}

	@Override
	public Map<String, FieldDescriptor> getUpdateDescriptors(CatalogDescriptor catalog) {
		return filterDescriptors('U', catalog);
	}

	@Override
	public Map<String, FieldDescriptor> getSummaryDescriptors(CatalogDescriptor catalog) {
		return filterDescriptors('S', catalog);
	}

	@Override
	public Map<String, FieldDescriptor> getDetailDescriptors(CatalogDescriptor catalog) {
		return filterDescriptors('D', catalog);
	}

	@Override
	public Map<String, FieldDescriptor> getEphemeralDescriptors(CatalogDescriptor catalog) {
		return filterDescriptors('E', catalog);
	}

	private Map<String, FieldDescriptor> filterDescriptors(char criteria, CatalogDescriptor catalog) {
		Map<String, FieldDescriptor> regreso = new LinkedHashMap<String, FieldDescriptor>();
		Collection<String> fieldNames = catalog.getFieldNames();
		FieldDescriptor curr = null;
		for (String field : fieldNames) {
			curr = catalog.getFieldDescriptor(field);
			switch (criteria) {
			case 'D':
				if (curr.isDetailable() || curr.isWriteable()) {
					regreso.put(curr.getFieldId(), curr);
				}
				break;
			case 'C':
				if (curr.isCreateable() || curr.isWriteable()) {
					regreso.put(curr.getFieldId(), curr);
				}
				break;
			case 'U':
				if (curr.isWriteable()) {
					regreso.put(curr.getFieldId(), curr);
				}
				break;
			case 'F':
				if (curr.isFilterable()) {
					regreso.put(curr.getFieldId(), curr);
				}
				break;
			case 'S':
				if (curr.isSummary()) {
					regreso.put(curr.getFieldId(), curr);
				}
				break;
			case 'E':
				if (curr.isEphemeral()) {
					regreso.put(curr.getFieldId(), curr);
				}
				break;
			}

		}
		return regreso;
	}

}
