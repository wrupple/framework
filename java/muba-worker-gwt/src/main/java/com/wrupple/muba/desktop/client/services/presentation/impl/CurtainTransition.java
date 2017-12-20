package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class CurtainTransition implements PanelTransition {

	public static class WidgetData {
		public Widget brother;
		public Orientation orientation;
		public boolean isInView;

		public WidgetData(Orientation o, boolean inView) {
			orientation = o;
			isInView = inView;
		}

		public WidgetData(Orientation o, boolean inView, Widget brother) {
			orientation = o;
			isInView = inView;
			this.brother = brother;
		}

	}

	private Orientation from;
	private Widget child;
	private Widget brother;
	private WidgetData widgetData;
	private double height;
	private Unit heightUnit;
	private double width;
	private Unit widthUnit;
	private LayoutPanel parent;

	public CurtainTransition(Orientation from, Widget child, Widget brother, LayoutPanel parent, double heightOrWidthInPixels, Unit px) {
		widgetData =new WidgetData(from,false,brother);
		this.from = from;
		this.child = child;
		this.brother = brother;
		this.parent = parent;
		
		if (from == Orientation.TOP || from == Orientation.BOTTOM) {
			this.setFinalHeight(heightOrWidthInPixels, px);
		} else {
			this.setFinalWidth(heightOrWidthInPixels, px);
		}
	}

	public CurtainTransition(Orientation from, Widget child, Widget brother, LayoutPanel parent) {
		this(from, child, brother, parent,100, Unit.PCT);
	}
	

	@Override
	public void onBeforeAnimate() {
	}

	@Override
	public void setInitialState() {
		switch (from) {
		case TOP:
			parent.setWidgetTopHeight(child, 0, Unit.PX, 0, Unit.PX);
			break;
		case BOTTOM:
			parent.setWidgetBottomHeight(child, 0, Unit.PX, 0, Unit.PX);
			break;
		case RIGHT:
			parent.setWidgetRightWidth(child, 0, Unit.PX, 0, Unit.PX);
			break;
		case LEFT:
			parent.setWidgetLeftWidth(child, 0, Unit.PX, 0, Unit.PX);
			break;
		}

		if (brother != null) {
			switch (from) {
			case TOP:
				parent.setWidgetTopBottom(brother, 0, Unit.PX, 0, Unit.PX);
				break;
			case BOTTOM:
				parent.setWidgetTopBottom(brother, 0, Unit.PX, 0, Unit.PX);
				break;
			case RIGHT:
				parent.setWidgetLeftRight(brother, 0, Unit.PX, 0, Unit.PX);
				break;
			case LEFT:
				parent.setWidgetLeftRight(brother, 0, Unit.PX, 0, Unit.PX);
				break;
			}
		}

	}

	@Override
	public void setFinalState() {
		switch (from) {
		case TOP:
			parent.setWidgetTopHeight(child, 0, Unit.PX, height, heightUnit);
			break;
		case BOTTOM:
			parent.setWidgetBottomHeight(child, 0, Unit.PX, height, heightUnit);
			break;
		case RIGHT:
			parent.setWidgetRightWidth(child, 0, Unit.PX, width, widthUnit);
			break;
		case LEFT:
			parent.setWidgetLeftWidth(child, 0, Unit.PX, width, widthUnit);
			break;
		}
		if (brother != null) {
			switch (from) {
			case TOP:
				parent.setWidgetTopBottom(brother, height, heightUnit, 0, Unit.PX);
				break;
			case BOTTOM:
				parent.setWidgetTopBottom(brother, 0, Unit.PX, height, heightUnit);
				break;
			case RIGHT:
				parent.setWidgetLeftRight(brother, 0, Unit.PX, width, widthUnit);
				break;
			case LEFT:
				parent.setWidgetLeftRight(brother, width, widthUnit, 0, Unit.PX);
				break;
			}
		}

	}

	@Override
	public void onAnimationFinished() {
		this.widgetData.isInView=true;
	}

	protected void setFinalWidth(double width, Unit widthUnit) {
		this.width = width;
		this.widthUnit = widthUnit;
	}

	protected void setFinalHeight(double height, Unit heightUnit) {
		this.height = height;
		this.heightUnit = heightUnit;
	}

	public Orientation getOrientation() {
		return from;
	}

	public void setOrientation(Orientation from) {
		this.from = from;
	}

	public void slideOut() {
		parent.forceLayout();
		switch (widgetData.orientation) {
		case TOP:
			parent.setWidgetTopHeight(child, 0, Unit.PX, 0, Unit.PX);
			if (widgetData.brother != null) {
				parent.setWidgetTopBottom(widgetData.brother, 0, Unit.PX, 0, Unit.PX);
			}
			break;
		case BOTTOM:
			parent.setWidgetBottomHeight(child, 0, Unit.PX, 0, Unit.PX);
			break;
		case LEFT:
			parent.setWidgetLeftWidth(child, 0, Unit.PX, 0, Unit.PX);
			break;
		case RIGHT:
			parent.setWidgetRightWidth(child, 0, Unit.PX, 0, Unit.PX);
			break;
		}
		parent.animate(500);
		widgetData.isInView = false;
	}

	public boolean isCurrentlyIn() {
		return widgetData.isInView;
	}

	public void slideIn(int duration) {
		this.onBeforeAnimate();
		this.setInitialState();
		parent.forceLayout();
		this.setFinalState();
		parent.animate(duration);
		this.onAnimationFinished();
	}


}
