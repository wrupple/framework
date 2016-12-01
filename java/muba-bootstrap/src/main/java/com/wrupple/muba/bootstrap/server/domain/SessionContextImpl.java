package com.wrupple.muba.bootstrap.server.domain;

import java.util.Date;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.muba.bootstrap.domain.Host;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.SessionContext;

public class SessionContextImpl extends ContextBase implements SessionContext {
	private static final long serialVersionUID = 2234214815822637184L;
	private final long stakeHolder;
	private final Person stakeHolderValue;
	private final String peer;
	private final Host peerValue;
	private Long domain;
	
	public SessionContextImpl(long stakeHolder, Person stakeHolderValue, String peer, Host peerValue,Long domain) {
		super();
		this.stakeHolder = stakeHolder;
		this.stakeHolderValue = stakeHolderValue;
		this.peer = peer;
		this.peerValue = peerValue;
		this.domain=domain;
	}
	public Long getStakeHolder() {
		return stakeHolder;
	}
	public Person getStakeHolderValue() {
		return stakeHolderValue;
	}
	public String getPeer() {
		return peer;
	}
	public Host getPeerValue() {
		return peerValue;
	}
	@Override
	public void setStakeHolder(long stakeHolder) {
	}
	@Override
	public <T> T getStakeHolderPrincipal(Class<T> clazz) {
		return null;
	}
	@Override
	public boolean hasPermissionsToProcessContext(Context context, ServiceManifest manifest) {
		return true;
	}
	@Override
	public boolean isGranted(String roleCatalogMaster) {
		return true;
	}
	@Override
	public void releaseAuthority() {
		
	}
	@Override
	public void processAccessToken(String publicKey, String accessToken, String message, String pseudoUniqueRequestId,
			Date date) throws SecurityException {
		
	}
	@Override
	public boolean hasPermission(String string) {
		return true;
	}
	@Override
	public Long getDomain() {
		return domain;
	}
	
	
	

}
