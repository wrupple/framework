package com.wrupple.vegetate.server.services;

import javax.validation.ConstraintValidator;

import com.wrupple.vegetate.server.domain.annotations.CatalogFieldValues;

public interface CatalogNormalizationConstraintValidator extends ConstraintValidator<CatalogFieldValues, Object> {

}
