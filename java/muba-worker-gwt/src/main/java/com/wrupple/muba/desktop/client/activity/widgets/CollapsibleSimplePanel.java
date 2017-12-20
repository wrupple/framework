package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;

public class CollapsibleSimplePanel extends ResizeComposite implements HasResizeHandlers {

	public static String OPEN = "open";
	public static String CLOSED = "closed";
	
	/*
	 * TODO REPLACE THIS WITH A PROPER STYLE NAME SHEET (GWT does provide something like that right?)
	 */
	public static String DIRECTIONAL_CONTROL_STYLE = "directionalControl";
	
	class ToggleState implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			int controlSize = getControllerSize(unit);
			if(OPEN.equals(getCurrentState())){
				setCurrentState(CLOSED,controlSize);
			}else{
				setCurrentState(OPEN,controlSize);
			}
			fireResize();
		}
		
	}
	
	private final LayoutPanel main;
	private final Label control;
	private String currentState;
	protected Unit unit;
	
	private final IsWidget wrapped;
	protected Direction direction;
	

	public CollapsibleSimplePanel(IsWidget wrapped,String currentState,Direction direction,Unit unit) {
		super();
		this.unit=unit;
		this.direction=direction;
		this.setCurrentState(currentState);
		this.wrapped = wrapped;
		main = new LayoutPanel();
		control= new Label();
		control.addClickHandler( new ToggleState());
		control.addStyleName(DIRECTIONAL_CONTROL_STYLE);
		main.add(control);
		main.add(wrapped);
		main.addStyleName("collapsiblePanelWrapper");
		initWidget(main);
		int controlSize = getControllerSize(unit);
		setCurrentState(currentState,controlSize);
	}
	
	public void fireResize() {
		ResizeEvent.fire(this, getOffsetWidth(), getOffsetHeight());
	}
	

	

	private void setCurrentState(String newState, int controlSize) {
		boolean open = OPEN.equals(newState);
		if(!(open||CLOSED.equals(newState))){
			throw new IllegalArgumentException("Unknown state "+newState);
		}
		if(open){
			control.removeStyleName("collapsible-control-closed");
			control.addStyleName("collapsible-control-open");
		}else{
			control.removeStyleName("collapsible-control-open");
			control.addStyleName("collapsible-control-closed");
		}
		Direction direction = getDirection();
		switch (direction) {
		case NORTH:
			if (open) {
				main.setWidgetTopBottom(wrapped, 0, unit, controlSize, unit);
				main.setWidgetBottomHeight(control, 0, unit, controlSize, unit);
				control.setText("↑");
			} else {
				main.setWidgetTopHeight(wrapped, 0, unit, 0, unit);
				main.setWidgetBottomHeight(control, 0, unit, controlSize, unit);
				control.setText("↓");
			}
			break;
		case EAST:
			if (open) {
				main.setWidgetLeftRight(wrapped, controlSize, unit, 0, unit);
				main.setWidgetLeftWidth(control, 0, unit, controlSize, unit);
				control.setText(">");
			} else {
				main.setWidgetRightWidth(wrapped, 0, unit, 0, unit);
				main.setWidgetLeftWidth(control, 0, unit, controlSize, unit);
				control.setText("<");
			}
			break;
		case WEST:
			if (open) {
				main.setWidgetLeftRight(wrapped, 0, unit, controlSize, unit);
				main.setWidgetRightWidth(control, 0, unit, controlSize, unit);
				control.setText("<");
			} else {
				main.setWidgetLeftWidth(wrapped, 0, unit, 0, unit);
				main.setWidgetRightWidth(control, 0, unit, controlSize, unit);
				control.setText(">");
			}
			break;
		case SOUTH:
			if (open) {
				main.setWidgetTopBottom(wrapped, controlSize, unit, 0, unit);
				main.setWidgetTopHeight(control, 0, unit, controlSize, unit);
				control.setText("↓");
			} else {
				main.setWidgetBottomHeight(wrapped, 0, unit, 0, unit);
				main.setWidgetTopHeight(control, 0, unit, controlSize, unit);
				control.setText("↑");
			}
			break;
		case LINE_START:
			if(LocaleInfo.getCurrentLocale().isRTL()){
				if (open) {
					main.setWidgetLeftRight(wrapped, controlSize, unit, 0, unit);
					main.setWidgetLeftWidth(control, 0, unit, controlSize, unit);
					control.setText(">");
				} else {
					main.setWidgetRightWidth(wrapped, 0, unit, 0, unit);
					main.setWidgetLeftWidth(control, 0, unit, controlSize, unit);
					control.setText("<");
				}
			}else{
				if (open) {
					main.setWidgetLeftRight(wrapped, 0, unit, controlSize, unit);
					main.setWidgetRightWidth(control, 0, unit, controlSize, unit);
					control.setText("<");
				} else {
					main.setWidgetLeftWidth(wrapped, 0, unit, 0, unit);
					main.setWidgetRightWidth(control, 0, unit, controlSize, unit);
					control.setText(">");
				}
			}
			break;
		case LINE_END:
			if(LocaleInfo.getCurrentLocale().isRTL()){
				if (open) {
					main.setWidgetLeftRight(wrapped, 0, unit, controlSize, unit);
					main.setWidgetRightWidth(control, 0, unit, controlSize, unit);
					control.setText("<");
				} else {
					main.setWidgetLeftWidth(wrapped, 0, unit, 0, unit);
					main.setWidgetRightWidth(control, 0, unit, controlSize, unit);
					control.setText(">");
				}
			}else{
				if (open) {
					main.setWidgetLeftRight(wrapped, controlSize, unit, 0, unit);
					main.setWidgetLeftWidth(control, 0, unit, controlSize, unit);
					control.setText(">");
				} else {
					main.setWidgetRightWidth(wrapped, 0, unit, 0, unit);
					main.setWidgetLeftWidth(control, 0, unit, controlSize, unit);
					control.setText("<");
				}
			}
			break;
		}
		currentState=newState;
	}
	
	public int getControllerSize(Unit unit){
		int regreso=0;
		switch (unit) {
		case PX:
			regreso=20;
			break;
		case PCT:
			regreso=5;
			break;
		case EM:
			regreso=1;
			break;
		case EX:regreso=2;
			break;
		case PT:regreso=2;
			break;
		case PC:regreso=2;
			break;
		case IN:
			regreso=1;
			break;
		case CM:
			regreso=1;
			break;
		case MM:
			regreso=8;
			break;
		}
		return regreso;
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}
	
	protected Direction getDirection() {
		return direction;
	}

	public IsWidget getWrapped() {
		return wrapped;
	}

	public boolean isOpen() {
		return OPEN.equals(getCurrentState());
	}
	
	public void open(){
		if(!isOpen()){
			int controlSize = getControllerSize(unit);
			setCurrentState(OPEN,controlSize);
			fireResize();
		}
	}
	
	public void close(){
		if(isOpen()){
			int controlSize = getControllerSize(unit);
			setCurrentState(CLOSED,controlSize);
			fireResize();
		}
	}

	public String getCurrentState() {
		return currentState;
	}

	private void setCurrentState(String currentState) {
		this.currentState = currentState;
	}
	
}
