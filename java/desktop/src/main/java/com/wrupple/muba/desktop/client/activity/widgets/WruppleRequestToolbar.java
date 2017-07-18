package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.event.EntriesDeletedEvent;
import com.wrupple.muba.desktop.client.event.EntriesRetrivedEvent;
import com.wrupple.muba.desktop.client.event.EntryCreatedEvent;
import com.wrupple.muba.desktop.client.event.EntryUpdatedEvent;
import com.wrupple.muba.desktop.client.event.NewVegetateRequestEvent;
import com.wrupple.muba.desktop.client.event.VegetateRequestFailureEvent;
import com.wrupple.muba.desktop.client.event.VegetateRequestSuccessEvent;
import com.wrupple.muba.desktop.client.services.logic.impl.ActivityVegetateEventHandler;
import com.wrupple.muba.desktop.domain.PanelTransformationConfig;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogActionRequest;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTaskToolbarDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.VegetateUserException;

public class WruppleRequestToolbar extends Composite implements RequestToolbar {

	FlowPanel main;
	private StorageManager sm;

	@Inject
	public WruppleRequestToolbar(StorageManager sm) {
		super();
		this.sm = sm;
		main = new FlowPanel();
		initWidget(main);
		main.addStyleName("wrupple-request-toolbar");
	}

	@Override
	public void onNewVegetateRequest(NewVegetateRequestEvent e) {
		int requestId = e.getRequestNumber();
		// CatalogServiceManifest.ServiceName
		JsCatalogActionRequest cargo = (JsCatalogActionRequest) e.getCargo();
		if (cargo != null) {
			String action = cargo.getAction();
			String catalog = cargo.getCatalog();
			if (catalog == null) {
				catalog = "catalog";
			}

			addRequest(requestId, action, catalog);
		}
	}

	@Override
	public void onRequestSuccessful(VegetateRequestSuccessEvent e) {
		int requestId = e.getRequestNumber();
		removeSuccessfulRequest(requestId);
	}

	@Override
	public void onRequestFailed(VegetateRequestFailureEvent e) {
		int errorCode = e.getExceptionOverlay() == null ? 0 : e.getExceptionOverlay().getErrorCode();
		int requestId = e.getRequestNumber();
		if (errorCode ==VegetateUserException.USER_UNKNOWN) {
			//FIXME Toolbar should not hable exceptions, but let others catch the failure events
		} else {
			String exceptionMessage = getExceptionMessage(e);
			if (Window.confirm("A request has reported an error: \n" + exceptionMessage + " \n do you wish to retry the request?")) {
				ActivityVegetateEventHandler.retry(e,sm);
			}
		}

		removeFailedRequest(requestId);
	}



	private String getExceptionMessage(VegetateRequestFailureEvent e) {
		if (e.getException() != null) {
			return e.getException().getMessage();
		} else if (e.getExceptionOverlay() != null) {
			return e.getExceptionOverlay().getDetailMessage();
		}
		return "";
	}

	private void removeFailedRequest(int requestId) {
		removeSuccessfulRequest(requestId);
	}

	private void removeSuccessfulRequest(int requestId) {
		// TODO sometime this gets called more that once for a given request id
		RequestToolbarToken token = findRequestToken(requestId);
		if (token == null) {
		} else {
			main.remove(token);
		}
	}

	private void addRequest(int requestId, String service, String target) {
		RequestToolbarToken token = new RequestToolbarToken(requestId, service, target);
		main.add(token);
	}

	private RequestToolbarToken findRequestToken(int requestId) {
		RequestToolbarToken token = null;
		for (Widget w : main) {
			if (((RequestToolbarToken) w).getRequestId() == requestId) {
				token = (RequestToolbarToken) w;
			}
		}
		return token;
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public void initialize(JsTaskToolbarDescriptor toolbarDescriptor, JsProcessTaskDescriptor parameter, JsTransactionApplicationContext contextParameters,
			EventBus bus, ProcessContextServices contextServices) {

	}

	@Override
	public void setType(String s) {

	}

	@Override
	public void applyAlterations(PanelTransformationConfig properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionApplicationContext contextParamenters) {

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
