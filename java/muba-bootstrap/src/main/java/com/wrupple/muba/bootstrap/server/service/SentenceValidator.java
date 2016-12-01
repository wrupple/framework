package com.wrupple.muba.bootstrap.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.annotations.Sentence;

public interface SentenceValidator extends ConstraintValidator<Sentence, ExcecutionContext> {

}
