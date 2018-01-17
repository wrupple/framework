package com.wrupple.muba.desktop.domain.overlay;

import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;
import com.wrupple.muba.worker.domain.BPMPeer;

import java.util.Date;

@SuppressWarnings("serial")
public class JsBPMPeer extends JsCatalogEntry implements BPMPeer {

	@Override
	public native String getAgent() /*-{
									return this.agent;
									}-*/;

	@Override
	public native String getCatalogDomain() /*-{
											return this.catalogDomain;
											}-*/;

	@Override
	public native String getUrlBase() /*-{
										return this.urlBase;
										}-*/;

	@Override
	public native String getHost() /*-{
									return this.host;
									}-*/;

	@Override
	public Date getExpirationDate() {
		String s = getRawExpirationDate();
		if (s == null) {
			return null;
		} else {
			return DesktopLoadingStateHolder.getFormat().parse(s);
		}

	}

	public native String getRawExpirationDate() /*-{
												return this.expirationDate;
												}-*/;

	@Override
	public Integer getSubscriptionStatus() {
		String s = getRawSubscriptionStatus();
		if (s == null) {
			return null;
		} else {
			return Integer.parseInt(s);
		}
	}

	public native String getRawSubscriptionStatus() /*-{
													return this.subscriptionStatus;
													}-*/;

	@Override
	public Date getPresence() {
		String s = getRawPresence();
		if (s == null) {
			return null;
		} else {
			return DesktopLoadingStateHolder.getFormat().parse(s);
		}

	}

	public native String getRawPresence() /*-{
											return this.presence;
											}-*/;

	@Override
	public Date getTimestamp() {
		String s = getRawTimestamp();
		if (s == null) {
			return null;
		} else {
			return DesktopLoadingStateHolder.getFormat().parse(s);
		}

	}

	public native String getRawTimestamp() /*-{
											return this.timestamp;
											}-*/;

	@Override
	public native String getStakeHolder() /*-{
											return this.stakeHolder;
											}-*/;

	@Override
	public native String getPublicKey() /*-{
										return this.publicKey;
										}-*/;

	@Override
	public native String getPrivateKey() /*-{
											return this.privateKey;
											}-*/;

	@Override
	public boolean isSuscribed(String eventName) {
		throw new IllegalArgumentException("unsupported operation");
	}

	@Override
	public native String getLastLocation() /*-{
											return this.lastLocation;
											}-*/;

	@Override
	public void setPublicKey(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAgent(String s) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setHost(String h) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getChannel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setChannel(int c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTimestamp(Date d) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStakeHolder(long stakeHolder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPrivateKey(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setExpirationDate(Date expirationDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPresence(Date suscriptorLastReportedContact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSubscriptionStatus(int s) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setMinimumActivityStatus(int statusIdle) {
		// TODO Auto-generated method stub
		return false;
	}

}
