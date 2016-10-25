package com.wrupple.muba.bootstrap.domain;

import java.util.Date;

import com.wrupple.muba.bootstrap.domain.reserved.HasStakeHolder;

public interface Host extends CatalogEntry,HasStakeHolder {
	String CATALOG = "Peers";
	final String PUBLIC_KEY="publicKey";
	int STATUS_OFFLINE = 0;// unreachable
	int STATUS_UNSUBSCRIBED = 1;// reachable but not interested in updates
	int STATUS_IDLE = 2;// reachable and subscribed but inactive
	int STATUS_ONLINE = 3;// subscribed

	Date getExpirationDate();

	Integer getSubscriptionStatus();

	/**
	 * @return last recorded interaction
	 */
	Date getPresence();

	void setSubscriptionStatus(int s);

	/**
	 * @param statusIdle
	 * @return status changed
	 */
	boolean setMinimumActivityStatus(int statusIdle);

	// id, whenever possible, is unique for each physical hardware making the
	// request
	// TODO list of criteria the actually interest me? (in bpm)

	/**
	 * usually related to the public id
	 * 
	 * @return
	 */
	String getPublicKey();

	void setPublicKey(String key);

	/**
	 * an extensible way to define what this peer is interested in
	 * 
	 * @param eventName
	 *            the system event in question
	 * @return if this particular Peer would be interested
	 */
	boolean isSuscribed(String eventName);

	public void setPresence(Date suscriptorLastReportedContact);

	/**
	 * @return if we have a private key and the door is reachable we can open it
	 */
	String getHost();

	void setHost(String h);

	/**
	 * @return the language used to speak to this peer
	 */
	int getChannel();

	void setChannel(int c);

	void setDomain(long domain);
}