package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.services.presentation.impl.AbstractProblemPresenterChain;
import com.wrupple.muba.worker.client.activity.process.state.impl.CommitEditTransaction;
import com.wrupple.muba.worker.client.activity.process.state.impl.EditingState;
import com.wrupple.muba.worker.client.activity.process.state.impl.StartEditTransaction;

import javax.inject.Provider;


public class EditProblemPresenterChain extends AbstractProblemPresenterChain {

	@Inject
    public EditProblemPresenterChain(
            Provider<StartEditTransaction> startProvider,
			Provider<CommitEditTransaction> commitProvider,
			Provider<EditingState> interactionProvider) {
		super(startProvider, commitProvider, interactionProvider);
	}

}
