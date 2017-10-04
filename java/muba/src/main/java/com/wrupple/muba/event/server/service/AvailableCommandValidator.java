package com.wrupple.muba.event.server.service;

import javax.validation.ConstraintValidator;

import com.wrupple.muba.event.domain.annotations.AvailableCommand;

public interface AvailableCommandValidator extends ConstraintValidator<AvailableCommand, String> {

}
