package com.wrupple.muba.desktop.client.activity.widgets.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.cms.domain.WruppleDomainHTMLPage;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogActionRequest;
import com.wrupple.vegetate.client.services.StorageManager;

import java.util.ArrayList;
import java.util.List;

public class AsynchonousHtmlWidget extends SimpleLayoutPanel {
	class HTMLPageCallback extends DataCallback<String> {
		@Override
		public void execute() {
			replaceHTML(result);
			if (hooks != null) {
				for (StateTransition<String> hook : hooks) {
					hook.setResultAndFinish(result);
				}
			}
		}
	}

	List<StateTransition<String>> hooks;
	final StateTransition<String> callback;
	private final boolean scroll;
	private final StorageManager sm;
	private final DesktopManager dm;
	HTMLPanel htmlPanel;

	/*
	 * Supported Variants
	 */
	private String ltrLandscape, ltrPortrait;
	private String currentlyDrawn;

	public AsynchonousHtmlWidget(boolean scroll, StorageManager sm, DesktopManager dm) {
		super();
		callback = new HTMLPageCallback();
		this.scroll = scroll;
		this.sm = sm;
		this.dm = dm;

	}

	@Override
	public void onResize() {
		renderApropiateHtml();
		super.onResize();
	}

	public void hookCallback(StateTransition<String> callback) {
		if (hooks == null) {
			hooks = new ArrayList<StateTransition<String>>();
		}
		hooks.add(callback);
		if (htmlPanel != null) {
			callback.setResultAndFinish(currentlyDrawn);
		}
	}

	protected void replaceHTML(String rawUnsafeHtmlValue) {
		// FIXME this is not actually a trusted string, client and server
		// side validation
		SafeHtml html = SafeHtmlUtils.fromTrustedString(rawUnsafeHtmlValue);
		htmlPanel = new HTMLPanel(html);
		setWidget(htmlPanel);
		if (scroll) {
			htmlPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
		}
	}

	public void initialize(String htmlPageId, String portraitHtml) {
		this.ltrLandscape = htmlPageId;
		this.ltrPortrait = portraitHtml;
		renderApropiateHtml();
	}

	private void renderApropiateHtml() {
		boolean currentlyLandscape = dm.isLandscape();
		String htmlPageId;
		if (currentlyLandscape) {
			htmlPageId = this.ltrLandscape;
		} else {
			if (this.ltrPortrait == null) {
				htmlPageId = this.ltrLandscape;
			} else {
				htmlPageId = this.ltrPortrait;
			}
		}
		if (htmlPageId != null && !htmlPageId.equals(currentlyDrawn)) {
			this.currentlyDrawn = htmlPageId;
			// TODO cache

			JsCatalogActionRequest request = JsCatalogActionRequest.newRequest(dm.getCurrentActivityDomain(), CatalogActionRequest.LOCALE, WruppleDomainHTMLPage.CATALOG, CatalogActionRequest.READ_ACTION, htmlPageId,"0", null, null);
			String htmlRequestUri = sm.getRemoteStorageUnit(dm.getCurrentActivityHost()).buildServiceUrl(request );
			
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, htmlRequestUri);

			builder.setCallback(new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					callback.setResultAndFinish(response.getText());
				}

				@Override
				public void onError(Request request, Throwable exception) {
					GWT.log("error retriving html", exception);
				}
			});
			try {
				builder.send();
			} catch (RequestException e) {
				GWT.log("error retriving html", e);
			}
		}

	}

	public HTMLPanel getHtmlPanel() {
		return htmlPanel;
	}

	public Element getElementById(String id) {
		// htmlPanel.getElementById(id);
		Element parent = htmlPanel.getElement();
		return getElementById(id, parent);
	}

	private Element getElementById(String id, Element parent) {
		String parentId = parent.getId();
		if (parentId != null && id.equals(parentId)) {
			return parent;
		} else {
			NodeList<Node> children = parent.getChildNodes();
			if (children != null && children.getLength() > 0) {
				Node child;
				Element childElement;
				Element found = null;
				for (int i = 0; i < children.getLength(); i++) {
					child = children.getItem(i);
					childElement = child.cast();
					found = getElementById(id, childElement);
					if (found != null) {
						return found;
					}
				}
			}

			return null;
		}
	}

}
