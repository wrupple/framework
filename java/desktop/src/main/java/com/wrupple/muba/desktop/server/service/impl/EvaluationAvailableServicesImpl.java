package com.wrupple.muba.desktop.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.wrupple.muba.desktop.server.service.EvaluationAvailableServices;

public class EvaluationAvailableServicesImpl implements EvaluationAvailableServices {

	@Inject
	public EvaluationAvailableServicesImpl() {
	}

	@Override
	public List<Object> list(int size) {
		return new ArrayList<Object>(size);
	}

}
