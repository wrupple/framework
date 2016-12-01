package com.wrupple.muba.bootstrap.server.service;

import java.lang.annotation.Annotation;

public interface ContextAwareValidator {

	/**
	 * @param annotations
	 * @param owner
	 * @param fieldDelegate
	 * @return violations found
	 * @throws ReflectiveOperationException
	 */
	boolean processAnnotationInContext(Annotation[] annotations, Class<?> owner, PropertyValidationContext fieldDelegate) throws ReflectiveOperationException;

}
