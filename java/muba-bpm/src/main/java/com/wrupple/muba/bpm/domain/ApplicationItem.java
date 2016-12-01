package com.wrupple.muba.bpm.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.HasChildrenValues;

public interface ApplicationItem extends ActivityDescriptor,CatalogEntry,HasChildrenValues<Long,ApplicationItem>{
	
	public  final String CATALOG = "DesktopPlace";

	public List<Long> getRequiredScripts();
	
	public List<Long> getRequiredStyleSheets();
	
	public String getApplicationDomain();
	
	public void appendChild(int index,ApplicationItem item);
	
	public void appendChild(ApplicationItem item);
	
	public void appendChildren(List<ApplicationItem> newChildren);

	public void setId(Long l);

	public void setStaticImageUrl(String string);

	public void setProperties(List<String> asList);

	public String getDescription();

	public  String getHost();

}