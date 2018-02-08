package com.wrupple.muba.desktop.client.activity.widgets.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.activity.widgets.impl.AsynchonousHtmlWidget;
import com.wrupple.muba.worker.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;
import com.wrupple.muba.worker.shared.widgets.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class HTMLTransactionPanel extends AbstractHumanTaskWindow implements HumanTaskWindow {

	class PostRenderToolbarCallback extends DataCallback<String> {

		private Toolbar toolbar;
		private String id;

		public PostRenderToolbarCallback(Toolbar toolbar, String id) {
			this.toolbar = toolbar;
			this.id = id;
		}

		@Override
		public void execute() {
			renderToolbar(toolbar, id);
		}

	}

	class PostRenderTransactionCallback extends DataCallback<String> {

		private IsWidget w;

		public PostRenderTransactionCallback(IsWidget w) {
			this.w = w;
		}

		@Override
		public void execute() {
			renderTransatcion(w);
		}

	}
	
	static class ToolbarInfo {
		final Toolbar toolbar;
		final String id;
		public ToolbarInfo(Toolbar toolbar, String id) {
			super();
			this.toolbar = toolbar;
			this.id = id;
		}
	}

	private final AsynchonousHtmlWidget main;
	private final List<ToolbarInfo> toolbars;
	private HumanTaskProcessor<? extends JavaScriptObject,?> ui;

	@Inject
	public HTMLTransactionPanel(StorageManager sm, DesktopManager dm) {
		super();
		toolbars = new ArrayList<ToolbarInfo>();
		main = new AsynchonousHtmlWidget( true,sm,dm);
		initWidget(main);
	}

	@Override
	public void setWidget(IsWidget w) {
		main.hookCallback(new PostRenderTransactionCallback(w));
	}

	@Override
	public Toolbar getToolbarById(String toolbarId) {
		for(ToolbarInfo i : toolbars){
			if(i.id!=null && i.id.equals(toolbarId)){
				return i.toolbar;
			}
		}
		return null;
	}

	@Override
	public void focusToolbar(String toolbarId) {
		// ¿?
	}

	@Override
	public boolean isToolbarVisible(String toolbarId) {
		String tagId = getTagIdForToolbar(toolbarId);
		Element valueHolder = main.getElementById(tagId);
		return valueHolder!=null;
	}

	@Override
	public void addToolbar(Toolbar toolbar, JavaScriptObject properties) {
		DockLayoutTransactionPanel.DockToolbarProperties p = properties.cast();
		String id = p.getId();
		toolbars.add(new ToolbarInfo(toolbar,id));
		main.hookCallback(new PostRenderToolbarCallback(toolbar, id));
	}

	@Override
	public void focusToolbar(Toolbar activityToolbar) {
		activityToolbar.asWidget().getElement().scrollIntoView();
	}
	
	@Override
	public HumanTaskProcessor<? extends JavaScriptObject,?> getMainTaskProcessor() {
		return ui;
	}

	@Override
	public void setMainTaskProcessor(HumanTaskProcessor<? extends JavaScriptObject,?> ui) {
		this.ui=ui;
	}

	private void renderToolbar(Toolbar toolbar, String id) {
		Widget widget = toolbar.asWidget();
		widget.setSize("100%", "100%");
		String tagId = getTagIdForToolbar(id);
		Element valueHolder = main.getElementById(tagId);

		if (main.getHtmlPanel().getWidgetIndex(widget) < 0) {
			if (valueHolder == null) {
			} else {
				main.getHtmlPanel().add(widget, valueHolder);
			}
		}
	}

	private String getTagIdForToolbar(String id) {
		return "activityToolbar_" + id;
	}

	private void renderTransatcion(IsWidget transaction) {
		Widget widget = transaction.asWidget();
		widget.setSize("100%", "100%");
		String tagId = "activityTransaction";
		Element valueHolder = main.getElementById(tagId);
		if (main.getHtmlPanel().getWidgetIndex(widget) < 0) {
			if (valueHolder == null) {
				GWT.log("No holder for transaction  DOM id: " + tagId);
			} else {
				main.getHtmlPanel().add(widget, valueHolder);
			}
		}
	}

	public void setTaskHTML(String html) {
		// default (ltr landscape), ltr portrait, TODO rtl landscape, rtl  portrait
		if (html == null) {
			throw new IllegalArgumentException("no html to mount content on");
		} else {
			int split = html.indexOf(',');
			if (split > 1) {
				String portrait = html.substring(split + 1, html.length());
				html = html.substring(0, split);
				main.initialize(html, portrait);
			} else {
				main.initialize(html, null);
			}
		}
	}

	@Override
	public void setUnit(String layoutUnit) {

	}

}
