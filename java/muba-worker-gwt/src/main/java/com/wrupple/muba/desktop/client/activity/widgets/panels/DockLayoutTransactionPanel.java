package com.wrupple.muba.desktop.client.activity.widgets.panels;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.layout.LayoutPositioner.ToolbarJoining;
import com.wrupple.muba.desktop.domain.ToolbarConfiguration;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;
import com.wrupple.muba.worker.shared.widgets.Toolbar;

import java.util.List;

public class DockLayoutTransactionPanel extends AbstractHumanTaskWindow implements HumanTaskWindow {

	public final static class DockToolbarProperties extends JavaScriptObject {

		protected DockToolbarProperties() {
		}

		public native String getId() /*-{
			return this.toolbarId;
		}-*/;

		public native boolean isCollapsible() /*-{
			if (this.collapsible == null) {
				return false;
			} else {
				return true;
			}

		}-*/;

		public ToolbarDirection getDirection() {
			return parseDirection(getRawDirection());
		}

		private native String getRawDirection() /*-{
			return this.direction;
		}-*/;

		public native double getSize() /*-{
			if (this.size == null) {
				this.size = 60;
			}
			return this.size;
		}-*/;

		protected ToolbarDirection parseDirection(String direction) {
			if (direction == null) {
				return ToolbarDirection.SHORT_END;
			} else {
				if (ToolbarDirection.NORTH.toString().toString().equals(direction)) {
					return ToolbarDirection.NORTH;
				} else if (ToolbarDirection.EAST.toString().equals(direction)) {
					return ToolbarDirection.EAST;
				} else if (ToolbarDirection.SOUTH.toString().equals(direction)) {
					return ToolbarDirection.SOUTH;
				} else if (ToolbarDirection.WEST.toString().equals(direction)) {
					return ToolbarDirection.WEST;
				} else if (ToolbarDirection.CENTER.toString().equals(direction)) {
					return ToolbarDirection.CENTER;
				} else if (ToolbarDirection.LINE_START.toString().equals(direction)) {
					return ToolbarDirection.LINE_START;
				} else if (ToolbarDirection.LINE_END.toString().equals(direction)) {
					return ToolbarDirection.LINE_END;
				} else if (ToolbarDirection.SHORT_END.toString().equals(direction)) {
					return ToolbarDirection.SHORT_END;
				} else if (ToolbarDirection.SHORT_START.toString().equals(direction)) {
					return ToolbarDirection.SHORT_START;
				} else if (ToolbarDirection.WIDE_END.toString().equals(direction)) {
					return ToolbarDirection.WIDE_END;
				} else if (ToolbarDirection.WIDE_START.toString().equals(direction)) {
					return ToolbarDirection.WIDE_START;
				} else {
					return ToolbarDirection.SHORT_END;
				}
			}
		}

	}

    NestedActivityWindow main;

	private HumanTaskProcessor<? extends JavaScriptObject,?> ui;

	@Inject
	public DockLayoutTransactionPanel(DesktopManager dm) {
		super();
        main = new NestedActivityWindow(dm);
        initWidget(main);
	}

	@Override
	public void focusToolbar(String toolbarId) {
		Toolbar toolbar = getToolbarById(toolbarId);
		main.focusToolbar(toolbar);
	}

	@Override
	public boolean isToolbarVisible(String toolbarId) {
		Toolbar toolbar = getToolbarById(toolbarId);
		return main.isToolbarVisible(toolbar);
	}

	@Override
	public void addToolbar(Toolbar toolbar, JavaScriptObject properties) {
		DockToolbarProperties p = properties.cast();
		String name = p.getId();
		boolean collapsible = p.isCollapsible();
		boolean redraw = GWTUtils.getAttributeAsBoolean(properties, REDRAW_FLAG);
		ToolbarDirection direction = p.getDirection();
		double size = p.getSize();
		main.addToolbarAndRedraw(toolbar, collapsible, redraw, direction, name, size);
	}

	@Override
	public void setUnit(String layoutUnit) {
		Unit newUnit = parseUnit(layoutUnit);
		if (newUnit != null) {
			main.setUnit(newUnit);
		}
	}

	@Override
	public void setWidget(IsWidget w) {
		main.getRootTaskPresenter().setWidget(w);
	}

	@Override
	public Toolbar getToolbarById(String toolbarId) {
        List<ToolbarConfiguration> toolbars = main.getToolbars();
        String curr;
		for (ToolbarConfiguration toolbar : toolbars) {
			curr = toolbar.getName();
			if (toolbarId.equals(curr)) {
				return toolbar.getToolbar();
			}
		}
		return null;
	}

	@Override
	public void focusToolbar(Toolbar toolbar) {
		main.focusToolbar(toolbar);
	}

	@Override
	public HumanTaskProcessor<? extends JavaScriptObject,?> getMainTaskProcessor() {
		return ui;
	}

	@Override
	public void setMainTaskProcessor(HumanTaskProcessor<? extends JavaScriptObject,?> ui) {
		this.ui = ui;
	}

	public void setJoinLineStartToolbars(String s) {
		if (s == null) {
			return;
		}
		main.setJoinLineStartToolbars(parseJoinConfiguration(s));
	}

	public void setJoinLineEndToolbars(String s) {
		if (s == null) {
			return;
		}
		main.setJoinLineEndToolbars(parseJoinConfiguration(s));
	}

	public void setLineStartAggregateCollapsible(String lineStartCollapsible) {
		main.setLineStartCollapsible(Boolean.parseBoolean(lineStartCollapsible));
	}

	public void setLineEndAggregateCollapsible(String lineEndCollapsible) {
		main.setLineEndCollapsible(Boolean.parseBoolean(lineEndCollapsible));
	}

	private ToolbarJoining parseJoinConfiguration(String parseable) {
		if (ToolbarJoining.HORIZONTAL.toString().equals(parseable)) {
			return ToolbarJoining.HORIZONTAL;
		} else if (ToolbarJoining.VERTICAL.toString().equals(parseable)) {
			return ToolbarJoining.VERTICAL;
		} else if (ToolbarJoining.STACK.toString().equals(parseable)) {
			return ToolbarJoining.STACK;
		} else {
			throw new IllegalArgumentException();
		}
	}

}
