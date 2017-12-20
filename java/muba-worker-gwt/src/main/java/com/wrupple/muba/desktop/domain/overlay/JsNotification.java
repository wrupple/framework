package com.wrupple.muba.desktop.domain.overlay;

import com.wrupple.muba.bpm.domain.Notification;
import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;

import java.util.Date;

@SuppressWarnings("serial")
public final class JsNotification extends JsContentNode implements Notification {
	protected JsNotification() {
	}

	public native int getStatus() /*-{
									return this.status;
									}-*/;

	public Long getSource() {
		return Long.parseLong(getRawSource());
	}

	private native String getRawSource() /*-{
											return this.source;
											}-*/;

	public Long getTargetDiscriminator() {
		return Long.parseLong(getRawTargetDiscriminator());
	}

	private native String getRawTargetDiscriminator() /*-{
														return this.targetDiscriminator;
														}-*/;

	public Long getHandler() {
		return Long.parseLong(getRawHandler());
	}

	private native String getRawHandler() /*-{
											return this.handler;
											}-*/;

	public Date getHandled() {
		String s = getRawHandled();
		if (s == null) {
			return null;
		}

		return DesktopLoadingStateHolder.getFormat().parse(s);
	}

	private native String getRawHandled() /*-{
											return this.handled;
											}-*/;

	public native String getCatalogId() /*-{
										return this.catalogId;
										}-*/;

	public native String getCatalogEntryId() /*-{
												return this.catalogEntryId;
												}-*/;

	@Override
	public native void setStatus(int i) /*-{
										this.status=i;
										}-*/;

	@Override
	public void setHandled(Date handled) {
		throw new IllegalArgumentException();
	}

	@Override
	public void setHandler(Long personId) {
		throw new IllegalArgumentException();
	}

	@Override
	public native boolean isArchived() /*-{
											return this.disposable;
											}-*/;

	@Override
	public void setArchived(Boolean disposable) {
		throw new IllegalArgumentException();
	}

	@Override
	public Date getDue() {
		String s = getRawDue();
		if (s == null) {
			return null;
		}
		return DesktopLoadingStateHolder.getFormat().parse(s);
	}

	private native String getRawDue() /*-{
											return this.due;
											}-*/;

	@Override
	public native String getStakeHolder() /*-{
											return null;
											}-*/;

	@Override
	public void setStakeHolder(long stakeHolder) {
		throw new IllegalArgumentException();
	}

	@Override
	public native void setCatalogId(String catalog) /*-{
													this.catalogId=catalog;
													}-*/;

	@Override
	public  native  void setCatalogEntryId(String id) /*-{
		this.catalogEntryId=id;
	}-*/;
}
