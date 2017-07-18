package com.wrupple.muba.bootstrap.server.service.impl;

import java.util.Map;

import javax.inject.Singleton;
import javax.validation.ConstraintValidatorContext;

import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bootstrap.domain.RootServiceManifest;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.annotations.Sentence;
import com.wrupple.muba.bootstrap.server.service.SentenceValidator;
// singleton? or allow validation manager to handle instance pooling
@Singleton
public class SentenceValidatorImpl implements SentenceValidator {

	@Override
	public void initialize(Sentence constraintAnnotation) {

	}

	@Override
	public boolean isValid(RuntimeContext requestContext, ConstraintValidatorContext context) {
		if (requestContext.hasNext()) {
			int currentWord = requestContext.nextIndex();
			// dont move  iterator while validating
			String service = requestContext.getSentence().get(currentWord);
			RootServiceManifest rootService = requestContext.getApplication().getRootService();
			Map<String, ServiceManifest> versions = rootService.getVersions(service);
			if (versions == null) {
				// it could still fallbaack
				if (rootService.getFallbackService() == null) {
					// theres no fallback
					if(rootService.getChildrenValues()==null){
						context.buildConstraintViolationWithTemplate(UNCONFIGURED).addConstraintViolation();
					}
					return false;
				}
			}
		}
		return true;
	}

}
