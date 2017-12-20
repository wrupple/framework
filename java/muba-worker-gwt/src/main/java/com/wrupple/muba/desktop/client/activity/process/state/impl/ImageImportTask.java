package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.HumanTask;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImportData;
import com.wrupple.muba.desktop.client.activity.widgets.impl.MultipleCatalogFileUpload;
import com.wrupple.muba.desktop.client.activity.widgets.impl.MultipleCatalogFileUpload.Value;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.MultipartFormActionUrlService;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.Collection;

/**
 * this implementation renders a multiple image upload form, and a list of all
 * other fields of the catalog, for each of the catalog's fields.
 * 
 * the point is to upload several images with a filename that matches the value
 * of a field
 * 
 * @author japi
 * 
 */
public class ImageImportTask extends ResizeComposite implements HumanTask<ImportData, ImportData> {

	private final DesktopManager dm;
	private final StorageManager catalogService;
	private final FlexTable table;
	private final MultipartFormActionUrlService actionUrl;
	private final CatalogUserInterfaceMessages cc;
	private final DesktopTheme theme;
	private static final int nameFieldColumn=0, uploadFieldColumn=1,fieldListColumn=2;

	@Inject
	public ImageImportTask(DesktopManager dm, StorageManager catalogService, MultipartFormActionUrlService actionUrl, CatalogUserInterfaceMessages cc,
			DesktopTheme theme) {
		this.dm=dm;
		this.catalogService = catalogService;
		table = new FlexTable();
		this.actionUrl = actionUrl;
		this.cc = cc;
		table.setWidth("100%");
		this.theme = theme;
		SimpleLayoutPanel sl = new SimpleLayoutPanel();
		sl.setWidget(table);
		initWidget(sl);
	}

	@Override
	public void start(final ImportData parameter, final StateTransition<ImportData> onDone, EventBus bus) {

		String catalogId = parameter.getCatalog();
		CatalogDescriptor descriptor = catalogService.loadFromCache(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalogId);
		Collection<FieldDescriptor> fields = descriptor.getOwnedFieldsValues();

		JsArray<JsFieldDescriptor> imageFields = JavaScriptObject.createArray().cast();

		for (FieldDescriptor field : fields) {

			if (ImplicitJoinUtils.isFileField(field)) {
				imageFields.push((JsFieldDescriptor) field);
			}
		}

		JsFieldDescriptor field;
		for (int i = 0; i < imageFields.length(); i++) {
			field = imageFields.get(i);
			addMultipleUploadforField(field, fields);
		}

		Image ok = new Image(theme.ok());
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int rowCount = table.getRowCount()-1;
				ListBox list;
				MultipleCatalogFileUpload uploadField;
				JsArray<Value> imageIdtoFileName;
				String fieldNameMappedtoImageFilename;
				int selectedIndex;
				String field;
				for (int i = 0; i < rowCount; i++) {
					field = table.getText(i, nameFieldColumn);
					list = (ListBox) table.getWidget(i, fieldListColumn);
					uploadField = (MultipleCatalogFileUpload) table.getWidget(i, uploadFieldColumn);
					imageIdtoFileName = uploadField.getValue();
					selectedIndex = list.getSelectedIndex();
					fieldNameMappedtoImageFilename = list.getValue(selectedIndex);
					parameter.addImageFieldImportData(field, imageIdtoFileName, fieldNameMappedtoImageFilename);
				}
				onDone.setResultAndFinish(parameter);
			}
		});
		table.setWidget(table.getRowCount(), 3, ok);
	}

	private void addMultipleUploadforField(JsFieldDescriptor field, Collection<FieldDescriptor> fields) {

		MultipleCatalogFileUpload uploadField = new MultipleCatalogFileUpload(actionUrl, cc);
		ListBox list = new ListBox();

		for (FieldDescriptor fieldd : fields) {
			list.addItem(fieldd.getName(), fieldd.getFieldId());
		}
		int row = table.getRowCount();
		table.setText(row, nameFieldColumn, field.getFieldId());
		table.setWidget(row, uploadFieldColumn, uploadField);
		table.setWidget(row, fieldListColumn, list);
	}

	

}
