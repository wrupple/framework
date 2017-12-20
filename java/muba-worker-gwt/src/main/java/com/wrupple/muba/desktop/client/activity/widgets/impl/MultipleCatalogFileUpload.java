package com.wrupple.muba.desktop.client.activity.widgets.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.services.logic.MultipartFormActionUrlService;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;

public class MultipleCatalogFileUpload extends Composite implements HasValue<JsArray<MultipleCatalogFileUpload.Value>> {
	private static MultipleCatalogFileUploadUiBinder uiBinder = GWT.create(MultipleCatalogFileUploadUiBinder.class);

	@SuppressWarnings("serial")
	public final static class Value extends JsCatalogKey {
        protected Value() {
        }
    }

	interface MultipleCatalogFileUploadUiBinder extends UiBinder<Widget, MultipleCatalogFileUpload> {
	}

	static final private class JsBridge extends JavaScriptObject {
		protected JsBridge() {
		}

		public native boolean hasFileReader()/*-{
			return typeof FileReader != 'undefined';
		}-*/;

		public native boolean hasDND(DivElement holder)/*-{
			return 'draggable' in holder;
		}-*/;

		public native boolean hasAsyncFormData()/*-{
			return !!window.FormData;
			dropZone
		}-*/;

		public native boolean hasUploadProgress()/*-{
			return "upload" in new XMLHttpRequest;
		}-*/;

		public native JavaScriptObject getInputFiles()/*-{
			return this.inputFiles;
		}-*/;

		public native JavaScriptObject getDropFiles()/*-{
			return this.dropFiles;
		}-*/;

	}

	@UiField
	InputElement inputElement;
	@UiField
	DivElement dropZone;
	@UiField
	DivElement preview;
	@UiField
	Element progress;
	@UiField
	InlineLabel headerText;
	private final JsBridge bridge;
	private JsArray<Value> value;
	private MultipartFormActionUrlService actionUrl;
	private boolean lock;

	@Inject
	public MultipleCatalogFileUpload(MultipartFormActionUrlService actionUrl, CatalogUserInterfaceMessages cc) {
		initWidget(uiBinder.createAndBindUi(this));
		lock = false;
		this.actionUrl = actionUrl;
		bridge = JsBridge.createObject().cast();
		if (bridge.hasAsyncFormData() && bridge.hasDND(dropZone) && bridge.hasFileReader() && bridge.hasUploadProgress()) {
			init(preview, inputElement, bridge);
		} else {
			headerText.setText("your browser dows not support required features");
		}
		//keeps an upload session spanning multiple upload requests
		value = JavaScriptObject.createArray().cast();
	}

	@UiHandler("submit")
	public void startSubmit(ClickEvent e)  {
		if (lock) {
			return;
		}
		lock = true;

		StateTransition<String> callback = new DataCallback<String>() {

			@Override
			public void execute() {
				JavaScriptObject inputFiles = bridge.getInputFiles();
				if (inputFiles != null) {
					processFileArrayFromIndex(inputFiles, 0, progress, value, result);
				}
				JavaScriptObject dropFiles = bridge.getDropFiles();
				if (dropFiles != null) {
					processFileArrayFromIndex(dropFiles, 0, progress, value, result);
				}
			}

		};
		try {
			actionUrl.getUrl(callback);
		} catch (Exception eadsf) {
			GWT.log("Error getting upload url",eadsf);
		}
	}
	
	@Override
	public JsArray<Value> getValue() {
		return value;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<JsArray<Value>> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public void setValue(JsArray<Value> value, boolean fireEvents) {
		setValue(value);
		if (fireEvents) {
			ValueChangeEvent.fire(this, getValue());
		}
	}

	@Override
	public void setValue(JsArray<Value> value) {
		if (value == null) {
			value = Value.createArray().cast();
		}
		this.value = value;
	}

	public static void processFileArrayFromIndex(JavaScriptObject inputFiles, int index, Element progress, JsArray<Value> value, String fileUploadUrl) {
		JavaScriptObject file = getFile(inputFiles, index);
		if (file == null) {
			return;
		}
		JavaScriptObject callbackFunction = createCallbackFunction();
		sendImageToserver(file, fileUploadUrl, progress, callbackFunction, index, value, inputFiles);
	}

	private static native JavaScriptObject createCallbackFunction() /*-{
		return function(response, fileName, index, value, inputFiles, progress,
				fileUploadUrl) {
			var createdArr = @com.wrupple.muba.desktop.client.activity.widgets.impl.CatalogFileUpload::calculateNewValue(Ljava/lang/String;)(response);
			var newFile = createdArr[0];
			var valueElement = {
				id : newFile,
				name : fileName
			};
			value.push(valueElement);
			index++;
			@com.wrupple.muba.desktop.client.activity.widgets.impl.MultipleCatalogFileUpload::processFileArrayFromIndex(Lcom/google/gwt/core/client/JavaScriptObject;ILcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JsArray;Ljava/lang/String;)(inputFiles,index,progress,value,fileUploadUrl);
		};
	}-*/;

	private static native JavaScriptObject getFile(JavaScriptObject inputFiles, int index) /*-{
		if (index < inputFiles.length) {
			return inputFiles[index];
		} else {
			return null;
		}
	}-*/;

	private static native void sendImageToserver(JavaScriptObject file, String fileUploadUrl, Element progress, JavaScriptObject callbackFunction, int index,
			JsArray<Value> value, JavaScriptObject inputFiles)/*-{
		var formData = new FormData();
		// now post a new XHR request
		formData.append('file', file);
		var xhr = new XMLHttpRequest();
		xhr.open('POST', fileUploadUrl);
		xhr.onload = function() {
			var responseText = xhr.responseText;
			//progress.value = progress.innerHTML = file.name;
			callbackFunction(responseText, file.name, index, value, inputFiles,
					progress, fileUploadUrl);
		};

		if (progress != null) {
			xhr.upload.onprogress = function(event) {
				if (event.lengthComputable) {
					var complete = (event.loaded / event.total * 100 | 0);
					progress.value = progress.innerHTML = complete;
				}
			}
		}

		xhr.send(formData);
	}-*/;

	private native void init(DivElement holder, InputElement fileupload, JsBridge stateHolder)/*-{

		var renderPreview = function(state, holder) {
			if (state.dropFiles != null) {
				for (var i = 0; i < state.dropFiles.length; i++) {
					var reader = new FileReader();
					reader.onload = function(event) {
						var image = new Image();
						image.src = event.target.result;
						image.width = 98; // a fake resize
						holder.appendChild(image);
					};
					reader.readAsDataURL(state.dropFiles[i]);
				}
			}
			if (state.inputFiles != null) {
				for (var i = 0; i < state.inputFiles.length; i++) {
					var reader = new FileReader();
					reader.onload = function(event) {
						var image = new Image();
						image.src = event.target.result;
						image.width = 98; // a fake resize
						holder.appendChild(image);
					};
					reader.readAsDataURL(state.inputFiles[i]);
				}
			}

		}

		holder.ondragover = function() {
			this.style = 'color:white;';
			return false;
		};
		holder.ondragend = function() {
			this.style = 'color:black;';
			return false;
		};
		holder.ondrop = function(e) {
			this.style = '';
			e.preventDefault();
			stateHolder.dropFiles = e.dataTransfer.files;
			renderPreview(stateHolder, holder);
		}

		fileupload.onchange = function() {
			stateHolder.inputFiles = this.files;
			renderPreview(stateHolder, holder);
		};
	}-*/;



}
