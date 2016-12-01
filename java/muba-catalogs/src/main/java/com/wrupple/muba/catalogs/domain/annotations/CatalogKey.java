package com.wrupple.muba.catalogs.domain.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.wrupple.muba.catalogs.server.service.CatalogKeyConstraintValidator;

@Documented
@Inherited
@Constraint( validatedBy = CatalogKeyConstraintValidator.class )
@Target( { FIELD} )
@Retention( RUNTIME )
public @interface CatalogKey {
	
	String foreignCatalog();
	
	boolean unique() default true;

    Class<?>[] groups() default {};

    String message() default "{catalog.foreignKey}";

    Class<? extends Payload>[] payload() default {};
}
