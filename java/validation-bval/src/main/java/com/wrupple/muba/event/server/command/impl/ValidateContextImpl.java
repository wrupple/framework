package com.wrupple.muba.event.server.command.impl;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.chain.command.ValidateContext;
import com.wrupple.muba.event.server.service.ValidationGroupProvider;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Singleton
public class ValidateContextImpl implements ValidateContext {

    private static final Logger log = LogManager.getLogger(ValidateContextImpl.class);


    private final Validator validator;
    private final Class<?>[] groups;

    @Inject
    public ValidateContextImpl(Validator validator, ValidationGroupProvider a) {
        this.validator = validator;
        this.groups = a == null ? null : a.get();

    }

    @Override
    public boolean execute(Context context) throws Exception {


        RuntimeContext requestContext = (RuntimeContext) context;

        Set<ConstraintViolation<?>> violations = (Set) validator.validate(requestContext, groups);
        requestContext.setConstraintViolations(violations);
        if (!(violations == null || violations.isEmpty())) {
            log.error("excecution request encountered constraint violations ");
            //if (log.isWarnEnabled()) {
            for (ConstraintViolation<?> v : violations) {
                log.warn("{}", v.getMessage());
            }
            //}
            throw new IllegalArgumentException();
        }


        return CONTINUE_PROCESSING;
    }
}
