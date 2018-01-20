package com.wrupple.muba.desktop.client.activity.widgets.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
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

public class ManzanaToolbar extends Composite implements Toolbar{
	public static final String CONTROL_STYLE_NAME = "view-control";
	private static ManzanaToolbarUiBinder uiBinder = GWT.create(ManzanaToolbarUiBinder.class);

	interface ManzanaToolbarUiBinder extends UiBinder<Widget, ManzanaToolbar> {
	}
	
	
	@UiField
	public Label title;
	@UiField
	Anchor back;
	@UiField
	SimplePanel backWrapper;
	@UiField
	Anchor forward;
	@UiField
	SimplePanel forwardWrapper;
	@UiField
	public TableCellElement leftControls;
	@UiField
	public TableCellElement rightControls;
	
	public ManzanaToolbar() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		this.back.setVisible(false);
		this.forward.setVisible(false);
	}

	public void setTitlebarText(String title) {
		this.title.setText(title);
	}
	

	/**
	 * in a LTR enviroment, this would set the left side controls
	 * 
	 * @param inheritedControls
	 */
	public void setBackControls(Widget inheritedControls) {
		this.backWrapper.setWidget(inheritedControls);
		setAnchorStyle(inheritedControls);
	}
	
	public void setForwardTitle(String title2) {
		this.forward.setVisible(true);
		this.forward.setText(title2);
	}
	
	public void hideBackButton() {
		this.back.setVisible(false);
	}

	public void setBackTitle(String title2) {
		this.back.setVisible(true);
		this.back.setText(title2);
	}

	public void hideForwardButton() {
		this.forward.setVisible(false);
	}

	public void setForwardControls(Widget inheritedControls) {
		this.forwardWrapper.setWidget(inheritedControls);
		setAnchorStyle(inheritedControls);
	}


	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	

	public void clearControls() {
		this.forwardWrapper.clear();
		this.backWrapper.clear();
	}


	
	private void setAnchorStyle(Widget inheritedControls) {
		if (inheritedControls != null && inheritedControls instanceof Anchor) {
			inheritedControls.addStyleName(CONTROL_STYLE_NAME);
		}
	}


	
	public static String buttonStyle(){
		return CONTROL_STYLE_NAME;
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
