package com.wrupple.muba.event.domain;


public interface CatalogEntry extends Entity {

	public static final int NUMERIC_DATA_TYPE = 1;
	public static final int INTEGER_DATA_TYPE = 2;
	public static final int DATE_DATA_TYPE = 3;
	public static final int CATALOG_ENTRY_DATA_TYPE = 4;
	public static final int BLOB_DATA_TYPE = 5;
	public static final int OBJECT_DATA_TYPE = 7;
	public static final int LARGE_STRING_DATA_TYPE = 12;
	public static final int STRING_DATA_TYPE = 51;
	public static final int BOOLEAN_DATA_TYPE = 1001001;


	public static final long PUBLIC_ID = -1;
	public static final String WRUPPLE = "Wrupple";
	public static final long WRUPPLE_ID = 0;
	public static final String PUBLIC = "anonymouslyVisible";
	public static final String DRAFT_FIELD = "draft";
	public static final String NAME_FIELD = "name";
	public static final String DOMAIN_TOKEN = "domain";
	

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
	Object getDomain();

	boolean isAnonymouslyVisible();

	void setAnonymouslyVisible(boolean p);

}
