package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.process.state.UserInteractionState;
import com.wrupple.muba.worker.server.chain.command.impl.AssembleBrowserImpl;
import com.wrupple.muba.worker.server.chain.command.impl.ConfigureBrowserImpl;

import javax.inject.Provider;


public class SelectProblemPresenterImpl implements ProblemPresenter {


	private boolean disableBrowserInit;
	@Inject
    public SelectProblemPresenterImpl(
            Provider<ConfigureBrowserImpl> startProvider,
			Provider<CommitSelectTransaction> commitProvider,
			Provider<AssembleBrowserImpl> interactionProvider) {
		super(startProvider, commitProvider, interactionProvider);
	}
	@Override
	protected void initUserTransaction(UserInteractionState userTransaction) {
		((AssembleBrowserImpl) userTransaction).setDisableBrowserInit(disableBrowserInit);
	}
	
	public void setDisableBrowserInit(String s){
		this.disableBrowserInit=Boolean.parseBoolean(s);
	}
}
