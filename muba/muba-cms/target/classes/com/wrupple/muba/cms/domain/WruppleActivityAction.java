package com.wrupple.muba.cms.domain;

import java.util.List;

public interface WruppleActivityAction {
	
	String CATALOG = "WruppleActivityAction";
	String COMMAND_FIELD = "command";

	String getName();
	
	String getDescription();
	
	String getImage();
	
	String getCommand();
	
	List<String> getProperties();
	
}
