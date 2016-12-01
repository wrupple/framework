package com.wrupple.muba.bpm.domain.impl;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntryImpl;
import com.wrupple.muba.bpm.domain.ProcessDescriptor;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.catalogs.domain.annotations.CatalogKey;
import com.wrupple.muba.catalogs.domain.annotations.CatalogValue;

public class ProcessDescriptorImpl extends CatalogEntryImpl implements ProcessDescriptor {

	private static final long serialVersionUID = 6412202198956066554L;
	@CatalogKey(foreignCatalog=ProcessTaskDescriptor.CATALOG)
	private List<Long> processSteps;
	@CatalogValue(foreignCatalog=ProcessTaskDescriptor.CATALOG)
	private List<ProcessTaskDescriptor> processStepsValues;
	private String catalog,outputCatalog;

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getOutputCatalog() {
		return outputCatalog;
	}

	public void setOutputCatalog(String outputCatalog) {
		this.outputCatalog = outputCatalog;
	}

	@Override
	public String getCatalogType() {
		return ProcessDescriptor.CATALOG;
	}

	@Override
	public List<Long> getProcessSteps() {
		return processSteps;
	}

	@Override
	public List<? extends ProcessTaskDescriptor> getProcessStepsValues() {
		return processStepsValues;
	}

	public void setProcessSteps(List<Long> processSteps) {
		this.processSteps = processSteps;
	}

	public void setProcessStepsValues(List<ProcessTaskDescriptor> processStepsValues) {
		this.processStepsValues = processStepsValues;
	}

}
