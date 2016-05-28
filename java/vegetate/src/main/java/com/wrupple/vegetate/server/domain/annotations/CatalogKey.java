package com.wrupple.vegetate.server.domain.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.wrupple.vegetate.server.services.CatalogKeyConstraintValidator;

@Documented
@Inherited
@Constraint( validatedBy = CatalogKeyConstraintValidator.class )
@Target( { FIELD} )
@Retention( RUNTIME )
public @interface CatalogKey {
	
	String foreignCatalog();

    Class<?>[] groups() default {};

    String message() default "{catalog.foreignKey}";

    Class<? extends Payload>[] payload() default {};
}
