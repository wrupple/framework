package com.wrupple.base.server.domain;

import org.apache.shiro.authc.AuthenticationToken;

public class SystemAuthenticationToken implements AuthenticationToken {
	
	private static final long serialVersionUID = 8258975837267471L;
	private final long principalPersonId;
	private final Object lender;
	
	public SystemAuthenticationToken(long principalPersonId, Object lenderPrincipal) {
		super();
		this.principalPersonId = principalPersonId;
		this.lender = lenderPrincipal;
	}

	@Override
	public Object getPrincipal() {
		return principalPersonId;
	}

	@Override
	public Object getCredentials() {
		return lender;
	}

	public long getPrincipalPersonId() {
		return principalPersonId;
	}

	public Object getLender() {
		return lender;
	}

}
