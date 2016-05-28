package com.wrupple.muba.bpm.domain;

import java.util.List;

import com.wrupple.muba.catalogs.domain.ContentNode;

public interface DomainRolePermission extends ContentNode {

	String CATALOG  = "DomainRolePermission";
	String SERVICE_FIELD = "service";
	
	List<String> getProperties();
	
	int getService();
	
}