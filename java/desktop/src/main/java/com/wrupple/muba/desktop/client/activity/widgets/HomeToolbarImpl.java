package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.HomeToolbar;
import com.wrupple.muba.desktop.client.event.ContextSwitchEvent;
import com.wrupple.muba.desktop.client.event.EntriesDeletedEvent;
import com.wrupple.muba.desktop.client.event.EntriesRetrivedEvent;
import com.wrupple.muba.desktop.client.event.EntryCreatedEvent;
import com.wrupple.muba.desktop.client.event.EntryUpdatedEvent;
import com.wrupple.muba.desktop.client.event.NewVegetateRequestEvent;
import com.wrupple.muba.desktop.client.event.ProcessExitEvent;
import com.wrupple.muba.desktop.client.event.ProcessSwitchEvent;
import com.wrupple.muba.desktop.client.event.VegetateRequestFailureEvent;
import com.wrupple.muba.desktop.client.event.VegetateRequestSuccessEvent;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.PanelTransformationConfig;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTaskToolbarDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

@Singleton
public class HomeToolbarImpl extends ResizeComposite implements HomeToolbar {
	public BreadcrumbToolbar history;

	public RequestToolbar monitor;

	LayoutPanel panel;

	@Inject
	public HomeToolbarImpl(BreadcrumbToolbar history, RequestToolbar monitor) {
		super();
		this.history = history;
		this.monitor = monitor;
		panel = new LayoutPanel();
		panel.add(history);
		panel.add(monitor);
		panel.setWidgetRightWidth(monitor, 0, Unit.PX, 60, Unit.PCT);
		panel.setWidgetLeftWidth(history, 0, Unit.PX, 40, Unit.PCT);
		initWidget(panel);
		panel.addStyleName("activity-toolbar");
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}


	@Override
	public void onProcessSwitch(ProcessSwitchEvent e) {
		history.onProcessSwitch(e);
	}

	@Override
	public void onContextSwitch(ContextSwitchEvent e) {
		history.onContextSwitch(e);
	}

	@Override
	public void onProcessDone(ProcessExitEvent e) {
		history.onProcessDone(e);
	}

	@Override
	public void onNewVegetateRequest(NewVegetateRequestEvent e) {
		monitor.onNewVegetateRequest(e);
	}

	@Override
	public void onRequestSuccessful(VegetateRequestSuccessEvent e) {
		monitor.onRequestSuccessful(e);
	}

	@Override
	public void onRequestFailed(VegetateRequestFailureEvent e) {
		monitor.onRequestFailed(e);
	}

	@Override
	public void initialize(JsTaskToolbarDescriptor toolbarDescriptor, JsProcessTaskDescriptor parameter, JsTransactionActivityContext contextParameters,
			EventBus bus, ProcessContextServices contextServices) {
		
	}

	@Override
	public void setType(String s) {
		
	}

	@Override
	public void applyAlterations(PanelTransformationConfig properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionActivityContext contextParamenters) {
		
	}

	@Override
	public void setValue(JavaScriptObject value) {
		
	}

	@Override
	public JavaScriptObject getValue() {
		return null;
	}

	@Override
	public void setSize(int height) {
		history.setSize(height);
		monitor.setSize(height);
	}

	@Override
	public void onPlaceChange(DesktopPlace place, JsApplicationItem item) {
		history.onPlaceChange(place,item);
	}

	@Override
	public void onEntriesDeleted(EntriesDeletedEvent entriesDeletedEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntriesRetrived(EntriesRetrivedEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryUpdated(EntryUpdatedEvent entryUpdatedEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryCreated(EntryCreatedEvent entryCreatedEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<JavaScriptObject> handler) {
		// TODO Auto-generated method stub
		return null;
	}

}