package com.wrupple.muba.catalogs.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldValues;

public interface CatalogNormalizationConstraintValidator extends ConstraintValidator<CatalogFieldValues, Object> {

}
