package com.wrupple.muba.bootstrap.domain.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.wrupple.muba.bootstrap.server.service.SentenceValidator;

@Documented
@Constraint(validatedBy = SentenceValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Sentence {

    Class<?>[] groups() default {};

    String message() default "{chain.sentence}";

    Class<? extends Payload>[] payload() default {};
    
    
}
