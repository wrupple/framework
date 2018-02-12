package com.wrupple.muba.desktop.client.activity.widgets.editors.composite;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.*;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.CatalogEditorMap;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.CatalogFieldMap;
import com.wrupple.muba.desktop.client.services.logic.GenericFieldFactory;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.muba.worker.shared.services.FieldConversionStrategy;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.Set;

public class StackCatalogEditor<V extends JavaScriptObject> extends CompositeCatalogEditor<V> {
	
	class EditorValueChangeHandler implements ValueChangeHandler<V>{

		@Override
		public void onValueChange(ValueChangeEvent<V> event) {
			JsCatalogEntry value = event.getValue().cast();
			Set<String> fieldNames = fields.keySet();
			Object fieldValue;
			for(String field: fieldNames){
				fieldValue=getAttributeAsObject(value, field, conversionService, null);
				if(fieldValue==null){
					jumpTo(field);
					//this... efectively jumps to the first field after the value is set
					break;
				}
			}
		}
		
	}

	private StackLayoutPanel main;
	protected CatalogFieldMap widgetter;
	public StackCatalogEditor(ContentManagementSystem cms,
			GenericFieldFactory fieldFactory,
			FieldDescriptionService fieldService,
			CatalogFieldMap widgetter, 
			FieldConversionStrategy conversionService, 
			CatalogEditorMap configurationService) {
		super(cms, fieldService, conversionService, fieldFactory, configurationService);
		main = new StackLayoutPanel(Unit.EM);
		this.widgetter = widgetter;
		initWidget(main);
		setJumpToFirstEmptyField(false);
	}
	

	public void jumpTo(String fieldId) {
		HasValue<Object> field = fields.get(fieldId);
		main.showWidget((Widget)field);
	}

	@Override
	protected void maybeAddField(HasValue<Object> widget,
			FieldDescriptor field, JavaScriptObject fieldProperties) {
		if(main.getWidgetIndex((IsWidget)widget)<0){
			Widget header = generateFieldHeader(field);
			
			setFieldWidget(header, (IsWidget)widget);
		}
		
	}

	
	public void setJumpToFirstEmptyField(boolean jumpToFirstEmptyField) {
		if(jumpToFirstEmptyField){
			super.setCollectFieldChangeEvents(true);
			addValueChangeHandler(new EditorValueChangeHandler());
		}
	}


	public IsWidget setFieldWidget(String name,
			IsWidget field) {
		Widget head = generateFieldHeader(name);
		return setFieldWidget(head, field);
	}
	
	public IsWidget setFieldWidget(Widget head,
			IsWidget field) {
		main.add(field, head, 2.5);
		return field;
	}

	public void removeField(IsWidget field) {
		main.remove(field.asWidget());
	}
	
	private Widget generateFieldHeader(FieldDescriptor field) {
		return generateFieldHeader(field.getName());
	}
	
	private Widget generateFieldHeader(String field) {
		Label l = new Label(field);
		return l;
	}


	





}
