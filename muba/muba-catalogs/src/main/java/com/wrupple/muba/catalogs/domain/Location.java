package com.wrupple.muba.catalogs.domain;

/**
 * 
 * https://cloud.google.com/appengine/docs/java/javadoc/com/google/appengine/api/datastore/GeoPt
 * 
 * https://cloud.google.com/appengine/docs/java/datastore/geosearch
 * @author japi
 *
 */
public interface Location {

	String CATALOG = "Location";

	/**
	 * @return  latitude. Must be between -90 and 90 (inclusive).
	 */
	float getLatitude();

	/**
	 * @return  longitude. Must be between -180 and 180 (inclusive).
	 */
	float getLongitude();

}
