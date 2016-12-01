package com.wrupple.muba.desktop.client.services.logic.impl;

import javax.inject.Provider;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.AbstractValueRelationEditor;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.AbstractValueRelationEditor.RelationshipDelegate;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.ManyToOneRelationEditor;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.OneToManyRelationEditor;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.OneToOneRelationEditor;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.ImageKeyThumbnail;
import com.wrupple.muba.desktop.client.activity.widgets.impl.CatalogFileUpload;
import com.wrupple.muba.desktop.client.activity.widgets.impl.ParentSelectionField;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogFieldMap;
import com.wrupple.muba.desktop.client.factory.dictionary.RelationshipFieldBrowserMap;
import com.wrupple.muba.desktop.client.factory.dictionary.SelectionModelDictionary;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.logic.GenericFieldFactory;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.client.services.presentation.ForeignRelationWidgetHandle;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.impl.MultipleSelectionModel;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class GenericFieldFactoryImpl implements GenericFieldFactory {

	protected CatalogFieldMap widgetter;
	private RelationshipFieldBrowserMap relationBrowserMap;
	private SelectionModelDictionary selectionModelFactory;

	private Provider<GenericDataProvider> dinamicDataProviderProvider;
	private Provider<CatalogFileUpload> catalogFileUploadProvider;
	private Provider<ImageKeyThumbnail> imageThumbnailProvider;
	private CatalogUserInterfaceMessages messages;

	@Inject
	public GenericFieldFactoryImpl(SelectionModelDictionary selectionModelFactory, Provider<GenericDataProvider> dinamicDataProviderProvider,
			RelationshipFieldBrowserMap relationBrowserMap, Provider<ImageKeyThumbnail> imageThumbnailProvider,
			Provider<CatalogFileUpload> catalogFileUploadProvider, CatalogFieldMap widgetter, CatalogUserInterfaceMessages messages) {
		super();
		this.selectionModelFactory = selectionModelFactory;
		this.relationBrowserMap = relationBrowserMap;
		this.messages = messages;
		this.dinamicDataProviderProvider = dinamicDataProviderProvider;
		this.catalogFileUploadProvider = catalogFileUploadProvider;
		this.widgetter = widgetter;
		this.imageThumbnailProvider = imageThumbnailProvider;
	}
	

	@Override
	public HasValue<Object> getOrCreateField(JavaScriptObject fieldProperties, CatalogAction mode, EventBus bus, ProcessContextServices contextServices, RelationshipDelegate delegate,
			JsTransactionActivityContext contextParameters, String host, String domain, JsCatalogEntry currentEntry, FieldDescriptor field) {
		Cell cell = null;
		String fieldId = field.getFieldId();
		String widget = GWTUtils.getAttribute(fieldProperties, "widget");
		if (widget == null) {
			// default field configuration
			widget = field.getWidget();
		}

		HasValue regreso;
		// FIXME support multiple file fields
		if (!field.isMultiple() && ImplicitJoinUtils.isFileField(field)) {
			if (mode == CatalogAction.READ) {
				/*FIXME support files (hint: ImplicitJoinUtils.isFileField )
				 * a file upload field sends a Upload Action Request to the foreign catalog of the field in question
				 */
				ImageKeyThumbnail imageField = imageThumbnailProvider.get()
				String rawcustomSize = GWTUtils.getAttribute(fieldProperties, ImageKeyThumbnail.CUSTOM_IMAGE_SIZE_PROPERTY);
				if (rawcustomSize != null) {
					try {
						// zero is full size
						int customSize = Integer.parseInt(rawcustomSize);
						imageField.setCustomSize(customSize);
					} catch (NumberFormatException e) {
						// ignore
					}
				}
				regreso = imageField;
			} else {
				regreso = catalogFileUploadProvider.get().initialize(contextServices);
			}
			return regreso;
		}

		try {
			// TODO CONFIGURATINO FRAMEWORK
			cell = widgetter.get(widget).createCell(bus, contextServices, contextParameters, fieldProperties, field, mode);
		} catch (NullPointerException e) {
			GWT.log("Unable to find cell suitable for field " + field.getName() + " with widget id:" + widget, e);
			return null;
		}

		if (ImplicitJoinUtils.isJoinableValueField(field)) {
			// provided cell handles foreign values
			regreso = assembleDataField(host,domain,cell, delegate, bus, contextServices, contextParameters, field, fieldProperties, mode, currentEntry);
		} else {
			regreso = new CellWidget<Object>(cell);
		}
		return regreso;
	}

	@Override
	public HasValue<String> getParentSelectionField(String host, String domain,
			String catalogParentId, StorageManager catalogService, ContentManager<JsCatalogEntry> parentCatalogManager, ProcessContextServices processServices, CatalogEditor<? extends JavaScriptObject> editor) {
		String message = messages.selectParentEntry();
		return new ParentSelectionField(host,domain,catalogParentId, catalogService, parentCatalogManager, processServices, editor, message);
	}

	private AbstractValueRelationEditor<?> assembleDataField(String host, String domain,Cell<JsCatalogEntry> cell, RelationshipDelegate delegate, EventBus bus,
			ProcessContextServices contextServices, JsTransactionActivityContext contextParameters, FieldDescriptor field, JavaScriptObject fieldProperties, CatalogAction mode,
			JsCatalogEntry entry) {
		HasData<JsCatalogEntry> dataWidget;
		String foreignCatalog = field.getForeignCatalogName();
		GenericDataProvider dataProvider = dinamicDataProviderProvider.get();
		dataProvider.setCatalog(foreignCatalog);
		//if you want cache then preload entries , since retriving service will attempt to satisfy key-only queries using cache anyways
		dataProvider.setUseCache(false);
		ForeignRelationWidgetHandle widgetFactory = relationBrowserMap.getConfigured(fieldProperties, contextServices, bus, contextParameters);
		widgetFactory.init(field,fieldProperties,contextParameters,contextServices,delegate,dataProvider,mode);
		dataWidget = widgetFactory.get(cell);

		dataProvider.setCustomJoins(GWTUtils.getCustomJoins(widgetFactory.getCustomJoins()));
		GWTUtils.setAttribute(fieldProperties, selectionModelFactory.getPropertyName(), selectionModelFactory.getDefault());
		if (!GWTUtils.hasAttribute(fieldProperties, SelectionModelDictionary.SELECTION_HANDLER)) {
			GWTUtils.setAttribute(fieldProperties, SelectionModelDictionary.SELECTION_HANDLER, SelectionModelDictionary.NO_SELECTION_HANDLER);
		}

		GWTUtils.setAttribute(fieldProperties, selectionModelFactory.getPropertyName(), MultipleSelectionModel.NAME);
		
		SelectionModel<JsCatalogEntry> foreignEntrySelectionModel = selectionModelFactory.getConfigured(fieldProperties, contextServices, bus,
				contextParameters);

		dataWidget.setSelectionModel(foreignEntrySelectionModel);

		AbstractValueRelationEditor<?> regreso;
		if (field.isEphemeral()) {
			if (CatalogAction.CREATE == mode) {
				regreso = null;
			} else {
				regreso = new ManyToOneRelationEditor(host,domain,contextParameters, contextServices, delegate, dataProvider, dataWidget, fieldProperties, field,
						mode, widgetFactory.getPageSize(), entry.getId(), entry.getCatalog(), widgetFactory.showAddRelation(), widgetFactory.showRemoveSelectionFromRelation());
			}
		} else if (field.isMultiple()) {
			regreso = new OneToManyRelationEditor(contextParameters, contextServices, delegate, dataProvider, dataWidget, fieldProperties, field,
					mode, widgetFactory.getPageSize(), widgetFactory.showAddRelation(),widgetFactory.showRemoveSelectionFromRelation());
		} else {
			regreso = new OneToOneRelationEditor(contextParameters, fieldProperties, contextServices, delegate, dataProvider, dataWidget, fieldProperties,
					field, mode, widgetFactory.getPageSize(), widgetFactory.showAddRelation(),widgetFactory.showRemoveSelectionFromRelation());
		}
		return regreso;
	}
}