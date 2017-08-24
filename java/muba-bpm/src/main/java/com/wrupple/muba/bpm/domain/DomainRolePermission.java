package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.reserved.HasProperties;
import com.wrupple.muba.catalogs.domain.ContentNode;

public interface DomainRolePermission extends ContentNode ,HasProperties{

	String CATALOG  = "DomainRolePermission";
	String SERVICE_FIELD = "service";
	
	
	int getService();
	
}