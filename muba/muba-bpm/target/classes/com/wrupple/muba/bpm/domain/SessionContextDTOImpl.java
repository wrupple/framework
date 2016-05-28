package com.wrupple.muba.bpm.domain;


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
	public BPMPeer getPeer() {
		return peer;
	}
	@Override
	public void setStakeHolder(long stakeHolder) {
		throw new IllegalArgumentException("cannot change session stakeHolder");
	}
	
	
}
