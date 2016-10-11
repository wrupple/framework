package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import com.google.inject.Injector;

/**
 * A {@link ConstraintValidatorFactory} that relies on guice for creating
 * validators.
 *
 * @author japi
 */
@Singleton
public class ConstraintValidatorFactoryImpl implements ConstraintValidatorFactory {

	private final Injector injector;

	@Inject
	public ConstraintValidatorFactoryImpl(final Injector injector) {
		this.injector = injector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(final Class<T> key) {
		return injector.getInstance(key);
	}

	@Override
	public void releaseInstance(ConstraintValidator<?, ?> instance) {
		
	}

}
