package com.wrupple.muba.desktop.client.activity.widgets.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.services.logic.MultipartFormActionUrlService;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.PersistentImageMetadata;

public class CatalogFileUpload extends Composite implements HasValue<String> {

	private static CatalogFileUploadUiBinder uiBinder = GWT.create(CatalogFileUploadUiBinder.class);

	interface CatalogFileUploadUiBinder extends
			UiBinder<Widget, CatalogFileUpload> {
	}
	
	private class NewImageSelected extends DataCallback<JsTransactionApplicationContext>{

		@Override
		public void execute() {
			JsArray<JsCatalogEntry> rawOutput = result.getUserOutputAsCatalogEntryArray();
			if(rawOutput!=null && rawOutput.length()>0){
				JsCatalogEntry output = rawOutput.get(0);
				String selectedImage = output.getId();
				setValue(selectedImage, true);
			}
		}
		
	}

	private class CompleteHandler implements SubmitCompleteHandler {

		@Override
		public void onSubmitComplete(SubmitCompleteEvent event) {
			// Response an be either a JSON download descriptor, or a plain
			// string with the id of the file
			String newFile = event.getResults();
			JsArrayString array = calculateNewValue(newFile);
			String newValue = null;
			if (array.length() == 0) {
				newValue = null;
			} else {
				newValue = array.get(0);
			}
			setValue(newValue, true);
			submit.setEnabled(false);
			submit.setVisible(false);
			headerText.setText("Upload Success!");
		}

	}
	
	private class AttachmentChangeHandler implements ChangeHandler{

		@Override
		public void onChange(ChangeEvent event) {
			selectAnother.setVisible(false);
			submit.setVisible(true);
			submit.setEnabled(true);
			showEmptyPreview();
		}
		
	}

	private ImageTemplate imageTemplate;
	private ProcessContextServices contextServices;
	private ContentManagementSystem cms;
	protected String value;
	@UiField FormPanel form;
	@UiField Button submit;
	@UiField Button selectAnother;
	@UiField InlineLabel headerText;
	@UiField FileUpload uploader;
	@UiField DivElement preview;
	
	

	@Inject
	public CatalogFileUpload(ImageTemplate imageTemplate,MultipartFormActionUrlService actionUrl,CatalogUserInterfaceMessages cc, ContentManagementSystem cms) {
		super();
		this.cms=cms;
		initWidget(uiBinder.createAndBindUi(this));
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		try {
			actionUrl.setUploadUrl(form);
		} catch (Exception e) {
			Window.alert(e.getMessage());
		}
		form.addSubmitCompleteHandler(new CompleteHandler());
		uploader.setName("file");
		uploader.addChangeHandler(new AttachmentChangeHandler());
		submit.setText(cc.uploadImageProcess());
		submit.setVisible(false);
		this.imageTemplate=imageTemplate;
		headerText.setText(cc.chooseFile());
		selectAnother.setText(cc.selectFromGallery());
	}




	public void setAction(String action) {
		form.setAction(action);
	}

	public String getFileName() {
		return uploader.getFilename();
	}

	public static native JsArrayString calculateNewValue(String newFile) /*-{
																		return eval(newFile);
																		}-*/;

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String selectedImage) {
		if(selectedImage==null|| selectedImage.isEmpty()){
			this.value = null;
			showEmptyPreview();
		}else{
			this.value = selectedImage;
			SafeHtml safeHtml = imageTemplate.smallImageOutput(selectedImage);
			preview.setInnerHTML(safeHtml.asString());
		}
		
	}
	
	private void showEmptyPreview() {
		preview.setInnerHTML(imageTemplate.noImageOutput().asString());
	}

	@Override
	public void setValue(String selectedImage, boolean fireEvents) {
		setValue(selectedImage);
		if (fireEvents) {
			ValueChangeEvent.fire(this, getValue());
		}
	}

	@UiHandler("submit")
	public void submit(ClickEvent ev) {
		form.submit();
	}
	@UiHandler("selectAnother")
	public void changeImage(ClickEvent ev) {
		ContentManager<JsCatalogEntry> imageManager = cms.getContentManager(PersistentImageMetadata.CATALOG);
		Process<JsTransactionApplicationContext, JsTransactionApplicationContext> imageSelectionProcess = imageManager.getSelectionProcess(contextServices, false, false);
		JsTransactionApplicationContext input= JsTransactionApplicationContext.createObject().cast();
		StateTransition<JsTransactionApplicationContext> callback=new NewImageSelected();
		contextServices.getProcessManager().processSwitch(imageSelectionProcess, selectAnother.getText(), input, callback, contextServices);
	}


	public CatalogFileUpload initialize(ProcessContextServices contextServices) {
		this.contextServices=contextServices;
		return this;
	}
	

}
