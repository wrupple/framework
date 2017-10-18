package com.wrupple.muba.catalogs.server.domain;

import java.util.Date;
import java.util.List;

import com.wrupple.muba.catalogs.domain.ContentNodeImpl;
import com.wrupple.muba.event.domain.Host;

public class HostImpl extends ContentNodeImpl implements com.wrupple.muba.event.domain.Host {
	

	private static final long serialVersionUID = -671577088785926566L;

	private Date expirationDate;
	
	private Long stakeHolder;
	private String agent;
	private Integer channel;
	private Integer stakeHolderIndex;
	private String catalogUrlBase;
	
	private Integer subscriptionStatus;
	private Date presence;
	private String publicKey;
	private List<String> properties;
	private String host;
	

	
	public List<String> getProperties() {
		return properties;
	}
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}
	
	public Long getStakeHolder() {
		return stakeHolder;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public Integer getChannel() {
		return channel;
	}
	public void setChannel(Integer channel) {
		this.channel = channel;
	}
	public Integer getStakeHolderIndex() {
		return stakeHolderIndex;
	}
	public void setStakeHolderIndex(Integer stakeHolderIndex) {
		this.stakeHolderIndex = stakeHolderIndex;
	}
	public String getCatalogUrlBase() {
		return catalogUrlBase;
	}
	public void setCatalogUrlBase(String catalogUrlBase) {
		this.catalogUrlBase = catalogUrlBase;
	}
	

	public Integer getSubscriptionStatus() {
		return subscriptionStatus;
	}
	public void setSubscriptionStatus(Integer subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}
	public Date getPresence() {
		return presence;
	}
	public void setPresence(Date presence) {
		this.presence = presence;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	@Override
	public boolean setMinimumActivityStatus(int statusIdle) {
		if(this.subscriptionStatus==null||this.subscriptionStatus.intValue()<statusIdle){
			this.subscriptionStatus=statusIdle;
			return true;
		}
		return false;
	}
	@Override
	public boolean isSuscribed(String eventName) {
		if(properties==null){
			return false;
		}
		return properties.contains(eventName);
	}
	@Override
	public String getHost() {
		return host;
	}
	@Override
	public void setHost(String h) {
		this.host=h;
	}
	@Override
	public void setStakeHolder(Long stakeHolder) {
		this.stakeHolder=stakeHolder;
	}
	@Override
	public String getCatalogType() {
		return Host.CATALOG;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}


}
