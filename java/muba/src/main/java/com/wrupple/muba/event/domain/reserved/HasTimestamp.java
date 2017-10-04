package com.wrupple.muba.event.domain.reserved;

import java.util.Date;

public interface HasTimestamp {
	
	String FIELD="timestamp";

	Date getTimestamp();
	
	void setTimestamp(Date d);	
	
}
