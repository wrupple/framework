package com.wrupple.muba.desktop.client.services.logic.impl;

import javax.inject.Provider;

import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.process.state.impl.CommitEditTransaction;
import com.wrupple.muba.bpm.client.activity.process.state.impl.EditingState;
import com.wrupple.muba.bpm.client.activity.process.state.impl.StartEditTransaction;
import com.wrupple.muba.desktop.client.services.presentation.impl.AbstractTransactionAssembler;


public class EditTransactionAssembler extends AbstractTransactionAssembler{

	@Inject
	public EditTransactionAssembler(
			Provider<StartEditTransaction> startProvider,
			Provider<CommitEditTransaction> commitProvider,
			Provider<EditingState> interactionProvider) {
		super(startProvider, commitProvider, interactionProvider);
	}

}
