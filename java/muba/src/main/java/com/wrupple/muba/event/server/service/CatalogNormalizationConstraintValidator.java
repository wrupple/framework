package com.wrupple.muba.event.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.event.domain.annotations.CatalogFieldValues;

public interface CatalogNormalizationConstraintValidator extends ConstraintValidator<CatalogFieldValues, Object> {

}
