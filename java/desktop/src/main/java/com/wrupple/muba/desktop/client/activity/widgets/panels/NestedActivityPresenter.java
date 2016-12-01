package com.wrupple.muba.desktop.client.activity.widgets.panels;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.widget.Toolbar;
import com.wrupple.muba.desktop.client.activity.widgets.ContentPanel.ToolbarDirection;
import com.wrupple.muba.desktop.client.activity.widgets.ProcessPresenter;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.layout.LayoutPositioner.ToolbarJoining;
import com.wrupple.muba.desktop.domain.ToolbarConfiguration;

public class NestedActivityPresenter extends ResizeComposite implements ProcessPresenter  {

	public static final class DefaultToolbarProperties extends JavaScriptObject {

		protected DefaultToolbarProperties() {
			super();
		}

		public native String getDirection()/*-{
			return this.direction;
		}-*/;

		public int getSizeInt() {
			String rawSize = getSize();
			return Integer.parseInt(rawSize);
		}

		public native int getTitleSize()/*-{
			var value = this.titleSize;
			if (value == null) {
				value = 25;
			}
			return parseInt(value);
		}-*/;

		public native String getSize()/*-{
			if (this.toolbarSize == null) {
				this.toolbarSize = "0";
			}
			return this.toolbarSize;
		}-*/;

		public native String getToolbarTitle()/*-{
			return this.toolbarTitle;
		}-*/;

	}

	// TODO implements a HOLD and Release mechanism so toolbars can be added and
	// not redrawing each time
	class Resizer implements ResizeHandler {

		@Override
		public void onResize(ResizeEvent event) {
			redraw();
		}

	}

	private final List<ToolbarConfiguration> toolbars;
	private final TransitionPanel container;
	private final LayoutPanel main;
	private final ResizeHandler handler;
	protected final PanelWithToolbarLayoutDelegate layout;
	private final DesktopManager dm;
	private boolean currentOrientationIsLandscape;
	private boolean dependsOnOrientation;

	@Inject
	public NestedActivityPresenter(DesktopManager dm) {
		container = new TransitionPanel();
		main = new LayoutPanel();
		handler = new Resizer();
		toolbars = new ArrayList<ToolbarConfiguration>();
		layout = new PanelWithToolbarLayoutDelegate(dm);
		initWidget(main);
		this.dm = dm;
		redraw();
		dependsOnOrientation=false;
	}

	@Override
	public void onResize() {
		if (dependsOnOrientation&&currentOrientationIsLandscape != dm.isLandscape()) {
			redraw();
		}
		super.onResize();
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		redraw();
	}

	public List<ToolbarConfiguration> getToolbars() {
		return this.toolbars;
	}

	public TransitionPanel getRootTaskPresenter() {
		return container;
	}

	public void hideToolbar(int i) {
		if (i < toolbars.size() && i >= 0) {
			this.toolbars.get(i).getToolbarAsWidget().setVisible(false);
			redraw();
		}

	}

	public void showToolbar(int i) {
		if (i < toolbars.size() && i >= 0) {
			this.toolbars.get(i).getToolbarAsWidget().setVisible(true);
			redraw();
		}
	}

	public void hideToolbar(Toolbar step) {
		int i = findToolbarIndex(step);
		hideToolbar(i);
	}

	public void showToolbar(Toolbar step) {
		int i = findToolbarIndex(step);
		showToolbar(i);
	}

	protected int findToolbarIndex(Toolbar toolbar) {
		for (int i = 0; i < toolbars.size(); i++) {
			if (toolbars.get(i).getToolbarAsWidget() == toolbar) {
				return i;
			}
		}
		return -1;
	}

	public void addToolbarAndRedraw(Toolbar toolbar, boolean collapsible, boolean redraw, ToolbarDirection direction, String name, double size) {
		if (toolbar == null||direction==null) {
			return;
		}
		if(ToolbarDirection.SHORT_END==direction ||ToolbarDirection.WIDE_END==direction||ToolbarDirection.SHORT_START==direction||ToolbarDirection.SHORT_END==direction){
			this.dependsOnOrientation=true;
		}
		toolbars.add(new ToolbarConfiguration(toolbar.asWidget(), collapsible, direction, name, size, layout, dm));
		toolbar.addResizeHandler(handler);
		if (redraw) {
			redraw();
		}
	}

	protected void redraw() {
		if (isAttached()) {
			this.currentOrientationIsLandscape = dm.isLandscape();
			layout.initialize(main, container);
			Toolbar toolbar;
			for (ToolbarConfiguration config : toolbars) {
				toolbar = config.getToolbar();
				if (toolbar.asWidget().isVisible()) {
					layout.addAtPosition(config);
				}
			}
			layout.animate(500);
		}
	}

	public boolean isToolbarVisible(Toolbar toolbar) {
		if (findToolbarIndex(toolbar) > -1) {
			return toolbar.asWidget().isVisible();
		} else {
			return false;
		}
	}

	public Widget getCurrentWidget() {
		return container.getCurrentWidget();
	}

	public void setJoinLineStartToolbars(ToolbarJoining joinLineStartToolbars) {
		layout.setJoinLineStartToolbars(joinLineStartToolbars);
	}

	public void setLineStartCollapsible(boolean lineStartCollapsible) {
		layout.setLineStartCollapsible(lineStartCollapsible);
	}

	public void setLineEndCollapsible(boolean lineEndCollapsible) {
		layout.setLineEndCollapsible(lineEndCollapsible);
		;
	}

	public void setJoinLineEndToolbars(ToolbarJoining joinLineEndToolbars) {
		layout.setJoinLineEndToolbars(joinLineEndToolbars);
		;
	}

	public void focusToolbar(Toolbar toolbar) {
		int index = findToolbarIndex(toolbar);
		if (!isToolbarVisible(toolbar)) {
			showToolbar(index);
		}
		if (toolbars.get(index).isCollapsible()) {
			toolbars.get(index).getWrapper().open();
		} else {
			layout.showWidgetInAggregatesIfAvailable(toolbar.asWidget());
		}
	}

	public void setUnit(Unit newUnit) {
		layout.setUnit(newUnit);
	}

}
