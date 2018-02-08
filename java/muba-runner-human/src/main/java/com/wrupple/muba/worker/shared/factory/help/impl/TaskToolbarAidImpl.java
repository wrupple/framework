package com.wrupple.muba.worker.shared.factory.help.impl;

import com.google.inject.Inject;
import com.wrupple.muba.cms.domain.TaskToolbarDescriptor;
import com.wrupple.muba.worker.shared.factory.dictionary.ToolbarMap;
import com.wrupple.muba.worker.shared.factory.help.TaskToolbarAid;

public class TaskToolbarAidImpl extends ValueDependableConfigurationAdvisor implements TaskToolbarAid {

	@Inject
	public TaskToolbarAidImpl(ToolbarMap tollbars) {
		super(tollbars, TaskToolbarDescriptor.TYPE_FIELD);
	}

}
