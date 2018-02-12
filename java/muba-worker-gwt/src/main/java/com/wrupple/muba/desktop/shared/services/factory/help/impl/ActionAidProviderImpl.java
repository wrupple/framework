package com.wrupple.muba.desktop.shared.services.factory.help.impl;

import com.google.inject.Inject;
import com.wrupple.muba.cms.domain.WruppleActivityAction;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.ServiceMap;
import com.wrupple.muba.desktop.shared.services.factory.help.ActionAidProvider;

public class ActionAidProviderImpl extends ValueDependableConfigurationAdvisor implements ActionAidProvider {

	@Inject
	public ActionAidProviderImpl(ServiceMap valueMap) {
		super(valueMap, WruppleActivityAction.COMMAND_FIELD);
	}

}
