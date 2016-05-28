package com.wrupple.muba.cms.server.domain;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.server.domain.FilterDataImpl;

public class ContentContext extends ContextBase {

	
	public static class FoundValue {
		private final Object value;
		private double error;
		
		public FoundValue(Object value,double error) {
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
	
	private final CatalogExcecutionContext catalogContext;
	private String task,Process,catalogId,catalogEntryId;
	private CatalogEntry output;
	private FilterDataImpl filter;
	private ProcessTaskDescriptor taskValue;
	private List<FoundValue> foundValues;
	private Session reflectionSession;
	private FieldDescriptor field;
	private Object fieldValue;

	@Inject
	public ContentContext(CatalogExcecutionContext catalog) {
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

	public List<FoundValue> getFoundValues() {
		return foundValues;
	}

	public void setFoundValues(List<FoundValue> foundValues) {
		this.foundValues = foundValues;
	}


	public Session assertReflectionSession(CatalogPropertyAccesor access) {
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

	public CatalogExcecutionContext getCatalogContext() {
		return catalogContext;
	}

	public ProcessTaskDescriptor getTaskValue() {
		return taskValue;
	}

	public void setTaskValue(ProcessTaskDescriptor taskValue) {
		this.taskValue = taskValue;
	}

}
