package com.wrupple.muba.catalogs.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.annotations.ValidCatalogActionRequest;

public interface CatalogActionRequestValidator extends ConstraintValidator<ValidCatalogActionRequest, CatalogActionRequest> {

}
