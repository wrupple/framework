package com.wrupple.muba.worker.server.service;

import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.Operation;
import com.wrupple.muba.worker.domain.ApplicationContext;

import java.util.List;
import java.util.function.BinaryOperator;

public interface VariableConsensus extends BinaryOperator<Runner> {
    void modelOperation(List<Runner> runners, Operation result, ApplicationContext context, Instrospection intros);
}
