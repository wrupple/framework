package com.wrupple.muba.desktop.client.activity.widgets;

public class RequestToolbarToken extends UserInteractionToken {
	
	private final int requestId;

	/**
	 * @param requestId number of the request
	 * @param service update, list, read, etc.
	 * @param target product, price
	 */
	public RequestToolbarToken(int requestId,String service, String target) {
		super();
		this.requestId = requestId;
		firstSpan.setInnerText(service);
		nameSpan.setInnerText(target);
	}

	public int getRequestId() {
		return requestId;
	}
}
