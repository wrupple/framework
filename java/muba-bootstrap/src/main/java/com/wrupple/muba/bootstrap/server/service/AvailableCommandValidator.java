package com.wrupple.muba.bootstrap.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.bootstrap.domain.annotations.AvailableCommand;

public interface AvailableCommandValidator extends ConstraintValidator<AvailableCommand, String> {

}
