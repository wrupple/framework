package com.wrupple.muba.event.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.event.domain.annotations.CatalogKey;

public interface KeyDomainValidator extends ConstraintValidator<CatalogKey, Object> {

}
