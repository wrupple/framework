package com.wrupple.muba.bootstrap.domain.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.wrupple.muba.bootstrap.server.service.AvailableCommandValidator;

@Documented
@Constraint(validatedBy = AvailableCommandValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface AvailableCommand {

    Class<?>[] groups() default {};

    String message() default "{chain.command.404}";

    Class<? extends Payload>[] payload() default {};

    String dictionary();
}
