package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.bpm.domain.SessionContextDTO;

public final class JsSessionContextDTO extends JavaScriptObject implements SessionContextDTO {

	protected JsSessionContextDTO() {
		super();
	}

	@Override
	public native String getUsername()/*-{
										return this.username;
										}-*/;

	public native JsArrayString getInstalledApplicationsData() /*-{
																return this.installedApplicationsData;
																}-*/;

	@Override
	public native String getStakeHolder() /*-{
		return this.stakeHolder;
	}-*/;

	@Override
	public void setStakeHolder(long stakeHolder) {
		throw new IllegalArgumentException();

	}

	@Override
	public native String getDomain() /*-{
		return this.domain;
	}-*/;

	@Override
	public native JsBPMPeer getPeer() /*-{
		return this.peer;
	}-*/;


}
