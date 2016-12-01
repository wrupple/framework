package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractDelegatingEditableField;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates.CatalogKeyTemplates;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
public class CurrencyValueCellProvider implements CatalogFormFieldProvider {

	private ContentManagementSystem cms;
	
	@Inject
	public CurrencyValueCellProvider(ContentManagementSystem cms){
		this.cms=cms;
	}

	@Override
	public Cell<? extends Object> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionActivityContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		return new CurrencyValueCell(cms, bus, contextServices, contextParameters, d, mode);
	}

	public static class CurrencyValueCell extends
			AbstractDelegatingEditableField<CurrencyCatalogEntry> {

		private ContentManagementSystem cms;
		private CatalogKeyTemplates template;
		private NumberFormat currencyFormat;

		public CurrencyValueCell(ContentManagementSystem cms, EventBus bus,
				ProcessContextServices contextServices,
				JavaScriptObject contextParameters, FieldDescriptor d, CatalogAction mode) {
			super(bus, contextServices, contextParameters, d, mode);
			this.cms = cms;
			template = GWT.create(CatalogKeyTemplates.class);
			currencyFormat = NumberFormat.getCurrencyFormat();
		}

		@Override
		protected String getProcessLocalizedName() {
			return super.fieldDescriptor.getForeignCatalogName();
		}

		@Override
		protected Process<CurrencyCatalogEntry, CurrencyCatalogEntry> getDelegateProcess() {
			String catalog = super.fieldDescriptor.getForeignCatalogName();
			ContentManager<JsCatalogEntry> manager = cms
					.getContentManager(catalog);
			Process editingProcess;
			switch (mode) {
			case CREATE:
				// modify foreign entry ¿really?
				editingProcess = manager.getEditingProcess(CatalogAction.UPDATE,
						getBus(), contextServices);
				break;
			default:
				editingProcess = manager.getEditingProcess(mode, getBus(),
						contextServices);
				break;
			}
			Process regreso = editingProcess;
			return regreso;
		}

		@Override
		protected void renderAsInput(
				com.google.gwt.cell.client.Cell.Context context,
				CurrencyCatalogEntry value,
				SafeHtmlBuilder sb,
				com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<CurrencyCatalogEntry> viewData) {
			if (value == null) {

			} else {
				JsCatalogEntry jkey = value.cast();
				String key = jkey.getId();
				String currencyCode = value.getCurrency();
				double price = value.getPrice();
				String name;
				if (key == null) {
					key = "";
				}

				if (currencyCode == null) {
					currencyCode = DesktopLoadingStateHolder.defaultCurrencyCode;
				}
				if (currencyCode == null) {
					name = currencyFormat.format(price);
				} else {
					NumberFormat currentCurrencyFormat = NumberFormat
							.getCurrencyFormat(currencyCode);
					name = currentCurrencyFormat.format(price);
				}
				SafeHtml output = template.value(key, name);
				sb.append(output);
			}
		}

		@Override
		protected void renderReadOnly(
				com.google.gwt.cell.client.Cell.Context context,
				CurrencyCatalogEntry value,
				SafeHtmlBuilder sb,
				com.wrupple.muba.desktop.client.activity.widgets.fields.cells.AbstractEditableField.FieldData<CurrencyCatalogEntry> viewData) {
			if (value == null) {
			} else {
				JsCatalogEntry jkey = value.cast();
				String key = jkey.getId();
				String currencyCode = value.getCurrency();
				double price = value.getPrice();
				String name;
				if (key == null) {
					key = "";
				}

				if (currencyCode == null) {
					currencyCode = DesktopLoadingStateHolder.defaultCurrencyCode;
				}
				if (currencyCode == null) {
					name = currencyFormat.format(price);
				} else {
					NumberFormat currentCurrencyFormat = NumberFormat
							.getCurrencyFormat(currencyCode);
					name = currentCurrencyFormat.format(price);
				}
				SafeHtml output = template.value(key, name);
				sb.append(output);
			}

		}

		@Override
		protected CurrencyCatalogEntry getCurrentInputValue(Element parent,
				boolean isEditing) {
			CurrencyCatalogEntry regreso = JavaScriptObject.createObject().cast();
			return regreso;
		}

	}

	public static final class CurrencyCatalogEntry extends JavaScriptObject {
		protected CurrencyCatalogEntry() {
		}

		public native String getCurrency() /*-{
			return this.currency;
		}-*/;

		public native double getPrice() /*-{
			if (this.price == null) {
				return 0;
			}
			return this.price;
		}-*/;
	}
}
