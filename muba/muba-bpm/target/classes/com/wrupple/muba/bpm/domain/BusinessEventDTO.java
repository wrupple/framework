package com.wrupple.muba.bpm.domain;


public interface BusinessEventDTO {
	
	//int STRATEGY_CLEAN_ALL=0;
	//int STRATEGY_REPLACE=1;
	
	//APPEND START? APPEND END?
	
	String getTimestamp();
	
	String getCatalog();
	
	String getEntryId();
	
	public Object getEntry();

	String getName();
	
	public String getDomain();
	
	public String getHost();
}
