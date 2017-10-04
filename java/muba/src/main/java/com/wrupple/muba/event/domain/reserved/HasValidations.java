package com.wrupple.muba.event.domain.reserved;

import java.util.Collection;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * validations are expressed as constrant violatios
 * @author japi
 *
 */
public interface HasValidations {
	Set<ConstraintViolation<?>> getConstraintViolations();
	void addConstraintViolation(ConstraintViolation<?> violation);
	void addAllConstraintViolation(Collection<ConstraintViolation<?>> violations);
	void addWarning(String string);
	void setConstraintViolations(Set<ConstraintViolation<?>> aggregate);
}
