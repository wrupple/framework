package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.process.state.UserInteractionState;
import com.wrupple.muba.bpm.client.activity.process.state.impl.CommitSelectTransaction;
import com.wrupple.muba.bpm.client.activity.process.state.impl.SelectionTask;
import com.wrupple.muba.bpm.client.activity.process.state.impl.StartSelectTransaction;
import com.wrupple.muba.desktop.client.services.presentation.impl.AbstractTransactionAssembler;

import javax.inject.Provider;


public class SelectTransactionAssembler extends AbstractTransactionAssembler {


	private boolean disableBrowserInit;
	@Inject
	public SelectTransactionAssembler(
			Provider<StartSelectTransaction> startProvider,
			Provider<CommitSelectTransaction> commitProvider,
			Provider<SelectionTask> interactionProvider) {
		super(startProvider, commitProvider, interactionProvider);
	}
	@Override
	protected void initUserTransaction(UserInteractionState userTransaction) {
		((SelectionTask) userTransaction).setDisableBrowserInit(disableBrowserInit);
	}
	
	public void setDisableBrowserInit(String s){
		this.disableBrowserInit=Boolean.parseBoolean(s);
	}
}
