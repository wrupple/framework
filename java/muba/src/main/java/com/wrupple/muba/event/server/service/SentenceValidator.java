package com.wrupple.muba.event.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.annotations.Sentence;

public interface SentenceValidator extends ConstraintValidator<Sentence, RuntimeContext> {
	final String UNCONFIGURED = "{chain.empty}";
}
