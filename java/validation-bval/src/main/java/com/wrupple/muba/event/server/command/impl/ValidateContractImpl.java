package com.wrupple.muba.event.server.command.impl;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.chain.command.ValidateContract;
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
public class ValidateContractImpl implements ValidateContract {
    private static final Logger log = LogManager.getLogger(ValidateContractImpl.class);


    private final Validator validator;
    private final Class<?>[] groups;

    @Inject
    public ValidateContractImpl(Validator validator, ValidationGroupProvider a) {
        this.validator = validator;
        this.groups = a == null ? null : a.get();

    }

    @Override
    public boolean execute(Context context) throws Exception {


        RuntimeContext requestContext = (RuntimeContext) context;

        if (requestContext.getServiceContract() == null) {
            log.error("There is no contract object to validate");
        } else {
            Set<ConstraintViolation<?>> violations = (Set) validator.validate(requestContext.getServiceContract(), groups);
            requestContext.setConstraintViolations(violations);
            if (!(violations == null || violations.isEmpty())) {
                log.error("contract violates restrictions");
                if (log.isInfoEnabled()) {
                    for (ConstraintViolation<?> v : violations) {
                        log.info(v.getLeafBean().toString());
                        log.info("\t{} : {}", v.getPropertyPath(), v.getMessage());
                    }
                }

                throw new IllegalArgumentException("Contract violates constrains");
            }


        }




        return CONTINUE_PROCESSING;
    }
}
