package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.catalogs.domain.Location;
import com.wrupple.muba.catalogs.server.domain.HostImpl;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;
import com.wrupple.muba.worker.domain.BPMPeer;

import java.util.Date;
import java.util.List;

public class BPMPeerImpl extends HostImpl implements BPMPeer {
	private String agent,catalogUrlBase,bPUrlBase,host,publicKey,privateKey;
	int channel,stakeHolderIndex;
	Integer subscriptionStatus;
	Date expirationDate,presence;
	Location lastLocation;
	boolean suscribed;
	private List<String> events;

	private static final long serialVersionUID = 8676652236470279026L;

	

	public Integer getStakeHolderIndex() {
		return stakeHolderIndex;
	}

	public void setStakeHolderIndex(int stakeHolderIndex) {
		this.stakeHolderIndex = stakeHolderIndex;
	}

	public String getbPUrlBase() {
		return bPUrlBase;
	}

	public void setbPUrlBase(String bPUrlBase) {
		this.bPUrlBase = bPUrlBase;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public boolean isSuscribed() {
		return suscribed;
	}

	public void setSuscribed(boolean suscribed) {
		this.suscribed = suscribed;
	}


	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}


	public String getCatalogUrlBase() {
		return catalogUrlBase;
	}

	public void setCatalogUrlBase(String catalogUrlBase) {
		this.catalogUrlBase = catalogUrlBase;
	}

	public String getBPUrlBase() {
		return bPUrlBase;
	}

	public void setBPUrlBase(String bPUrlBase) {
		this.bPUrlBase = bPUrlBase;
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

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Integer getSubscriptionStatus() {
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(Integer subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}

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

	@Override
	public boolean isSuscribed(String eventName) {
		return events!=null && events.contains(eventName);
	}

	public void setSubscriptionStatus(int s) {
		this.subscriptionStatus=s;
	}

	@Override
	public boolean setMinimumActivityStatus(int status) {
		setPresence(new Date());
		if (this.subscriptionStatus == null) {
			setSubscriptionStatus(status);
			return true;
		} else if (this.subscriptionStatus.intValue() < status) {
			setSubscriptionStatus(status);
			return true;
		}
		return false;
	}

	public List<String> getEvents() {
		return events;
	}

	public void setEvents(List<String> events) {
		this.events = events;
	}

	@Override
	public String getCatalogType() {
		return Host.CATALOG;
	}
	


}
