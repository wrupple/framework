package com.wrupple.muba.desktop.client.factory.help.impl;

import com.google.inject.Inject;
import com.wrupple.muba.cms.domain.TaskToolbarDescriptor;
import com.wrupple.muba.desktop.client.factory.dictionary.ToolbarMap;
import com.wrupple.muba.desktop.client.factory.help.TaskToolbarAid;

public class TaskToolbarAidImpl extends ValueDependableConfigurationAdvisor implements TaskToolbarAid {

	@Inject
	public TaskToolbarAidImpl(ToolbarMap tollbars) {
		super(tollbars, TaskToolbarDescriptor.TYPE_FIELD);
	}

}
