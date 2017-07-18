package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates.InlineText;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class ReCAPTCHACellProvider implements CAPTCHACellProvider {

	public static String KEY;

	private static final class CaptchaOptions extends JavaScriptObject {
		protected CaptchaOptions() {
		}

		public native void setSiteKey(String key)/*-{
			this.sitekey = key;
		}-*/;
	}

	private static final class RenderCaptcha implements Command{
		final String id;
		final CaptchaOptions options;
		
		
		public RenderCaptcha(String id, CaptchaOptions options) {
			super();
			this.id=id;
			this.options = options;
		}


		@Override
		public void execute() {
			Element element = Document.get().getElementById(id);
			render(element, options);
		}
		
		private native void render(Element element, CaptchaOptions parameters)/*-{
		grecaptcha.render(element, parameters);
	}-*/;

		
	}
	
	@Override
	public Cell<String> createCell(EventBus bus, ProcessContextServices contextServices, JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		if (CatalogAction.READ == mode) {
			return new TextCell();
		} else {
			// call reCAPTCHA api AFTER event loop returns

			// https://www.google.com/recaptcha/api.js?onload=onloadCAPTCHACallback&render=explicit
			return new AbstractEditableField<String>(d, mode) {
				
				InlineText template;

				@Override
				protected void renderAsInput(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb,
						com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<String> viewData) {
					if(template==null){
						template= GWT.create(InlineText.class);
					}
					String id = HTMLPanel.createUniqueId();
					SafeHtml div = template.div(id);
					sb.append(div);
					CaptchaOptions options = CaptchaOptions.createObject().cast();
					options.setSiteKey(KEY);
					Scheduler.get().scheduleDeferred(new RenderCaptcha( id, options));
				}

				@Override
				protected void renderReadOnly(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb,
						com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<String> viewData) {
					if(template==null){
						template= GWT.create(InlineText.class);
					}
					SafeHtml div = template.span(value);
					sb.append(div);
				}

				@Override
				protected String getCurrentInputValue(Element parent, boolean isEditing) {
					String id= parent.getFirstChildElement().getId();
					return getValue(id);
				}

			
				private native String getValue(String widgetId)/*-{
					return grecaptcha.getResponse(widgetId);
				}-*/;

				@Override
				protected void onValueWillCommit(Element parent,
						com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<String> viewData) {

				}

				@Override
				protected void onWillEnterEditMode(Element parent,
						com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<String> viewData) {

				}

			};
		}
	}

}
