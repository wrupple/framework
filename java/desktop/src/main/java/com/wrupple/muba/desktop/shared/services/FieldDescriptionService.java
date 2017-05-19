package com.wrupple.muba.desktop.shared.services;

import java.util.Map;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

public interface FieldDescriptionService {
	/**
	 * filters field descriptors from a catalog that are appropiate for a Create
	 * Form
	 * 
	 * @param catalog
	 *            the catalog to filter out
	 * @return a map containing FieldDescriptors for a Create operation
	 */
	public Map<String, FieldDescriptor> getCreateDescriptors(CatalogDescriptor catalog);

	/**
	 * filters field descriptors from a catalog that are appropiate for a Filter
	 * Form
	 * 
	 * @param catalog
	 *            the catalog to filter out
	 * @returna map containing FieldDescriptors for a Filter operation
	 */
	public Map<String, FieldDescriptor> getFilterDescriptors(CatalogDescriptor catalog);

	/**
	 * filters field descriptors from a catalog that are appropiate for am
	 * Update Form
	 * 
	 * @param catalog
	 *            the catalog to filter out
	 * @returna map containing FieldDescriptors for an Update operation
	 */
	public Map<String, FieldDescriptor> getUpdateDescriptors(CatalogDescriptor catalog);

	/**
	 * filters field descriptors from a catalog that are appropiate for the
	 * summary table
	 * 
	 * @param catalog
	 *            the catalog to filter out
	 * @returna map containing FieldDescriptors for a Summary operation
	 */
	public Map<String, FieldDescriptor> getSummaryDescriptors(CatalogDescriptor catalog);

	/**
	 * filters field descriptors from a catalog that are appropiate for the
	 * detail of an entry
	 * 
	 * @param catalog
	 *            the catalog to filter out
	 * @returna map containing FieldDescriptors for a Summary operation
	 */
	public Map<String, FieldDescriptor> getDetailDescriptors(CatalogDescriptor catalog);

	public Map<String, FieldDescriptor> getEphemeralDescriptors(CatalogDescriptor descriptor);

}
