package com.wrupple.muba.bpm.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import com.wrupple.muba.bpm.server.chain.command.AppendImplicitylySuscriptedPeers;
import com.wrupple.muba.event.server.chain.EventSuscriptionChain;

@Singleton
public class EventSuscriptionChainImpl  extends ChainBase implements EventSuscriptionChain {


	@Inject
	public EventSuscriptionChainImpl(AppendImplicitylySuscriptedPeers implicit) {
		super(new Command[]{implicit/*FIXME ExplicitEventSuscription, Notifications of work a worker peer can finish*/});
	}
}
