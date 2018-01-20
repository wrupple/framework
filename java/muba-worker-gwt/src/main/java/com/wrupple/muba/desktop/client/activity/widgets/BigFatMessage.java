package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTaskToolbarDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.shared.domain.ReconfigurationBroadcastEvent;
import com.wrupple.muba.worker.shared.event.EntriesDeletedEvent;
import com.wrupple.muba.worker.shared.event.EntriesRetrivedEvent;
import com.wrupple.muba.worker.shared.event.EntryCreatedEvent;
import com.wrupple.muba.worker.shared.event.EntryUpdatedEvent;
import com.wrupple.muba.worker.shared.widgets.Toolbar;

public class BigFatMessage extends Composite implements Toolbar {

	private static BigFatMessageUiBinder uiBinder = GWT
			.create(BigFatMessageUiBinder.class);

	interface BigFatMessageUiBinder extends UiBinder<Widget, BigFatMessage> {
	}

	@UiField
	ParagraphElement p;

	public BigFatMessage(String message) {
		initWidget(uiBinder.createAndBindUi(this));
		if (message != null) {
			p.setInnerText(message);
		}
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return null;
	}

	public void setTitlebarText(String title) {
		p.setInnerText(title);
	}

	@Override
    public void applyAlterations(ReconfigurationBroadcastEvent properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionApplicationContext contextParamenters) {

    }

	@Override
	public void setValue(JavaScriptObject value) {
		
	}

	@Override
	public JavaScriptObject getValue() {
		return null;
	}

	@Override
	public void initialize(JsTaskToolbarDescriptor toolbarDescriptor, JsProcessTaskDescriptor parameter, JsTransactionApplicationContext contextParameters,
			EventBus bus, ProcessContextServices contextServices) {
		
	}

	@Override
	public void setType(String s) {
		
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
