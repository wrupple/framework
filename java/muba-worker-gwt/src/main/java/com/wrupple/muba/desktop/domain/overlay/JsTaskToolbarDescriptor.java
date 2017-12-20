package com.wrupple.muba.desktop.domain.overlay;

import com.wrupple.muba.cms.domain.TaskToolbarDescriptor;

@SuppressWarnings("serial")
public final class JsTaskToolbarDescriptor extends JsCatalogKey
		implements TaskToolbarDescriptor {

	protected JsTaskToolbarDescriptor() {
		super();
	}

	@Override
	public native String getType() /*-{
		return this.type;
	}-*/;


	@Override
	public Long getTask() {
		String task = getTaskAsString();
		return JsCatalogKey.parseKeyField(task);
	}
	
	
	public native String getTaskAsString() /*-{
		if(this.task==null){
			return null;
		}
		return this.task;
	}-*/;

	public native JsProcessTaskDescriptor getTaskValue() /*-{
		return this.taskValue;
	}-*/;

	

}