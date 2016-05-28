package com.wrupple.vegetate.server.services;

import java.util.List;

import javax.validation.ConstraintValidator;

import com.wrupple.vegetate.server.domain.annotations.ConsistentFields;

public interface FieldConsistentcyValidator extends ConstraintValidator<ConsistentFields, List<Long>> {

}
