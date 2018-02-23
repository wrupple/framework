package com.wrupple.muba.event.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.annotations.ValidCatalogActionRequest;

public interface CatalogActionRequestValidator extends ConstraintValidator<ValidCatalogActionRequest, CatalogActionRequest> {

}
