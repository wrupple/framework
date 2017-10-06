package com.wrupple.muba.event.domain;

import java.util.Collection;
import java.util.List;

import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.event.domain.reserved.HasProperties;

public interface ContractDescriptor extends HasDistinguishedName,HasProperties, CatalogKey {

	/* 
	 * NOT VISIBLE TO CLIENTS
	 * (non-Javadoc)
	 * @see com.wrupple.vegetate.domain.ForeignKey#getId()
	 */
	public Long getId();
	/**
	 * @return the clazz
	 */
	public Class<? extends CatalogEntry> getClazz();
	
	public void setClazz(Class<? extends CatalogEntry> clazz) ;
	

	/**
	 * @return the keyField
	 */
	public String getKeyField();

	
	/**
	 * @return The ID of the human-readable most descriptive field of this
	 *         catalog, by default it's the keyField, but this may not always be
	 *         the most intuitive field.
	 */
	public String getDescriptiveField();
	
	
	/**
	 * CatalogEvaluationDelegate will evaluate each of these statements while
	 * initializing and evaluation context for this catalog.
	 * 
	 * Posible use is to define functions specific to this catalog
	 * 
	 * @return
	 */
	public List<String> getContextExpressions();
	public Collection<String> getFieldsIds();
	

}
