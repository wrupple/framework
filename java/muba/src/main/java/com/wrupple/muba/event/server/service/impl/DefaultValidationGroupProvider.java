package com.wrupple.muba.event.server.service.impl;

import javax.inject.Singleton;

import com.wrupple.muba.event.server.service.ValidationGroupProvider;
@Singleton
public class DefaultValidationGroupProvider implements ValidationGroupProvider {
	Class[] groups = new Class<?>[] { javax.validation.groups.Default.class };
	@Override
	public Class[] get() {
		return groups;
	}

}
