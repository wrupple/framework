package com.wrupple.muba.event.domain;


public interface CatalogEntry extends Entity {

	int NUMERIC_DATA_TYPE = 1;
	int INTEGER_DATA_TYPE = 2;
	int DATE_DATA_TYPE = 3;
	int CATALOG_ENTRY_DATA_TYPE = 4;
	int BLOB_DATA_TYPE = 5;
	int OBJECT_DATA_TYPE = 7;
	int LARGE_STRING_DATA_TYPE = 12;
	int STRING_DATA_TYPE = 51;
	int BOOLEAN_DATA_TYPE = 1001001;


	long PUBLIC_ID = -1;
	long CURRENT_NAMESPACE = 0;
	String PUBLIC = "anonymouslyVisible";
	String NAME_FIELD = "name";
	String DOMAIN_FIELD = "domain";


	/**
	 * sets the id of the  namespace for this
	 * entry
	 *
	 * @param domain
	 *            t
	 */
	void setDomain(Long domain);

	/**
	 *  the GUID consists on a combination of domain, catalog, and id then
	 * 
	 * @return the identifier for the Organizational Unit (ou) this entry belongs to
	 */
	Long getDomain();

	boolean isAnonymouslyVisible();

	void setAnonymouslyVisible(boolean p);

}
