package com.wrupple.muba.cms.server.domain;

import com.wrupple.muba.cms.domain.HumanTaskData;

public class HumanTaskDataImpl implements HumanTaskData {

	private String task,Process,catalogId,catalogEntryId;

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
	
}
