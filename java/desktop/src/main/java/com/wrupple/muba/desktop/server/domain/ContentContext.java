package com.wrupple.muba.desktop.server.domain;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.domain.FilterDataImpl;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;

public class ContentContext extends ContextBase {

	
	public static class PossibleValue {
		private final Object value;
		private double error;
		
		public PossibleValue(Object value,double error) {
			this.value=value;
			this.setError(error);
		}

		public Object getValue() {
			return value;
		}

		public double getError() {
			return error;
		}

		public void setError(double error) {
			if(error<0){
				error=0;
			}/*else if (error>1){
				error=1;
			}*/
			this.error = error;
		}
		
	}
	
	private final CatalogActionContext catalogContext;
	private String task,Process,catalogId,catalogEntryId;
	private CatalogEntry output;
	private FilterDataImpl filter;
	private ProcessTaskDescriptor taskValue;
	private List<PossibleValue> foundValues;
	private Session reflectionSession;
	private FieldDescriptor field;
	private Object fieldValue;

	@Inject
	public ContentContext(CatalogActionContext catalog) {
		super();
		this.catalogContext=catalog;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getCatalogEntryId() {
		return catalogEntryId;
	}

	public void setCatalogEntryId(String catalogEntryId) {
		this.catalogEntryId = catalogEntryId;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getProcess() {
		return Process;
	}

	public void setProcess(String process) {
		Process = process;
	}



	private static final long serialVersionUID = -4640944159445062405L;


	public CatalogEntry getOutput() {
		return output;
	}

	public void setOutput(CatalogEntry output) {
		this.output = output;
	}

	public FilterDataImpl getFilter() {
		return filter;
	}

	public void setFilter(FilterDataImpl filter) {
		this.filter = filter;
	}

	public List<PossibleValue> getFoundValues() {
		return foundValues;
	}

	public void setFoundValues(List<PossibleValue> foundValues) {
		this.foundValues = foundValues;
	}


	public Session assertReflectionSession(CatalogEvaluationDelegate access) {
		if(reflectionSession==null){
			reflectionSession = access.newSession(output);
		}
		return reflectionSession;
	}

	public void setField(FieldDescriptor fieldDescriptor) {
		this.field=fieldDescriptor;
	}

	public FieldDescriptor getField() {
		return field;
	}

	public void setFieldValue(Object value) {
		this.fieldValue=value;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public CatalogActionContext getCatalogContext() {
		return catalogContext;
	}

	public ProcessTaskDescriptor getTaskValue() {
		return taskValue;
	}

	public void setTaskValue(ProcessTaskDescriptor taskValue) {
		this.taskValue = taskValue;
	}

}
