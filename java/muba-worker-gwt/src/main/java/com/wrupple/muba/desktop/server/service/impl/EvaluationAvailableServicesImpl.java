package com.wrupple.muba.desktop.server.service.impl;

import com.wrupple.muba.desktop.server.service.EvaluationAvailableServices;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class EvaluationAvailableServicesImpl implements EvaluationAvailableServices {

	@Inject
	public EvaluationAvailableServicesImpl() {
	}

	@Override
	public List<Object> list(int size) {
		return new ArrayList<Object>(size);
	}

}
