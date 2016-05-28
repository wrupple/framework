package com.wrupple.muba.bpm.server.domain;

import javax.inject.Inject;

import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.muba.bpm.domain.HumanActivityTracking;
import com.wrupple.muba.bpm.domain.ProcessDescriptor;
import com.wrupple.muba.cms.server.domain.ContentContext;
import com.wrupple.vegetate.domain.FilterData;

public class ProcessorContext extends ContextBase implements HumanActivityTracking
{
	private static final long serialVersionUID = -2368369144248700364L;
	private  String exitActivity;
	private final ContentContext contentContext;
	private FilterData filterData;
	private ProcessDescriptor processValue;

	@Inject
	public ProcessorContext(ContentContext contentContext) {
		super();
		this.contentContext = contentContext;
	}

	public ContentContext getContentContext() {
		return contentContext;
	}

	@Override
	public String getExitActivity() {
		return exitActivity;
	}

	public FilterData getFilterData() {
		return filterData;
	}

	public void setFilterData(FilterData filterData) {
		this.filterData = filterData;
	}

	public void setExitActivity(String exitActivity) {
		this.exitActivity = exitActivity;
	}

	public ProcessDescriptor getProcessValue() {
		return processValue;
	}

	public void setProcessValue(ProcessDescriptor processValue) {
		this.processValue = processValue;
	}




}
