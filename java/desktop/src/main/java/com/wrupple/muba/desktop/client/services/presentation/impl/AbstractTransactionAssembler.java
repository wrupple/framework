package com.wrupple.muba.desktop.client.services.presentation.impl;

import javax.inject.Provider;

import com.wrupple.muba.bpm.client.activity.process.state.CommitUserTransaction;
import com.wrupple.muba.bpm.client.activity.process.state.StartUserTransaction;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.TransactionAssembler;
import com.wrupple.muba.bpm.server.chain.command.UserInteractionState;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
public  class AbstractTransactionAssembler implements TransactionAssembler {

	private final Provider<? extends StartUserTransaction> startProvider;
	private final Provider<? extends CommitUserTransaction> commitProvider;
	private final Provider<? extends UserInteractionState> interactionProvider;
	private String layoutUnit;
	private String transactionViewClass;
	private String saveTo;
	
	public AbstractTransactionAssembler(
			Provider<? extends StartUserTransaction> startProvider,
			Provider<? extends CommitUserTransaction> commitProvider,
			Provider<? extends UserInteractionState> interactionProvider) {
		super();
		this.startProvider = startProvider;
		this.commitProvider = commitProvider;
		this.interactionProvider = interactionProvider;
	}

	@Override
	public void assembleTaskProcessSection(Process<?, ?> regreso,
			JsProcessTaskDescriptor step) {

		StartUserTransaction transactionStart = this.startProvider.get();
		transactionStart.setTaskDescriptor(step);
		regreso.addState(transactionStart);

		UserInteractionState userTransaction= interactionProvider.get();
		userTransaction.setTaskDescriptor(step);
		userTransaction.setLayoutUnit(layoutUnit);
		userTransaction.setSaveTo(saveTo);
		userTransaction.setTransactionViewClass(transactionViewClass);
		initUserTransaction(userTransaction);
		regreso.addState(userTransaction);

		CommitUserTransaction transactionCommit = this.commitProvider.get();
		transactionCommit.setTaskDescriptor(step);
		transactionCommit.setSaveTo(saveTo);
		regreso.addState(transactionCommit);
		
	}

	
	protected void initUserTransaction(UserInteractionState userTransaction) {
		
	}

	
	public void setLayoutUnit(String s){
		this.layoutUnit=s;
	}
	
	public void setTransactionViewClass(String s){
		this.transactionViewClass=s;
	}

	public void setSaveTo(String s){
		this.saveTo=s;
	}
	
}
