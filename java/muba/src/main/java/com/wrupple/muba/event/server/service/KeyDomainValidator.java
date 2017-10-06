package com.wrupple.muba.event.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.event.domain.annotations.ForeignKey;

import java.lang.annotation.Annotation;

public interface KeyDomainValidator extends ConstraintValidator<Annotation, Object> {

}
