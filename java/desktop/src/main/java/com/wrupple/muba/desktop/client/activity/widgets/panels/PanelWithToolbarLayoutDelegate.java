package com.wrupple.muba.desktop.client.activity.widgets.panels;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.activity.widgets.CollapsibleSimplePanel;
import com.wrupple.muba.desktop.client.activity.widgets.ContentPanel.ToolbarDirection;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.layout.LayoutPositioner;
import com.wrupple.muba.desktop.domain.ToolbarConfiguration;

public class PanelWithToolbarLayoutDelegate implements LayoutPositioner {

	class CollapsibleResizeHandler implements ResizeHandler {
		double originalSize;
		CollapsibleSimplePanel wrapper;
		ToolbarConfiguration aggregate;

		public CollapsibleResizeHandler(CollapsibleSimplePanel wrapper, ToolbarConfiguration aggregate, double size) {
			this.originalSize = size;
			this.wrapper = wrapper;
			this.aggregate = aggregate;
		}

		@Override
		public void onResize(ResizeEvent event) {
			boolean isOpen = wrapper.isOpen();
			if (isOpen) {
				aggregate.setSize(originalSize);
			} else {
				aggregate.setSize(wrapper.getControllerSize(unit));
			}
			animate(500);
		}

	}

	private LayoutPanel main;
	private TransitionPanel container;
	private List<ToolbarConfiguration> layoutActions;

	private ToolbarJoining joinLineStartToolbars;
	private boolean lineStartCollapsible;
	private ToolbarConfiguration lineStartAggregate;

	private ToolbarJoining joinLineEndToolbars;
	private boolean lineEndCollapsible;
	private ToolbarConfiguration lineEndAggregate;
	private Unit unit;
	private DesktopManager dm;

	@Inject
	public PanelWithToolbarLayoutDelegate(DesktopManager dm) {
		super();
		this.unit=Unit.PX;
		this.dm=dm;
	}

	@Override
	public void initialize(LayoutPanel main, TransitionPanel container) {
		if (layoutActions == null) {
			layoutActions = new ArrayList<ToolbarConfiguration>();
		} else {
			layoutActions.clear();
		}
		lineStartAggregate = null;
		lineEndAggregate = null;
		this.main = main;
		main.clear();
		this.container = container;
		main.add(container);
	}

	@Override
	public void animate(int duration) {
		Widget toolbar;
		DockLayoutPanel.Direction direction;
		double size;
		double top = 0;
		double right = 0;
		double left = 0;
		double bottom = 0;
		boolean rtl = LocaleInfo.getCurrentLocale().isRTL();
		for (ToolbarConfiguration action : this.layoutActions) {
			toolbar = action.getToolbarAsWidget();
			direction = action.getDockDirection();
			size = action.getSize();
			if (main.getWidgetIndex(toolbar) == -1) {
				main.add(toolbar);
			}

			switch (direction) {
			case NORTH:
				main.setWidgetTopHeight(toolbar, top - size, unit, size, unit);
				main.setWidgetLeftRight(toolbar, left, unit, right, unit);
				break;
			case EAST:
				main.setWidgetRightWidth(toolbar, right - size, unit, size, unit);
				main.setWidgetTopBottom(toolbar, top, unit, bottom, unit);
				break;
			case WEST:
				main.setWidgetLeftWidth(toolbar, left - size, unit, size, unit);
				main.setWidgetTopBottom(toolbar, top, unit, bottom, unit);
				break;
			case SOUTH:
				main.setWidgetBottomHeight(toolbar, bottom - size, unit, size, unit);
				main.setWidgetLeftRight(toolbar, left, unit, right, unit);
				break;
			case LINE_START:
				if (rtl) {
					main.setWidgetRightWidth(toolbar, right - size, unit, size, unit);
				} else {
					main.setWidgetLeftWidth(toolbar, left - size, unit, size, unit);
				}
				main.setWidgetTopBottom(toolbar, top, unit, bottom, unit);
				break;
			case LINE_END:
				if (rtl) {
					main.setWidgetLeftWidth(toolbar, left - size, unit, size, unit);
				} else {
					main.setWidgetRightWidth(toolbar, right - size, unit, size, unit);
				}
				main.setWidgetTopBottom(toolbar, top, unit, bottom, unit);
				break;
			case CENTER:
			default:
				throw new IllegalArgumentException();
			}
		}
		main.forceLayout();

		for (ToolbarConfiguration action : this.layoutActions) {
			
			toolbar = action.getToolbarAsWidget();
			direction = action.getDockDirection();
			size = action.getSize();
			switch (direction) {
			case NORTH:
				main.setWidgetTopHeight(toolbar, top, unit, size, unit);
				main.setWidgetLeftRight(toolbar, left, unit, right, unit);
				top += size;
				break;
			case EAST:
				main.setWidgetRightWidth(toolbar, right, unit, size, unit);
				main.setWidgetTopBottom(toolbar, top, unit, bottom, unit);
				right += size;
				break;
			case WEST:
				main.setWidgetLeftWidth(toolbar, left, unit, size, unit);
				main.setWidgetTopBottom(toolbar, top, unit, bottom, unit);
				left += size;
				break;
			case SOUTH:
				main.setWidgetBottomHeight(toolbar, bottom, unit, size, unit);
				main.setWidgetLeftRight(toolbar, left, unit, right, unit);
				bottom += size;
				break;
			case LINE_START:
				if (rtl) {
					main.setWidgetRightWidth(toolbar, right, unit, size, unit);
					right += size;
				} else {
					main.setWidgetLeftWidth(toolbar, left, unit, size, unit);
					left += size;
				}
				main.setWidgetTopBottom(toolbar, top, unit, bottom, unit);
				break;
			case LINE_END:
				if (rtl) {
					main.setWidgetLeftWidth(toolbar, left, unit, size, unit);
					left += size;
				} else {
					main.setWidgetRightWidth(toolbar, right, unit, size, unit);
					right += size;
				}
				main.setWidgetTopBottom(toolbar, top, unit, bottom, unit);
				break;
			case CENTER:
			default:
				throw new IllegalArgumentException();
			}
		}
		main.setWidgetTopBottom(container, top, unit, bottom, unit);
		main.setWidgetLeftRight(container, left, unit, right, unit);

		main.animate(duration);
	}

