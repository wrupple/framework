package com.wrupple.vegetate.domain.structure;

import java.util.Date;

public interface HasTimestamp {
	
	String FIELD="timestamp";

	Date getTimestamp();
	
	void setTimestamp(Date d);	
	
}
