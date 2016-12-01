package com.wrupple.muba.bpm.server.domain;

import com.wrupple.muba.bpm.domain.HumanTaskData;

public class HumanTaskDataImpl implements HumanTaskData {

	private String task,Process,catalog;
	private Object entry;

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

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public Object getEntry() {
		return entry;
	}

	public void setEntry(Object entry) {
		this.entry = entry;
	}


	
}
