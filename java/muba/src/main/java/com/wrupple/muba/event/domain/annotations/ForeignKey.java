package com.wrupple.muba.event.domain.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.wrupple.muba.event.server.service.KeyDomainValidator;

@Documented
@Inherited
@Constraint( validatedBy = KeyDomainValidator.class )
@Target( { FIELD} )
@Retention( RUNTIME )
public @interface ForeignKey {
	
	String foreignCatalog();
	
	boolean unique() default true;

    Class<?>[] groups() default {};

    String message() default "{catalog.foreignKey}";

    Class<? extends Payload>[] payload() default {};
}
