package com.wrupple.muba;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.wrupple.muba.event.server.service.AvailableCommandValidator;
import com.wrupple.muba.event.server.service.ContextAwareValidator;
import com.wrupple.muba.event.server.service.ValidationGroupProvider;
import com.wrupple.muba.event.server.service.impl.AvailableCommandValidatorImpl;
import com.wrupple.muba.event.server.service.impl.ConstraintValidatorFactoryImpl;
import com.wrupple.muba.event.server.service.impl.ContextAwareValidatorImpl;
import com.wrupple.muba.event.server.service.impl.DefaultValidationGroupProvider;
import org.apache.bval.jsr.ApacheValidationProvider;
import org.apache.bval.jsr.ApacheValidatorConfiguration;
import org.apache.bval.jsr.ApacheValidatorFactory;

import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.Validator;
import javax.validation.spi.ValidationProvider;
import java.util.ArrayList;
import java.util.List;

public class ValidationModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ValidationGroupProvider.class).to(DefaultValidationGroupProvider.class);
		bind(ConstraintValidatorFactory.class).to(ConstraintValidatorFactoryImpl.class);
		bind(AvailableCommandValidator.class).to(AvailableCommandValidatorImpl.class);
		bind(ContextAwareValidator.class).to(ContextAwareValidatorImpl.class);
	}

	@Provides
	@Singleton
	@Inject
	public ApacheValidatorFactory validationFactoryProvider(ConstraintValidatorFactory constraintValidatorFactory) {
		ValidationProviderResolver resolver = new ValidationProviderResolver() {

			public List<ValidationProvider<?>> getValidationProviders() {
				List<ValidationProvider<?>> list = new ArrayList<ValidationProvider<?>>(1);
				list.add(new ApacheValidationProvider());
				return list;
			}
		};

		ApacheValidatorConfiguration builder = Validation.byProvider(ApacheValidationProvider.class)
				.providerResolver(resolver).configure();
		ApacheValidatorFactory factory = (ApacheValidatorFactory) builder.buildValidatorFactory();
		factory.setConstraintValidatorFactory(constraintValidatorFactory);
		return factory;
	}

	@Provides
	@Singleton
	@Inject
	public Validator validationFactoryProvider(ApacheValidatorFactory factory) {
		return factory.getValidator();
	}
}
