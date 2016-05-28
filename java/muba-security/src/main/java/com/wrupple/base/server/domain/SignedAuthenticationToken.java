package com.wrupple.base.server.domain;

import java.util.Date;

import org.apache.shiro.authc.AuthenticationToken;

import com.wrupple.vegetate.domain.PeerAuthenticationToken;

public class SignedAuthenticationToken implements AuthenticationToken,PeerAuthenticationToken {

	private static final long serialVersionUID = 4750332575812282490L;
	private final String key,rawMessage,signature,id;
	private final Date timestamp;
	
	
	
	public SignedAuthenticationToken(String key, String message, String signature,String id,Date timestamp) {
		super();
		if(key==null || message ==null || signature==null){
			throw new IllegalArgumentException("Incomplete Signature");
		}
		this.key = key;
		this.id=id;
		this.rawMessage = message;
		this.signature = signature;
		this.timestamp=timestamp;
	}

	@Override
	public String getPrincipal() {
		return key;
	}

	@Override
	public String getCredentials() {
		return signature;
	}

	public String getKey() {
		return key;
	}


	public String getSignature() {
		return signature;
	}

	@Override
	public void setPrincipal(String principal) {
		
	}

	@Override
	public void setCredentials(String credentials) {
		
	}

	@Override
	public String getRawMessage() {
		return rawMessage;
	}

	@Override
	public void setRawMessage(String rawMessage) {
		
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Date d) {
		
	}

	@Override
	public String getCatalog() {
		return PeerAuthenticationToken.class.getSimpleName();
	}

	@Override
	public String getIdAsString() {
		return getId();
	}

	@Override
	public void setIdAsString(String id) {
		
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "SignedAuthenticationToken [key=" + key + ", rawMessage=" + rawMessage + ", signature=" + signature + ", id=" + id + ", timestamp=" + timestamp
				+ "]";
	}
	
	

}
