package com.wrupple.muba.bootstrap.server.service;

import java.lang.reflect.Type;

import javax.validation.ConstraintValidatorContext;

import com.wrupple.muba.bootstrap.domain.reserved.HasDistinguishedName;

public interface PropertyValidationContext extends HasDistinguishedName{

	Object get(Object arg0);

	ConstraintValidatorContext getValidationContext();

	Type getJavaType();


}
