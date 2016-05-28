package com.wrupple.muba.catalogs.server.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.wrupple.muba.catalogs.server.service.impl.reCAPTCHAValidator;


@Documented
@Constraint( validatedBy = reCAPTCHAValidator.class )
@Target( { FIELD, ANNOTATION_TYPE, PARAMETER } )
@Retention( RUNTIME )
public @interface CAPTCHA {
	

    Class<?>[] groups() default {};

    String message() default "{captcha.message}";

    Class<? extends Payload>[] payload() default {};

}
