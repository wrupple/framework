package com.wrupple.muba.bpm.domain.impl;

import java.util.Date;
import java.util.List;

import com.wrupple.muba.bootstrap.domain.Host;
import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.catalogs.domain.Location;

public class BPMPeerImpl implements BPMPeer {
	private String agent,catalogUrlBase,bPUrlBase,host,image,name,id,publicKey,privateKey;
	int channel,stakeHolderIndex;
	Long domain,stakeHolder;
	Integer subscriptionStatus;
	Date expirationDate,presence,timestamp;
	Location lastLocation;
	boolean suscribed;

	private static final long serialVersionUID = 8676652236470279026L;

	

	public int getStakeHolderIndex() {
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

	@Override
	public boolean isAnonymouslyVisible() {
		return false;
	}

	@Override
	public void setAnonymouslyVisible(boolean p) {

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public Long getDomain() {
		return domain;
	}

	public void setDomain(Long domain) {
		this.domain = domain;
	}

	public Long getStakeHolder() {
		return stakeHolder;
	}

	public void setStakeHolder(Long stakeHolder) {
		this.stakeHolder = stakeHolder;
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


	@Override
	public String getIdAsString() {
		return getId();
	}

	@Override
	public void setIdAsString(String id) {
		setId(id);
	}

	@Override
	public void setStakeHolder(long stakeHolder) {
		this.stakeHolder=stakeHolder;
	}
private List<String> events;
	@Override
	public boolean isSuscribed(String eventName) {
		return events!=null && events.contains(eventName);
	}

	@Override
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
	public void setDomain(long domain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCatalogType() {
		return Host.CATALOG;
	}
	


}
