package com.wrupple.muba.desktop.domain;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.activity.widgets.CollapsibleSimplePanel;
import com.wrupple.muba.desktop.client.activity.widgets.panels.PanelWithToolbarLayoutDelegate;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTaskToolbarDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.shared.domain.PanelTransformationConfig;
import com.wrupple.muba.worker.shared.event.EntriesDeletedEvent;
import com.wrupple.muba.worker.shared.event.EntriesRetrivedEvent;
import com.wrupple.muba.worker.shared.event.EntryCreatedEvent;
import com.wrupple.muba.worker.shared.event.EntryUpdatedEvent;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow.ToolbarDirection;
import com.wrupple.muba.worker.shared.widgets.Toolbar;

public class ToolbarConfiguration {
	
	public static class CollapsibleToolbarWrapper extends CollapsibleSimplePanel  implements Toolbar{
		
		
		public CollapsibleToolbarWrapper(Toolbar wrapped, String currentState) {
			super(wrapped, currentState, Direction.EAST,Unit.PX);
		}

		@Override
		public void fireEvent(GwtEvent<?> event) {
			((Toolbar) getWrapped()).fireEvent(event);
		}


		@Override
		public void applyAlterations(PanelTransformationConfig properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionApplicationContext contextParamenters) {
			((Toolbar) getWrapped()).applyAlterations(properties, contextServices, eventBus, contextParamenters);
		}


		@Override
		public void setValue(JavaScriptObject value) {
			((Toolbar) getWrapped()).setValue(value);
		}


		@Override
		public JavaScriptObject getValue() {
			return ((Toolbar) getWrapped()).getValue();
		}


		@Override
		public void initialize(JsTaskToolbarDescriptor toolbarDescriptor, JsProcessTaskDescriptor parameter, JsTransactionApplicationContext contextParameters,
				EventBus bus, ProcessContextServices contextServices) {
			((Toolbar) getWrapped()).initialize(toolbarDescriptor, parameter, contextParameters, bus, contextServices);
		}


		@Override
		public void setType(String s) {
			((Toolbar) getWrapped()).setType(s);
		}

		@Override
		public void onEntriesDeleted(EntriesDeletedEvent entriesDeletedEvent) {
			((Toolbar) getWrapped()).onEntriesDeleted(entriesDeletedEvent);
		}

		@Override
		public void onEntriesRetrived(EntriesRetrivedEvent e) {
			((Toolbar) getWrapped()).onEntriesRetrived(e);
		}

		@Override
		public void onEntryUpdated(EntryUpdatedEvent entryUpdatedEvent) {
			((Toolbar) getWrapped()).onEntryUpdated(entryUpdatedEvent);
		}

		@Override
		public void onEntryCreated(EntryCreatedEvent entryCreatedEvent) {
			((Toolbar) getWrapped()).onEntryCreated(entryCreatedEvent);
		}

		@Override
		public HandlerRegistration addValueChangeHandler(
				ValueChangeHandler<JavaScriptObject> handler) {
			return ((Toolbar) getWrapped()).addValueChangeHandler(handler);
		}
	}
	
	final Widget toolbar;
	final boolean collapsible;
	final CollapsibleToolbarWrapper wrapper;
	final ToolbarDirection direction;
	final String name;
	double size;
	private PanelWithToolbarLayoutDelegate layout;
	private DesktopManager dm;

	public ToolbarConfiguration(Widget toolbar, boolean collapsible, ToolbarDirection direction, String name, double size, PanelWithToolbarLayoutDelegate layout, DesktopManager dm) {
		super();
		this.toolbar = toolbar;
		this.collapsible = collapsible;
		this.direction = direction;
		if (collapsible) {
			// TODO make initial state configurable (pass properties object)
			wrapper = new CollapsibleToolbarWrapper((Toolbar) toolbar, CollapsibleToolbarWrapper.OPEN);
		} else {
			wrapper = null;
		}
		this.name = name;
		this.size = size;
		this.layout=layout;
		this.dm=dm;
	}

	public Widget getToolbarAsWidget() {
		return toolbar;
	}

	public Toolbar getToolbar() {
		if (isCollapsible()) {
			return wrapper;
		} else {
			return (Toolbar) toolbar;
		}
	}

	public boolean isCollapsible() {
		return collapsible;
	}

	public CollapsibleToolbarWrapper getWrapper() {
		return wrapper;
	}

	public ToolbarDirection getDirection() {
		return direction;
	}

	public String getName() {
		return name;
	}

	public double getSize() {
		if(wrapper!=null){
			int controlSize = wrapper.getControllerSize(layout.getUnit());
			if(CollapsibleSimplePanel.OPEN.equals(wrapper.getCurrentState())){
				return size+controlSize;
			}else{
				return controlSize;
			}
		}else{
			return size;
		}
	}


	public void setSize(double size) {
		this.size=size;
	}

	
	public Direction getDockDirection(){
		return getPanelDirection(getDirection());
	}
	private Direction getPanelDirection(ToolbarDirection toolbarDirection) {
		switch (toolbarDirection) {
		case CENTER:
			return Direction.CENTER;
		case EAST:
			return Direction.EAST;
		case LINE_END:
			return Direction.LINE_END;
		case LINE_START:
			return Direction.LINE_START;
		case NORTH:
			return Direction.NORTH;
		case SOUTH:
			return Direction.SOUTH;
		case WEST:
			return Direction.WEST;
		case WIDE_END:
			if (dm==null||dm.isLandscape()) {
				return Direction.SOUTH;
			} else {
				if(LocaleInfo.getCurrentLocale().isRTL()){
					return Direction.WEST;
				}else{
					return Direction.EAST;
				}
			}
		case WIDE_START:
			if (dm==null||dm.isLandscape()) {
				return Direction.NORTH;
			} else {
				if(LocaleInfo.getCurrentLocale().isRTL()){
					return Direction.EAST;
				}else{
					return Direction.WEST;
				}
			}
		case SHORT_END:
			if (dm==null||dm.isLandscape()) {
				if(LocaleInfo.getCurrentLocale().isRTL()){
					return Direction.WEST;
				}else{
					return Direction.EAST;
				}
			} else {
				return Direction.SOUTH;
			}
		case SHORT_START:
			if (dm==null||dm.isLandscape()) {
				if(LocaleInfo.getCurrentLocale().isRTL()){
					return Direction.EAST;
				}else{
					return Direction.WEST;
				}
			} else {
				return Direction.NORTH;
			}
		default:
			throw new IllegalArgumentException();
		}
	}
}