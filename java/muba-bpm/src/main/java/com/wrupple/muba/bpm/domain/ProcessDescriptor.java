package com.wrupple.muba.bpm.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;


public interface ProcessDescriptor extends CatalogEntry{
	String CATALOG = "ProcessDescriptor";

	public List<? extends Object> getProcessSteps();
	
	List<? extends ProcessTaskDescriptor> getProcessStepsValues();
	
}