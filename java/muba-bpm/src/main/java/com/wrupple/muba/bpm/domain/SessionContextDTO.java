package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.reserved.HasStakeHolder;

public interface SessionContextDTO extends HasStakeHolder {

	String CATALOG = "AbstractEntity";
	
	
	BPMPeer getPeer();
	
	String getUsername();

	String getDomain();
	
}
