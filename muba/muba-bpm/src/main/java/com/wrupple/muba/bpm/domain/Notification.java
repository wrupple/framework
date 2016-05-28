package com.wrupple.muba.bpm.domain;

import java.util.Date;

import com.wrupple.muba.catalogs.domain.Location;
import com.wrupple.vegetate.domain.CatalogEntry;


/**
 * @author japi
 *
 */
public interface Notification extends ProcessControlNode{

	String CATALOG = "Notification";
	
	
	CatalogEntry getSourceDataValue();
	
	/**
	 * @return usually an organization or personId
	 */
	CatalogEntry getSourceValue();
	/**
	 * @return person id? 
	 */
	public Long getSource();
	/**
	 * @return the personId or the ROLE
	 */
	public Long getTargetDiscriminator();
	
	/**
	 * @return ApplicationItem to use as handler.
	 */
	Long getHandler();

	public Date getHandled();
	
	Location getHandledLocationValue();
	

}
