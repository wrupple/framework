package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.inject.Inject;
import com.wrupple.muba.worker.server.chain.command.impl.AssembleCreatorImpl;

import javax.inject.Provider;

public class CreateProblemPresenterImpl implements ProblemPresenter {
    @Inject
    public CreateProblemPresenterImpl(
            Provider<StartEditTransaction> startProvider,
			Provider<CommitEditTransaction> commitProvider,
			Provider<AssembleCreatorImpl> interactionProvider) {
		super(startProvider, commitProvider, interactionProvider);
	}

	
	

}
