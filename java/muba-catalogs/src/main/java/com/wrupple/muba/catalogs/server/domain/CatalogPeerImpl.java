package com.wrupple.muba.catalogs.server.domain;

import java.util.Date;
import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntryImpl;
import com.wrupple.muba.catalogs.domain.CatalogPeer;

public class CatalogPeerImpl extends CatalogEntryImpl implements CatalogPeer {
	
	private static final long serialVersionUID = -42266955486460366L;
	private Date expirationDate,presence;
	private Integer subscriptionStatus,channel,remoteStakeholder;
	private String host,publicKey,agent,catalogDomain,catalogUrlBase;
	private Long stakeHolder;
	private List<String> subscribedEvents;
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public Date getPresence() {
		return presence;
	}
	public void setPresence(Date presence) {
		this.presence = presence;
	}
	public Integer getSubscriptionStatus() {
		return subscriptionStatus;
	}
	public void setSubscriptionStatus(int subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public int getRemoteStakeholder() {
		return remoteStakeholder;
	}
	public void setRemoteStakeholder(int remoteStakeholder) {
		this.remoteStakeholder = remoteStakeholder;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getCatalogDomain() {
		return catalogDomain;
	}
	public void setCatalogDomain(String catalogDomain) {
		this.catalogDomain = catalogDomain;
	}
	public String getCatalogUrlBase() {
		return catalogUrlBase;
	}
	public void setCatalogUrlBase(String catalogUrlBase) {
		this.catalogUrlBase = catalogUrlBase;
	}
	public Long getStakeHolder() {
		return stakeHolder;
	}
	public void setStakeHolder(Long stakeHolder) {
		this.stakeHolder = stakeHolder;
	}
	public List<String> getSubscribedEvents() {
		return subscribedEvents;
	}
	public void setSubscribedEvents(List<String> subscribedEvents) {
		this.subscribedEvents = subscribedEvents;
	}
	@Override
	public boolean setMinimumActivityStatus(int requiredStatus) {
		if(requiredStatus>this.subscriptionStatus||this.subscriptionStatus==null){
			this.subscriptionStatus=requiredStatus;
		}
		return false;
	}
	@Override
	public boolean isSuscribed(String eventName) {
		return this.subscribedEvents!=null && this.subscribedEvents.contains(eventName);
	}
	@Override
	public void setDomain(long domain) {
		super.setDomain(domain);
	}
	@Override
	public void setStakeHolder(long stakeHolder) {
		this.stakeHolder=stakeHolder;
	}
	@Override
	public int getStakeHolderIndex() {
		return this.remoteStakeholder;
	}
	
	@Override
	public String getCatalogType() {
		return CATALOG;
	}

}
