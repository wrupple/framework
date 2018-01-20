package com.wrupple.muba.desktop.client.factory.help;

import com.wrupple.muba.desktop.domain.PropertyValueAvisor;
import com.wrupple.muba.worker.domain.ApplicationContext;

import java.util.List;

public interface SolverConcensor {

    void conferWithRunners(ApplicationContext currentState, List<PropertyValueAvisor> advice);

	/**
	 * @param fieldId
	 * @param value
	 * @param violations TODO
	 */
    void intersectConstraintsWithSolution(String fieldId, Object value, List<String> violations);

}
