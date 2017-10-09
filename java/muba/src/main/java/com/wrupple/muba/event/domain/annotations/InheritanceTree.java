package com.wrupple.muba.event.domain.annotations;

import com.wrupple.muba.event.server.service.CatalogInheritanceValidator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


@Documented
@Inherited
@Constraint( validatedBy = CatalogInheritanceValidator.class )
@Target( { FIELD} )
@Retention( RUNTIME )
public @interface InheritanceTree {
	
    Class<?>[] groups() default {};

    String message() default "{catalog.inheritanceTree}";

    Class<? extends Payload>[] payload() default {};

    String catalog();
}