	@Override
	public void addAtPosition(ToolbarConfiguration config) {
		boolean rtl = LocaleInfo.getCurrentLocale().isRTL();
		Direction direction = config.getDockDirection();
		if (joinLineEndToolbars != null && (direction == Direction.LINE_END || (!rtl && direction == Direction.EAST) || (rtl && direction == Direction.WEST))) {
			addToLineEndAggregate(config, rtl);
		} else if (joinLineStartToolbars != null
				&& (direction == Direction.LINE_START || (!rtl && direction == Direction.WEST) || (rtl && direction == Direction.EAST))) {
			addToLineStartAggregate(config, rtl);
		} else {
			this.layoutActions.add(config);
		}
	}

	// TODO join this and the next methos into something more scalable (since
	// they are basicaclly copies of each other)
	private void addToLineStartAggregate(ToolbarConfiguration config, boolean rtl) {
		double size = config.getSize();
		Widget toolbar = config.getToolbarAsWidget();
		String name = config.getName();
		if (lineStartAggregate == null) {
			Widget aggregate;

			switch (joinLineStartToolbars) {
			case STACK:
				aggregate = new StackLayoutPanel(unit);
				break;
			case VERTICAL:
				aggregate = new VerticalLayoutPanel();
				break;
			case HORIZONTAL:
				throw new IllegalArgumentException();
			default:
				aggregate = new StackLayoutPanel(unit);
				break;
			}

			if (rtl) {
				if (lineStartCollapsible) {
					aggregate = new CollapsibleSimplePanel(aggregate, CollapsibleSimplePanel.OPEN, Direction.EAST, unit);
				}
				lineStartAggregate = new ToolbarConfiguration(aggregate, false, ToolbarDirection.EAST, "east-aggregate", size,this, dm);
			} else {
				if (lineStartCollapsible) {
					aggregate = new CollapsibleSimplePanel(aggregate, CollapsibleSimplePanel.OPEN, Direction.WEST, unit);
				}
				lineStartAggregate = new ToolbarConfiguration(aggregate, false, ToolbarDirection.WEST, "west-aggregate", size,this, dm);
			}
			if (lineEndCollapsible) {
				((CollapsibleSimplePanel) aggregate)
						.addResizeHandler(new CollapsibleResizeHandler((CollapsibleSimplePanel) aggregate, lineStartAggregate, size));
			}
			this.layoutActions.add(lineStartAggregate);
		}

		if (lineStartAggregate.getSize() < size) {
			// this way the largest toolbar determines the aggregate size
			lineStartAggregate.setSize(size);
		}

		Widget aggregate = lineStartAggregate.getToolbarAsWidget();

		switch (joinLineStartToolbars) {
		case STACK:
			if (lineStartCollapsible) {
				((StackLayoutPanel) ((CollapsibleSimplePanel) aggregate).getWrapped().asWidget()).add(toolbar, name, 50);
			} else {
				((StackLayoutPanel) aggregate).add(toolbar, name, 50);
			}
			;
			break;
		case VERTICAL:
			if (lineEndCollapsible) {
				((VerticalLayoutPanel) ((CollapsibleSimplePanel) aggregate).getWrapped().asWidget()).add(toolbar);
			} else {
				((VerticalLayoutPanel) aggregate).add(toolbar);
			}
			break;
		case HORIZONTAL:
			throw new IllegalArgumentException();
		default:
			throw new IllegalArgumentException();
		}
	}

