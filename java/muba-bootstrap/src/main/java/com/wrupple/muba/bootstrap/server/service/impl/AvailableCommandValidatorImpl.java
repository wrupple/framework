package com.wrupple.muba.bootstrap.server.service.impl;

import javax.inject.Inject;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;

import com.google.inject.Injector;
import com.wrupple.muba.bootstrap.domain.annotations.AvailableCommand;
import com.wrupple.muba.bootstrap.server.service.AvailableCommandValidator;

public class AvailableCommandValidatorImpl implements AvailableCommandValidator {

	private final Injector injector;
	Catalog catalog;
	@Inject
	public AvailableCommandValidatorImpl(final Injector injector) {
		this.injector = injector;
	}
	
	
	
	@Override
	public void initialize(AvailableCommand constraintAnnotation) {
		this.catalog=injector.getInstance(CatalogFactory.class).getCatalog(constraintAnnotation.dictionary());
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value==null||catalog!=null&&catalog.getCommand(value)!=null;
	}

}
