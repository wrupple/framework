package com.wrupple.muba.event.domain.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.wrupple.muba.event.server.service.CatalogNormalizationConstraintValidator;

@Documented
@Target(ElementType.FIELD)
@Constraint( validatedBy = CatalogNormalizationConstraintValidator.class )
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CatalogFieldValues {

	String[] defaultValueOptions();
	
    Class<?>[] groups() default {};

    String message() default "{catalog.unrecognizedValue}";

    Class<? extends Payload>[] payload() default {};
}
