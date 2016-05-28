package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.structure.HasChildrenValues;

public interface ApplicationItem extends ActivityDescriptor,CatalogEntry,HasChildrenValues<Long,ApplicationItem>{
	
	public  final String CATALOG = "DesktopPlace";

	public Number getProcess();
	
	public List<Long> getRequiredScripts();
	
	public List<Long> getRequiredStyleSheets();
	
	public boolean isHijackDesktop();
	
	public String getApplicationDomain();
	
	public void appendChild(int index,ApplicationItem item);
	
	public void appendChild(ApplicationItem item);
	
	public void appendChildren(List<ApplicationItem> newChildren);

	public void setId(Long l);

	public void setActivity(String act);

	public void setStaticImageUrl(String string);

	public void setOutputHandler(String string);

	public void setOverrideUserDomain(boolean b);

	public void setRequiredRole(String domainMasterRole);

	public void setProperties(List<String> asList);

	public String getDescription();

	public  String getHost();

}