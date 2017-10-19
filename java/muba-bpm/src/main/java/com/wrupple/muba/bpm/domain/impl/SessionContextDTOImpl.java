package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.bpm.domain.SessionContextDTO;

public class SessionContextDTOImpl implements SessionContextDTO {
	
	private final String username,domain,stakeHolder;
	private final BPMPeer peer;
	
	
	public SessionContextDTOImpl(String username, String domain, String stakeHolder, BPMPeer peer) {
		super();
		this.username = username;
		this.domain = domain;
		this.stakeHolder = stakeHolder;
		this.peer = peer;
	}
	public String getUsername() {
		return username;
	}
	public String getDomain() {
		return domain;
	}
	public String getStakeHolder() {
		return stakeHolder;
	}

	@Override
	public void setStakeHolder(Object stakeHolder) {
		throw new IllegalArgumentException("cannot change session stakeHolder");
	}

	public BPMPeer getPeer() {
		return peer;
	}


	
	
}
