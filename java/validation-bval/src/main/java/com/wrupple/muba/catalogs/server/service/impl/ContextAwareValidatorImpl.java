package com.wrupple.muba.catalogs.server.service.impl;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Constraint;
import javax.validation.ConstraintValidatorContext;

import org.apache.bval.jsr.AnnotationProcessor;
import org.apache.bval.jsr.ApacheValidatorFactory;
import org.apache.bval.jsr.AppendValidationToMeta;
import org.apache.bval.jsr.ConstraintValidatorContextImpl;
import org.apache.bval.jsr.GroupValidationContext;
import org.apache.bval.model.MetaBean;
import org.apache.bval.model.MetaProperty;
import org.apache.bval.model.Validation;
import org.apache.bval.util.AccessStrategy;

import com.wrupple.muba.bootstrap.server.service.ContextAwareValidator;
import com.wrupple.muba.bootstrap.server.service.PropertyValidationContext;

@Singleton
public class ContextAwareValidatorImpl implements ContextAwareValidator {

	private final AnnotationProcessor annotationProcessor;

	class CatalogContextFieldAccessStrategy extends AccessStrategy {

		private final PropertyValidationContext delegate;

		public CatalogContextFieldAccessStrategy( PropertyValidationContext delegate) {
			super();
			this.delegate = delegate;
		}

		@Override
		public Object get(Object arg0) {
			return this.delegate.get(arg0);
		}

		@Override
		public String getPropertyName() {
			return this.delegate.getDistinguishedName();
		}

		@Override
		public ElementType getElementType() {
			return ElementType.FIELD;
		}

		@Override
		public Type getJavaType() {
			return delegate.getJavaType();
		}

	}

	@Inject
	public ContextAwareValidatorImpl(ApacheValidatorFactory factory) {
		super();
		this.annotationProcessor = new AnnotationProcessor(factory);
	}

	@Override
	public boolean processAnnotationInContext(Annotation[] annotations, Class<?> owner, PropertyValidationContext fieldDelegate) throws ReflectiveOperationException {
		ConstraintValidatorContext vx = fieldDelegate.getValidationContext();

		ConstraintValidatorContextImpl validationContext = vx.unwrap(ConstraintValidatorContextImpl.class);
		GroupValidationContext<?> groupContext = validationContext.getValidationContext();
		MetaBean arg = groupContext.getMetaBean();

		AccessStrategy access = new CatalogContextFieldAccessStrategy(fieldDelegate);
		// append property to main meta bean
		MetaProperty prop = addMetaProperty(arg, access);
		Constraint vcAnno;
		for (Annotation annotation : annotations) {
			// vcAnno = points to actial validator class
			// (UpperCaseValidator.class)
			vcAnno = annotation.annotationType().getAnnotation(Constraint.class);

			if (vcAnno != null) {
					annotationProcessor.processAnnotation(annotation, prop, owner, access,
							new AppendValidationToMeta(prop), false);
			}
		}
		int before = groupContext.getListener().violationsSize();
		for (Validation validation : prop.getValidations()) {
			
			validation.validate(groupContext);
		}
		
		return before == groupContext.getListener().violationsSize();
	}


	private MetaProperty addMetaProperty(MetaBean parentMetaBean, AccessStrategy access) {
		final MetaProperty result = new MetaProperty();
		final String name = access.getPropertyName();
		result.setName(name);
		result.setType(access.getJavaType());
		parentMetaBean.putProperty(name, result);
		return result;
	}

}
