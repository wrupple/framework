package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.services.presentation.impl.AbstractTransactionAssembler;
import com.wrupple.muba.worker.client.activity.process.state.impl.CommitEditTransaction;
import com.wrupple.muba.worker.client.activity.process.state.impl.ReadingState;
import com.wrupple.muba.worker.client.activity.process.state.impl.StartEditTransaction;

import javax.inject.Provider;

public class ReadTransactionAssembler extends AbstractTransactionAssembler {

	
	@Inject
	public ReadTransactionAssembler(
			Provider<StartEditTransaction> startProvider,
			Provider<CommitEditTransaction> commitProvider,
			Provider<ReadingState> interactionProvider) {
		super(startProvider, commitProvider, interactionProvider);
	}



}
