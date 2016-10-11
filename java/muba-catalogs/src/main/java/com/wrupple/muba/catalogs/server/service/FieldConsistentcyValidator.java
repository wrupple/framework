package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.catalogs.domain.annotations.ConsistentFields;

public interface FieldConsistentcyValidator extends ConstraintValidator<ConsistentFields, List<Long>> {

}
