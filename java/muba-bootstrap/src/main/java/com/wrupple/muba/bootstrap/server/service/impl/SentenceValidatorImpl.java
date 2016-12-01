package com.wrupple.muba.bootstrap.server.service.impl;

import java.util.Map;

import javax.validation.ConstraintValidatorContext;

import com.wrupple.muba.bootstrap.domain.Bootstrap;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.annotations.Sentence;
import com.wrupple.muba.bootstrap.server.service.SentenceValidator;
//TODO singleton? or allow validation manager to handle instance pooling
public class SentenceValidatorImpl implements SentenceValidator {

	@Override
	public void initialize(Sentence constraintAnnotation) {

	}

	@Override
	public boolean isValid(ExcecutionContext requestContext, ConstraintValidatorContext context) {
		Bootstrap rootService = requestContext.getApplication().getRootService();
		if (rootService == null) {
			throw new IllegalStateException("No root service has been configured");
		}
		if (requestContext.hasNext()) {
			int currentWord = requestContext.nextIndex();
			String service = requestContext.getSentence()[currentWord];
			Map<String, ServiceManifest> versions = rootService.getVersions(service);
			if (versions == null) {
				// it could still fallbaack
				if (rootService.getFallbackService() == null) {
					// theres no fallback
					return false;
				}
			}
		}
		return true;
	}

}
