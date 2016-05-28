package com.wrupple.vegetate.server.domain.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CatalogField {

	boolean ephemeral() default false;

	boolean ignore() default false;

	boolean sortable() default false;

	boolean filterable() default false;

	boolean createable() default true;

	boolean writeable() default true;

	boolean detailable() default true;

	boolean summary() default true;

	boolean localized() default false;
}
