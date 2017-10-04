package com.wrupple.muba.desktop.client.activity.widgets.editors.composite;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.activity.widgets.UserInteractionWidgetImpl;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.AbstractValueRelationEditor.RelationshipDelegate;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.ManyToOneRelationshipDelegate;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.OneToManyRelationshipDelegate;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.OneToOneRelationshipDelegate;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.StringJSOadapter;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEditorMap;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.desktop.client.services.logic.GenericFieldFactory;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.impl.GWTFieldConversionStrategyImpl;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.PanelTransformationConfig;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;

public abstract class CompositeCatalogEditor<V extends JavaScriptObject> extends UserInteractionWidgetImpl<V, V> implements CatalogEditor<V> {

	protected CatalogAction mode;
	protected EventBus bus;
	
	private String catalogParentId;
	protected ProcessContextServices processServices;
	protected JavaScriptObject properties;
	protected JsTransactionApplicationContext contextProcessParameters;
	private FieldChangeHandler collectFieldChangeEvents;
	private boolean collectFieldValuesFromPlace;
	
	private StorageManager catalogService;
	private ContentManagementSystem cms;

	protected Map<String, HasValue<Object>> fields;
	protected FieldConversionStrategy conversionService;
	protected GenericFieldFactory fieldFactory;
	protected FieldDescriptionService fieldService;

	private V currentEntry;
	private V originalEntry;
	private DesktopManager dm;

	public CompositeCatalogEditor(ContentManagementSystem cms, FieldDescriptionService fieldService, 
			FieldConversionStrategy conversion, GenericFieldFactory fieldDactory, CatalogEditorMap configurationService) {
		super(configurationService);
		this.cms = cms;
		this.conversionService = conversion;
		this.fieldService = fieldService;
		this.fieldFactory = fieldDactory;
		fields = new HashMap<String, HasValue<Object>>();
	}
	
	@Override
	protected void onAfterReconfigure(PanelTransformationConfig properties,
			ProcessContextServices contextServices, EventBus eventBus,
			JsTransactionApplicationContext contextParameters) {
		
	}


	@Override
	protected void onBeforeRecofigure(PanelTransformationConfig properties,
			ProcessContextServices contextServices, EventBus eventBus,
			JsTransactionApplicationContext contextParameters) {
		this.catalogService = contextServices.getStorageManager();
		this.dm=contextServices.getDesktopManager();
	}

	protected Object getAttributeAsObject(JavaScriptObject elem, String attr, FieldConversionStrategy strategy, List<FilterCriteria> includeCriteria) {
		if (elem == null) {
			return null;
		}
		assert attr != null : "Object Attribute cannot be null!";

		return strategy.convertToPresentableValue(attr, elem, includeCriteria);
	}

	@Override
	public V getValue() {
		assert getCatalog() != null : "This forms haz not been initialized";
		Set<String> fieldNames = fields.keySet();
		V value = getOrCreateOriginalEntry();
		Object fieldValue;
		String parentId;
		if (catalogParentId != null) {
			parentId = (String) getFieldValue(JsCatalogEntry.ANCESTOR_ID_FIELD);
			GWTUtils.setAttribute(value, JsCatalogEntry.ANCESTOR_ID_FIELD, parentId);
		}
		for (String fieldid : fieldNames) {
			fieldValue = getFieldValue(fieldid);
			GWTFieldConversionStrategyImpl.setAttribute(value, fieldid, fieldValue);
		}
		currentEntry = value;
		((JsCatalogEntry) currentEntry).setCatalog(getCatalog().getCatalogId());
		return value;
	}

	@Override
	public Object getFieldValue(String fieldid) {
		FieldDescriptor fdescriptor = getCatalog().getFieldDescriptor(fieldid);
		return getFieldValue(fdescriptor, fieldid);
	}