	private void addToLineEndAggregate(ToolbarConfiguration config, boolean rtl) {
		double size = config.getSize();
		Widget toolbar = config.getToolbarAsWidget();
		String name = config.getName();
		if (lineEndAggregate == null) {
			Widget aggregate;

			switch (joinLineEndToolbars) {
			case STACK:
				aggregate = new StackLayoutPanel(unit);
				break;
			case VERTICAL:
				aggregate = new VerticalLayoutPanel();
				break;
			case HORIZONTAL:
				throw new IllegalArgumentException();
			default:
				aggregate = new StackLayoutPanel(unit);
				break;
			}

			if (rtl) {
				if (lineEndCollapsible) {
					aggregate = new CollapsibleSimplePanel(aggregate, CollapsibleSimplePanel.OPEN, Direction.WEST, unit);
				}
				lineEndAggregate = new ToolbarConfiguration(aggregate, false, ToolbarDirection.WEST, "west-aggregate", size, this, dm);
			} else {
				if (lineEndCollapsible) {
					aggregate = new CollapsibleSimplePanel(aggregate, CollapsibleSimplePanel.OPEN, Direction.EAST, unit);
				}
				lineEndAggregate = new ToolbarConfiguration(aggregate, false, ToolbarDirection.EAST, "east-aggregate", size,this, dm);
			}
			if (lineEndCollapsible) {
				((CollapsibleSimplePanel) aggregate).addResizeHandler(new CollapsibleResizeHandler((CollapsibleSimplePanel) aggregate, lineEndAggregate, size));
			}
			this.layoutActions.add(lineEndAggregate);
		}

		if (lineEndAggregate.getSize() < size) {
			// this way the largest toolbar determines the aggregate size
			lineEndAggregate.setSize(size);
		}

		Widget aggregate = lineEndAggregate.getToolbarAsWidget();

		switch (joinLineEndToolbars) {
		case STACK:
			if (lineEndCollapsible) {
				((StackLayoutPanel) ((CollapsibleSimplePanel) aggregate).getWrapped().asWidget()).add(toolbar, name, 50);
			} else {
				((StackLayoutPanel) aggregate).add(toolbar, name, 50);
			}
			break;
		case VERTICAL:
			if (lineEndCollapsible) {
				((VerticalLayoutPanel) ((CollapsibleSimplePanel) aggregate).getWrapped().asWidget()).add(toolbar);
			} else {
				((VerticalLayoutPanel) aggregate).add(toolbar);
			}
			break;
		case HORIZONTAL:
			throw new IllegalArgumentException();
		default:
			throw new IllegalArgumentException();
		}
	}

	public ToolbarJoining getJoinLineStartToolbars() {
		return joinLineStartToolbars;
	}

	public void setJoinLineStartToolbars(ToolbarJoining joinLineStartToolbars) {
		this.joinLineStartToolbars = joinLineStartToolbars;
	}

	public void setLineStartCollapsible(boolean lineStartCollapsible) {
		this.lineStartCollapsible = lineStartCollapsible;
	}

	public void setLineEndCollapsible(boolean lineEndCollapsible) {
		this.lineEndCollapsible = lineEndCollapsible;
	}

	public void setJoinLineEndToolbars(ToolbarJoining joinLineEndToolbars) {
		this.joinLineEndToolbars = joinLineEndToolbars;
	}

	public ToolbarJoining getJoinLineEndToolbars() {
		return joinLineEndToolbars;
	}

	public void showWidgetInAggregatesIfAvailable(Widget asWidget) {

		if (lineEndAggregate != null) {
			Widget aggregate = lineEndAggregate.getToolbarAsWidget();

			switch (joinLineEndToolbars) {
			case STACK:
				StackLayoutPanel panel;
				if (lineEndCollapsible) {
					((CollapsibleSimplePanel) aggregate).open();
					panel = ((StackLayoutPanel) ((CollapsibleSimplePanel) aggregate).getWrapped().asWidget());
				} else {
					panel = (StackLayoutPanel) aggregate;
				}
				panel.showWidget(asWidget);
				break;
			default:
				throw new IllegalArgumentException();
			}
		}
		if (lineStartAggregate != null) {
			Widget aggregate = lineStartAggregate.getToolbarAsWidget();

			switch (joinLineEndToolbars) {
			case STACK:
				StackLayoutPanel panel;
				if (lineEndCollapsible) {
					((CollapsibleSimplePanel) aggregate).open();
					panel = ((StackLayoutPanel) ((CollapsibleSimplePanel) aggregate).getWrapped().asWidget());
				} else {
					panel = (StackLayoutPanel) aggregate;
				}
				panel.showWidget(asWidget);
				break;
			case VERTICAL:
			case HORIZONTAL:
				if (lineEndCollapsible) {
					((CollapsibleSimplePanel) aggregate).open();
				}
				break;
			default:
				throw new IllegalArgumentException();
			}
		}

	}

	@Override
	public void setUnit(Unit u) {
		this.unit=u;
	}

	public Unit getUnit() {
		return unit;
	}

}
