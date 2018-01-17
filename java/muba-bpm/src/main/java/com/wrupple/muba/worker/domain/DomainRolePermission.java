package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.ContentNode;
import com.wrupple.muba.event.domain.reserved.HasProperties;

public interface DomainRolePermission extends ContentNode,HasProperties{

	String CATALOG  = "DomainRolePermission";
	String SERVICE_FIELD = "service";
	
	
	int getService();
	
}