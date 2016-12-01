package com.wrupple.muba.catalogs.domain.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.wrupple.muba.catalogs.server.service.CatalogActionRequestValidator;

@Documented
@Inherited
@Constraint(validatedBy = CatalogActionRequestValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface ValidCatalogActionRequest {
	Class<?>[] groups() default {};

	String message() default "{catalog.invalidEntry}";

	Class<? extends Payload>[] payload() default {};
}
