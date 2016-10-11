package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.HasProperties;

public interface ActivityDescriptor  extends HasProperties {
	
	public String getActivity();
	
	public String getActivityPresenter();

	
	public String getOutputHandler();
	
	public String getRequiredRole();

}
