package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.ImplicitIntent;
import com.wrupple.muba.bootstrap.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.bootstrap.domain.reserved.HasProperties;

public interface ActivityDescriptor  extends HasProperties,HasDistinguishedName,ImplicitIntent {
	

	public Object getProcess();
	
	public ProcessDescriptor getProcessValue();
	
	public String getOutputHandler();
	
	public void setOutputHandler(String string);

}
