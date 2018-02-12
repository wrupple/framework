package com.wrupple.muba.desktop.client.activity.widgets.editors.composite;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.client.services.evaluation.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.desktop.client.activity.widgets.GenericCatalogEditor;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.CatalogEditorMap;
import com.wrupple.muba.desktop.client.services.logic.GenericFieldFactory;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.muba.worker.shared.services.FieldConversionStrategy;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.ArrayList;

/**
 * 
 * 
 * @author japi
 * 
 */
public class GenericCatalogEditorImpl extends CompositeCatalogEditor<JsCatalogEntry> implements GenericCatalogEditor {

	private ScrollPanel container;
	private FlexTable table;
	private final CatalogEvaluationDelegate delegate;
	private ArrayList<FieldRowState> rows;
	private boolean block;
	private String labelSource;
	protected static final int DEFAULT_WIDTH = 600;

	static class FieldRowState {
		private final FieldDescriptor field;
		private final Label labelWidget;
		private final Widget fieldWidget;

		public FieldRowState(FieldDescriptor fdescriptor, HasValue<Object> field, Label label2) {
			super();
			this.field = fdescriptor;
			this.labelWidget = label2;
			this.fieldWidget = ((IsWidget) field).asWidget();
		}

	}

	private class FieldFocusHandler implements ValueChangeHandler<Object> {

		private final int row;

		public FieldFocusHandler(int row) {
			this.row = row;
		}

		@Override
		public void onValueChange(ValueChangeEvent<Object> event) {
			FieldRowState state = rows.get(row);
			state.fieldWidget.setVisible(true);
			state.labelWidget.setVisible(true);
			FieldRowState current = rows.get(row);
			JsArrayString constraintViolation = JsArrayString.createArray().cast();
			String fieldId = current.field.getFieldId();
			
			delegate.validate(getCatalog(), fieldId, getValue(), constraintViolation);
			
			if (constraintViolation.length() == 0) {
				int nextRowIndex = row + 1;
				// show next field
				if(nextRowIndex<rows.size()){
					FieldRowState nextRow = rows.get(nextRowIndex);
					nextRow.fieldWidget.setVisible(true);
					nextRow.labelWidget.setVisible(true);
				}
				if(table.getCellCount(row)>2){
					table.removeCell(row, 2);
				}
			} else {
				String text;
				FlowPanel wv = new FlowPanel();
				for(int i = 0 ;  i < constraintViolation.length(); i++){
					text=constraintViolation.get(i);
					wv.add(new InlineLabel(text));
				}
				table.setWidget(row, 2, wv);
			}
		}

	}

	@Inject
	public GenericCatalogEditorImpl(CatalogEvaluationDelegate delegate, ContentManagementSystem cms, GenericFieldFactory fieldFactory, FieldDescriptionService fieldService,
			 FieldConversionStrategy conversionService, CatalogEditorMap configurationService) {
		super(cms, fieldService,  conversionService, fieldFactory, configurationService);
		container = new ScrollPanel();
		this.delegate=delegate;
		table = new FlexTable();
		table.getElement().getStyle().setProperty("marginLeft", "auto");
		table.getElement().getStyle().setProperty("marginRight", "auto");
		// set loading icon while the table is not initialized
		Resources r = GWT.create(CellTable.Resources.class);
		Image loading = new Image(r.cellTableLoading());
		container.setWidget(loading);
		SimpleLayoutPanel resizeWrapper = new SimpleLayoutPanel();
		resizeWrapper.add(container);
		initWidget(resizeWrapper);
	}

	@Override
	public void setValue(JsCatalogEntry value) {
		table.removeAllRows();
		super.setValue(value);
	}

	@Override
	public void initialize(String catalog, CatalogAction mode, EventBus bus, ProcessContextServices processServices, JavaScriptObject properties,
			JsTransactionApplicationContext contextProcessParameters) {
		super.initialize(catalog, mode, bus, processServices, properties, contextProcessParameters);
		container.setWidget(table);
	}

	@Override
	protected void maybeAddField(HasValue<Object> field, FieldDescriptor fdescriptor, JavaScriptObject fieldProperties) {
		int row = table.getRowCount();
		field.addValueChangeHandler(new FieldFocusHandler(row));
		if (rows == null) {
			rows = new ArrayList<GenericCatalogEditorImpl.FieldRowState>();
		}
		if (inUserAidMode()) {
			
			if (row == 0) {
				addRow(row, fdescriptor, field, true,fieldProperties);

			} else {
				addRow(row, fdescriptor, field, false,fieldProperties);
			}
		} else {
			addRow(row, fdescriptor, field, true,fieldProperties);
		}
	}

	private void addRow(int row, FieldDescriptor fdescriptor, HasValue<Object> field, boolean visible, JavaScriptObject fieldProperties) {

		String labelTag;
		if(labelSource==null){
			labelTag= fdescriptor.getName();
		}else {
			labelTag= GWTUtils.getAttribute((JavaScriptObject)fdescriptor, labelSource /*eg: description*/);
			if(labelTag==null){
				labelTag = GWTUtils.getAttribute(fieldProperties, labelSource);
			}
			 if(labelTag==null){
				 labelTag= fdescriptor.getName();
			 }
		}
		
		
		Label label = new Label(labelTag);
		label.setVisible(visible);
		((Widget) field).setVisible(visible);
		table.setWidget(row, 0, label);
		table.setWidget(row, 1, ((Widget) field));
		rows.add(row, new FieldRowState(fdescriptor, field, label));
	}
	
	

	private boolean inUserAidMode() {
		return block && delegate != null;
	}



	/*
	 * CONFIGURATION FRAMEWORK SETTERS
	 */

	@Override
	public void setBlock(String block) {
		this.block = block != null;
	}

	@Override
	public void setLabelSource(String property) {
		this.labelSource=property;
	}
}
