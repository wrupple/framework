package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.inject.Inject;
import com.wrupple.muba.worker.server.chain.command.impl.AssembleViewerImpl;

import javax.inject.Provider;

public class ReadProblemPresenterImpl implements ProblemPresenter {

	
	@Inject
    public ReadProblemPresenterImpl(
            Provider<StartEditTransaction> startProvider,
			Provider<CommitEditTransaction> commitProvider,
			Provider<AssembleViewerImpl> interactionProvider) {
		super(startProvider, commitProvider, interactionProvider);
	}



}
