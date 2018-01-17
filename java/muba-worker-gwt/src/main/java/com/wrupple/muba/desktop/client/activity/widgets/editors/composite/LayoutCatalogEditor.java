package com.wrupple.muba.desktop.client.activity.widgets.editors.composite;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEditorMap;
import com.wrupple.muba.desktop.client.services.logic.GenericFieldFactory;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.muba.worker.shared.domain.PanelTransformationConfig;
import com.wrupple.muba.worker.shared.event.EntriesDeletedEvent;
import com.wrupple.muba.worker.shared.services.FieldConversionStrategy;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class LayoutCatalogEditor extends CompositeCatalogEditor<JsCatalogEntry> {
	
	public static final String PERCENTAGE_LAYOUT = "pct";
	public static final String PIXEL_LAYOUT = "px";
	
	public static final String FRAME_LAYOUT = "frame";
	public static final String POSITION_LAYOUT = "position";
	
	public final static class CatalogLayoutDescriptor extends JavaScriptObject {

		protected CatalogLayoutDescriptor() {
			super();
		}

		public native String getLayoutUnit() /*-{
			return this.layoutUnit;
		}-*/;

		public native String getConstrainType()/*-{
			return this.constrainType;
		}-*/;

		public native double getTop(String fieldId) /*-{
			return this["top"+fieldId];
		}-*/;

		public native double getLeft(String fieldId) /*-{
		return this["left"+fieldId];
	}-*/;

		public native double getHeight(String fieldId) /*-{
		return this["height"+fieldId];
	}-*/;

		public native double getWidth(String fieldId) /*-{
		return this["width"+fieldId];
	}-*/;

		public native double getBottom(String fieldId) /*-{
		return this["bottom"+fieldId];
	}-*/;

		public native double getRight(String fieldId) /*-{
			return this["right"+fieldId];
		}-*/;
		
	}
	
	
	private Unit layoutUnit;
	private LayoutPanel main;
	

	@Inject
	public LayoutCatalogEditor(ContentManagementSystem cms, 
			FieldDescriptionService fieldService,
			FieldConversionStrategy conversion,
			GenericFieldFactory fieldDactory, CatalogEditorMap configurationService) {
		super(cms,  fieldService,  conversion, fieldDactory, configurationService);
		main = new LayoutPanel();
		initWidget(main);
	}

	@Override
	protected void maybeAddField(HasValue<Object> field, FieldDescriptor fdescriptor, JavaScriptObject fieldProperties) {
		CatalogLayoutDescriptor layoutDescriptor = super.properties.cast();
		IsWidget widget = (IsWidget) field;
		String layoutConstrain = layoutDescriptor.getConstrainType();
		String fieldId = fdescriptor.getFieldId();
		double top=layoutDescriptor.getTop(fieldId);
		double left=layoutDescriptor.getLeft(fieldId);
		if(main.getWidgetIndex(widget)<0){
			main.add(widget);
		}
		if(POSITION_LAYOUT.equals(layoutConstrain)){
			double height=layoutDescriptor.getHeight(fieldId);
			double width=layoutDescriptor.getWidth(fieldId);
			main.setWidgetTopHeight(widget, top, layoutUnit, height, layoutUnit);
			main.setWidgetLeftWidth(widget, left, layoutUnit, width, layoutUnit);
		}else if(FRAME_LAYOUT.equals(layoutConstrain)){
			double bottom=layoutDescriptor.getBottom(fieldId);
			double right=layoutDescriptor.getRight(fieldId);
			main.setWidgetTopBottom(widget, top, layoutUnit, bottom, layoutUnit);
			main.setWidgetLeftRight(widget, left, layoutUnit, right, layoutUnit);
		}else{
			throw new IllegalArgumentException("Unrecognized layout constrain type: "+layoutConstrain);
		}
		
	}
	
	@Override
	public void initialize(String catalog, CatalogAction mode, EventBus bus,
			ProcessContextServices processServices, JavaScriptObject properties,
			JsTransactionApplicationContext contextProcessParameters) {
		super.initialize(catalog, mode, bus, processServices, properties,
				contextProcessParameters);
		CatalogLayoutDescriptor layoutDescriptor = properties.cast();
		String unit = layoutDescriptor.getLayoutUnit();
		if(PIXEL_LAYOUT.equals(unit)){
			this.layoutUnit = Unit.PX;
		}else if(PERCENTAGE_LAYOUT.equals(unit)){
			this.layoutUnit = Unit.PCT;
		}else {
			throw new IllegalArgumentException("Layout unit unidentified");
		}
	}

	@Override
	public void onEntriesDeleted(EntriesDeletedEvent entriesDeletedEvent) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void applyAlterations(PanelTransformationConfig properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionApplicationContext contextParamenters) {
		// TODO what alteretaion can be applied? (similar to layout data panel?)
	}



}
