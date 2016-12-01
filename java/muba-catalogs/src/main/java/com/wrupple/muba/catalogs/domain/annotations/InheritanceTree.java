package com.wrupple.muba.catalogs.domain.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.wrupple.muba.catalogs.server.service.CatalogInheritanceValidator;

@Documented
@Inherited
@Constraint( validatedBy = CatalogInheritanceValidator.class )
@Target( { FIELD} )
@Retention( RUNTIME )
public @interface InheritanceTree {
	
    Class<?>[] groups() default {};

    String message() default "{catalog.inheritanceTree}";

    Class<? extends Payload>[] payload() default {};
}
