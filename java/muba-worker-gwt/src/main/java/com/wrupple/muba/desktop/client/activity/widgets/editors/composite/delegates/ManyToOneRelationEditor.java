package com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.view.client.HasData;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.services.presentation.FilterableDataProvider;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;

import java.util.Collection;

public class ManyToOneRelationEditor extends OneToManyRelationEditor {
	
	public ManyToOneRelationEditor(String host, String domain,JavaScriptObject contextParameters, ProcessContextServices contextServices, RelationshipDelegate delegate,
			FilterableDataProvider<JsCatalogEntry> dataProvider, HasData<JsCatalogEntry> dataWidget, JavaScriptObject formProperties,
			FieldDescriptor field, CatalogAction mode, int pageSize, String localKey,String localCatalogId,boolean showAddition, boolean showRemoval) {
		super(contextParameters, contextServices, delegate, dataProvider, dataWidget, formProperties, field, mode, pageSize, showAddition, showRemoval);
		String foreignCatalog = field.getForeignCatalogName();
		contextServices.getStorageManager().loadCatalogDescriptor(host,domain, foreignCatalog, new SetEphemeralFilterData(localKey, localCatalogId));
	}

	private class SetEphemeralFilterData extends DataCallback<CatalogDescriptor> {

		final String localKey;
		final String localCatalogId;
		

		public SetEphemeralFilterData(String localKey, String localCatalogId) {
			super();
			this.localKey = localKey;
			this.localCatalogId = localCatalogId;
		}


		@Override
		public void execute() {
			Collection<FieldDescriptor> allForeignFields = result.getOwnedFieldsValues();
			for (FieldDescriptor foreignField : allForeignFields) {
				if (localCatalogId.equals(foreignField.getForeignCatalogName())) {
					JsFilterData filterData = JsFilterData.createSingleFieldFilter(foreignField.getFieldId(), localKey);
					setFilterValue(filterData);
				}
			}
		}

	}
	
	@Override
	public void setValue(JsArrayString value) {
	}
	
	@Override
	public JsArrayString getValue() {
		return null;
	}

	
}
