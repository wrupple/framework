package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.inject.Inject;
import com.wrupple.muba.worker.server.chain.command.impl.AssembleEditorImpl;

import javax.inject.Provider;


public class EditProblemPresenterImpl implements ProblemPresenter {

	@Inject
    public EditProblemPresenterImpl(
            Provider<StartEditTransaction> startProvider,
			Provider<CommitEditTransaction> commitProvider,
			Provider<AssembleEditorImpl> interactionProvider) {
		super(startProvider, commitProvider, interactionProvider);
	}

}
