package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.DatePickerCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.Date;
import java.util.Set;
public class DateCellProvider implements CatalogFormFieldProvider {

	public static class FormattedDateCell implements Cell<String> {
		private final FieldDescriptor d;
		private final Cell<Date> wrapped;
		Date cache;
		String cacheValue;

		public FormattedDateCell(FieldDescriptor d, Cell<Date> wrapped) {
			this.d = d;
			this.wrapped = wrapped;
		}

		@Override
		public boolean dependsOnSelection() {
			return wrapped.dependsOnSelection();
		}

		@Override
		public Set<String> getConsumedEvents() {
			return wrapped.getConsumedEvents();
		}

		@Override
		public boolean handlesSelection() {
			return wrapped.handlesSelection();
		}

		@Override
		public boolean isEditing(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, String value) {
			Date parsedValue = getParsedValue(value);
			return wrapped.isEditing(context, parent, parsedValue);
		}

		@Override
		public void onBrowserEvent(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, String value, NativeEvent event,
				final ValueUpdater<String> valueUpdater) {
			Date parsedValue = getParsedValue(value);
			ValueUpdater<Date> wrappedvalueUpdater = new ValueUpdater<Date>() {

				@Override
				public void update(Date value) {
					String rawValue =DesktopLoadingStateHolder
							.getFormat().format(value);
					valueUpdater.update(rawValue);
				}
			};
			wrapped.onBrowserEvent(context, parent, parsedValue, event,
					wrappedvalueUpdater);
		}

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				String value, SafeHtmlBuilder sb) {
			Date parsedValue = getParsedValue(value);
			wrapped.render(context, parsedValue, sb);
		}

		@Override
		public boolean resetFocus(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, String value) {
			final Date parsedValue = getParsedValue(value);
			return wrapped.resetFocus(context, parent, parsedValue);
		}

		@Override
		public void setValue(
				com.google.gwt.cell.client.Cell.Context context,
				Element parent, String value) {
			Date parsedValue = getParsedValue(value);
			wrapped.setValue(context, parent, parsedValue);
		}

		private Date getParsedValue(String value) {
			if (cacheValue != null && value == cacheValue) {
				return cache;
			} else {
				if(value==null){
					if(d.getDefaultValue()!=null){
						cacheValue = d.getDefaultValue();
						cache = DesktopLoadingStateHolder.getFormat().parse(cacheValue);
						return cache;
					}else{
						return new Date();
					}
				}else{
					cacheValue = value;
					cache = DesktopLoadingStateHolder.getFormat().parse(value);
					return cache;
				}
			}
		}
	}

	@Override
	public Cell<String> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor,final FieldDescriptor d, CatalogAction mode) {
		// DesktopLoadingStateHolder.datePattern
		final Cell<Date> wrapped;
		if (mode == CatalogAction.READ) {
			wrapped = new DateCell();
		} else {
			wrapped = new DatePickerCell();
		}
		
		return new FormattedDateCell(d, wrapped);
	}

}
