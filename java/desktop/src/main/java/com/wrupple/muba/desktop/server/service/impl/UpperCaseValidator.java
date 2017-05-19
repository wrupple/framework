package com.wrupple.muba.desktop.server.service.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UpperCaseValidator implements ConstraintValidator<UpperCase, String> {

	public void initialize(UpperCase constraintAnnotation) {
		// constraintAnnotation.payload();
	}

	public boolean isValid(String object, ConstraintValidatorContext constraintContext) {

		System.out.println("Validating is uppercase: " + object);
		if (object == null)
			return true;

		return object.equals(object.toUpperCase());
	}

}