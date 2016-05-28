package com.wrupple.muba.bpm.domain;

public interface ProcessInstance {

	Number getProcess();
	
	int getStepIndex();
	
	int getStatus();
	
	String getSerializedContext();
}
