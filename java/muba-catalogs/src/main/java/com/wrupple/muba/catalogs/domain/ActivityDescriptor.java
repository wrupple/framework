package com.wrupple.muba.catalogs.domain;

import java.util.List;

public interface ActivityDescriptor  {
	
	public String getActivity();
	
	public String getActivityPresenter();

	public List<String> getProperties();
	
	public String getOutputHandler();
	
	public String getRequiredRole();

}
