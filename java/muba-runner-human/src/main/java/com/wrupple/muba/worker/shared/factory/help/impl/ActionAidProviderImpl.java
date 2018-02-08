package com.wrupple.muba.worker.shared.factory.help.impl;

import com.google.inject.Inject;
import com.wrupple.muba.cms.domain.WruppleActivityAction;
import com.wrupple.muba.worker.shared.factory.dictionary.ServiceMap;
import com.wrupple.muba.worker.shared.factory.help.ActionAidProvider;

public class ActionAidProviderImpl extends ValueDependableConfigurationAdvisor implements ActionAidProvider {

	@Inject
	public ActionAidProviderImpl(ServiceMap valueMap) {
		super(valueMap, WruppleActivityAction.COMMAND_FIELD);
	}

}
