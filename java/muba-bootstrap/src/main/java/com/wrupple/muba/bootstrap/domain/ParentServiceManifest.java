package com.wrupple.muba.bootstrap.domain;

import java.util.List;

public interface ParentServiceManifest extends ServiceManifest {

	String[] getChildServiceIds();
	
	List<ServiceManifest> getChildServiceManifests();

}