	private Object getFieldValue(FieldDescriptor fdescriptor, String fieldid) {
		HasValue<Object> field = fields.get(fieldid);
		if(field==null){
			return null;
		}else{
			Object fieldValue = field.getValue();
			fieldValue = conversionService.convertToPersistentValue(fieldValue, fdescriptor);
			return fieldValue;
		}
	}

	
	@Override
	public void setValue(V value) {
		this.originalEntry = value;
		catalogService.loadCatalogDescriptor(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), getCatalogId(), new SetValue());
	}

	protected void renderAllFields(V entry, Set<String> fieldNames) {
		assert getCatalog() != null : "This forms haz not been initialized";
		this.currentEntry = entry;
		StorageManager sm = processServices.getStorageManager();
		Object fieldValue;
		HasValue<Object> fieldWidget;
		FieldDescriptor fdescriptor;
		RelationshipDelegate relationshipDelegate;

		if (catalogParentId != null&&!getCatalog().isMergeAncestors()) {
			fieldWidget = fields.get(JsCatalogEntry.ANCESTOR_ID_FIELD);
			if (fieldWidget == null) {
				ContentManager<JsCatalogEntry> parentCatalogManager = cms.getContentManager(catalogParentId);
				fieldWidget = (HasValue)fieldFactory.getParentSelectionField(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalogParentId, catalogService, parentCatalogManager, processServices, this);
				fields.put(JsCatalogEntry.ANCESTOR_ID_FIELD, fieldWidget);
			}
			fdescriptor = generateParentIdFieldDescriptor();
			maybeAddField(fieldWidget, fdescriptor, ((JsFieldDescriptor) fdescriptor).getPropertiesObject());
			fieldWidget.setValue(GWTUtils.getAttribute(entry, JsCatalogEntry.ANCESTOR_ID_FIELD));
		}
		JavaScriptObject fieldProperties;
		for (String fieldid : fieldNames) {

			fdescriptor = getCatalog().getFieldDescriptor(fieldid);
			fieldProperties = ((JsFieldDescriptor) fdescriptor).getPropertiesObject();
			fieldProperties = mashWithformProperties(fieldid, fieldProperties, this.properties).cast();
			relationshipDelegate = null;
			if (fdescriptor.isKey()) {
				if (fdescriptor.isMultiple()) {
					relationshipDelegate = new OneToManyRelationshipDelegate(dm, fdescriptor, cms, sm, "relationship removed, delete entry aswell?");
				} else {
					relationshipDelegate = new OneToOneRelationshipDelegate(dm, fdescriptor, cms, sm, "relationship removed, delete entry aswell?");
				}
			} else {
				if (fdescriptor.isEphemeral()) {
					relationshipDelegate = new ManyToOneRelationshipDelegate(catalogService, bus, processServices, cms, fdescriptor, dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(),getCatalog().getCatalogId(),
							GWTUtils.getAttribute(entry, getCatalog().getKeyField()), contextProcessParameters);
				}
			}

			fieldValue = getAttributeAsObject(entry, fieldid, conversionService, null);

			fieldWidget = fields.get(fieldid);

			if (fieldWidget == null ) {
				if(fdescriptor.isEphemeral()&&CatalogAction.READ!=mode){
					fieldWidget=null;
				}else{
					fieldWidget = fieldFactory.getOrCreateField(fieldProperties, mode, bus, processServices, relationshipDelegate, contextProcessParameters,
							dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), (JsCatalogEntry) entry, fdescriptor);
					fields.put(fieldid, fieldWidget);
					if (collectFieldChangeEvents != null && fieldWidget != null) {
						fieldWidget.addValueChangeHandler(collectFieldChangeEvents);
					}
				}
			}

			if (fieldWidget == null) {
				// ignore
			} else {
				maybeAddField(fieldWidget, fdescriptor,fieldProperties);

				if (fieldValue == null && mode != CatalogAction.READ) {
					if (fdescriptor.getDefaultValue() != null) {
						//TODO poder decir que el valor por defecto se obtiene de un parametro de url
						//o a un valor en el context.task.getProducedField().proprty
						//ej: el valor de "driver" como la persona que se dió de alta en un paso anterior
						fieldValue = StringJSOadapter.performValueTransformation(fdescriptor.getDefaultValue());
					}
				}

				if (fdescriptor.isMultiple()) {
					if (fieldValue == null) {
						fieldValue = JavaScriptObject.createArray();
					}
				} else {

					// force string outcome
					if (fdescriptor.getDataType() == CatalogEntry.STRING_DATA_TYPE ||fdescriptor.getDataType() == CatalogEntry.NUMERIC_DATA_TYPE ||fdescriptor.getDataType() == CatalogEntry.INTEGER_DATA_TYPE||fdescriptor.getDataType() == CatalogEntry.LARGE_STRING_DATA_TYPE) {

						if (fieldValue == null) {
							fieldValue = "";
						} else {
							fieldValue = fieldValue.toString();
						}
					}

					if (fdescriptor.getDataType() == CatalogEntry.BOOLEAN_DATA_TYPE) {
						if (fieldValue == null) {
							fieldValue = false;
						}
					}
				}
				fieldWidget.setValue(fieldValue);
			}
		}
	}

	private native JavaScriptObject mashWithformProperties(String fieldid, JavaScriptObject fieldProperties, JavaScriptObject formproperties) /*-{
		var lrngth = fieldid.length;
		var formPropertyNameLength;
		var fieldPropertyName;
		for ( var k in formproperties) {
			//k = ""+k;
			if (k.indexOf(fieldid) == 0) {
				$wnd.alert(k);
				formPropertyNameLength = k.length;
				fieldPropertyName = k.substr(lrngth, formPropertyNameLength);
				if (formPropertyNameLength > 1
						&& !(fieldPropertyName == fieldPropertyName
								.toUpperCase())) {
					fieldPropertyName = fieldPropertyName.charAt(0)
							.toLowerCase()
							+ fieldPropertyName.slice(1);
				}
				fieldProperties[fieldPropertyName] = formproperties[k];
			}
		}
		return fieldProperties;
	}-*/;

	private FieldDescriptor generateParentIdFieldDescriptor() {
		JsFieldDescriptor regreso = JsFieldDescriptor.createObject().cast();
		regreso.setFieldId(JsCatalogEntry.ANCESTOR_ID_FIELD);
		regreso.setName(JsCatalogEntry.ANCESTOR_ID_FIELD);
		return regreso;
	}

	
	public void reset() {
		setValue(null);
	}

	public void setFieldValue(String fieldId, Object value) {
		HasValue<Object> fieldWidget = fields.get(fieldId);
		if (fieldWidget != null) {
			fieldWidget.setValue(value);
		}
	}


	@Override
	public void initialize(String catalog, CatalogAction mode, EventBus bus, ProcessContextServices processServices, JavaScriptObject properties,
			JsTransactionApplicationContext contextProcessParameters) {
		setCatalogid(catalog);
		this.mode = mode;
		this.bus = bus;
		this.properties = properties.cast();
		this.contextProcessParameters = contextProcessParameters;
		this.processServices = processServices;
	}


	public CatalogAction getMode() {
		return mode;
	}



	
	

	protected abstract void maybeAddField(HasValue<Object> field, FieldDescriptor fdescriptor, JavaScriptObject fieldProperties);



	public V getCurrentEntry() {
		if (currentEntry == null) {
			currentEntry = getOrCreateOriginalEntry();
		}
		return currentEntry;
	}

	protected V getOrCreateOriginalEntry() {
		if (originalEntry == null) {
			originalEntry = JsCatalogEntry.createCatalogEntry(getCatalog().getCatalogId()).cast();
		}
		return originalEntry;
	}

	protected void setCollectFieldChangeEvents(boolean collectFieldChangeEvents) {
		if (collectFieldChangeEvents) {
			this.collectFieldChangeEvents = new FieldChangeHandler();
		} else {
			this.collectFieldChangeEvents = null;
		}
	}

	private void findEphemeralValue(String localKey, String foreignCatalogId, String foreignKeyField, final StateTransition callback) {
		JsFilterData filterData = JsFilterData.createSingleFieldFilter(foreignKeyField, localKey);
		StorageManager sm = processServices.getStorageManager();
		sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), foreignCatalogId, filterData, callback);

	}

	public void fetchForeignEntries(JsArrayString array, String foreignCatalog, StateTransition foreignFieldCallback) {
		JsFilterData filterData = JsFilterData.createSingleFieldFilter(JsCatalogEntry.ID_FIELD, array);
		StorageManager sm = processServices.getStorageManager();
		sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), foreignCatalog, filterData, foreignFieldCallback);
	}

	

	public ProcessManager getProcessManager() {
		return processServices.getProcessManager();
	}

	public com.google.web.bindery.event.shared.EventBus getEventBus() {
		return bus;
	}

	class FieldChangeHandler implements ValueChangeHandler<Object> {

		public FieldChangeHandler() {
			super();
		}

		@Override
		public void onValueChange(ValueChangeEvent<Object> event) {
			V current = getValue();
			fireChangeEvent(current);
		}

	}



	private class FindForeignCatalogEphemeralValuesCallback extends DataCallback<CatalogDescriptor> {

		String localCatalog;
		private String localKey;
		private StateTransition<JsArray<JsCatalogEntry>> callback;

		public FindForeignCatalogEphemeralValuesCallback(String localKey, String localCatalog, StateTransition<JsArray<JsCatalogEntry>> foreignFieldCallback) {
			this.localKey = localKey;
			this.localCatalog = localCatalog;
			this.callback = foreignFieldCallback;
		}

		@Override
		public void execute() {
			assert result != null : "Ephemeral field points to null value";
			Collection<FieldDescriptor> foreignFields = result.getOwnedFieldsValues();

			String fieldsForeignCatalog;
			String foreignKeyField;
			for (FieldDescriptor field : foreignFields) {
				fieldsForeignCatalog = field.getForeignCatalogName();
				if (localCatalog.equals(fieldsForeignCatalog)) {
					foreignKeyField = field.getFieldId();
					findEphemeralValue(localKey, result.getCatalogId(), foreignKeyField, callback);
				}
			}

		}

	}

	


	private void gatherFieldDescriptorsAndRenderFields() {
		V value = originalEntry;
		if (collectFieldValuesFromPlace) {
			if (value == null) {
				value = getOrCreateOriginalEntry();
			}
		}
		Set<String> fieldNames;
		switch (mode) {
		case CREATE:
			fieldNames = fieldService.getCreateDescriptors(getCatalog()).keySet();
			break;
		case UPDATE:
			fieldNames = fieldService.getUpdateDescriptors(getCatalog()).keySet();
			break;
		default:
			fieldNames = fieldService.getDetailDescriptors(getCatalog()).keySet();
			break;
		}
		renderAllFields(value, fieldNames);

		fireChangeEvent(value);
	}
	
	/*
	 * USER INTERACTION 
	 */
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<V> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	protected void fireChangeEvent(V current) {
		ValueChangeEvent.fire(this, current);
	}
	
	private class SetValue extends DataCallback<CatalogDescriptor> {

		@Override
		public void execute() {
			setCatalog(result); ;
			if (mode == CatalogAction.CREATE && result.getParent() != null) {
				// PARENT ASSIGNATION ONLY AVAILABLE WHEN CREATING A NEW ENTRY
				catalogParentId = result.getParent();
			}
			gatherFieldDescriptorsAndRenderFields();

		}
	}
}
