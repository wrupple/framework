package com.wrupple.muba.desktop.client.factory.help.impl;

import java.util.Collection;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.factory.help.TriggerAidProvider;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.PropertyValueAvisor;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.HasCatalogId;

public class TriggerAidProviderImpl implements TriggerAidProvider {

	private CatalogEditor<JsCatalogEntry> editor;
	private ProcessContextServices context;

	@Inject
	public TriggerAidProviderImpl() {
	}

	@Override
	public void adviceOnCurrentConfigurationState(JavaScriptObject currentState, JsArray<PropertyValueAvisor> advice) {
		String catalogId = (String) editor.getFieldValue(HasCatalogId.FIELD);
		CatalogDescriptor catalog = context.getStorageManager().loadFromCache(context.getDesktopManager().getCurrentActivityHost(), context.getDesktopManager().getCurrentActivityDomain(), catalogId);
		Collection<FieldDescriptor> fields = catalog.getOwnedFieldsValues();
		for (FieldDescriptor field : fields) {
			maybeAdviceOnFieldContents(currentState, field, advice);
		}

	}

	private void maybeAdviceOnFieldContents(JavaScriptObject currentState, FieldDescriptor field, JsArray<PropertyValueAvisor> advice) {
		if (!GWTUtils.hasAttribute(currentState, field.getFieldId())) {
			// FIXME if has atribute advice on fields of enviroment variable
			advice.push(enviromentPropertyAdvice(field, CatalogActionTrigger.SOURCE_CONTEXT));
			advice.push(enviromentPropertyAdvice(field, CatalogActionTrigger.SOURCE_ENTRY));
			advice.push(enviromentPropertyAdvice(field, CatalogActionTrigger.SOURCE_OLD));
			advice.push(enviromentPropertyAdvice(field, CatalogActionRequest.CATALOG_ID_PARAMETER));
		}
	}

	private PropertyValueAvisor enviromentPropertyAdvice(FieldDescriptor field, String source) {
		PropertyValueAvisor regreso = PropertyValueAvisor.createObject().cast();
		regreso.setName(field.getFieldId());
		regreso.setValue(source);
		return regreso;
	}

	@Override
	public void validateValue(String fieldId, Object raw, JsArrayString r) {
		String value = (String) raw;
		if (value == null) {
			r.push("empty field ivalid");
		} else {
			if (!(value.startsWith(CatalogActionTrigger.SOURCE_CONTEXT) || value.startsWith(CatalogActionTrigger.SOURCE_ENTRY)
					|| value.startsWith(CatalogActionTrigger.SOURCE_OLD) || value.equals(CatalogActionRequest.CATALOG_ID_PARAMETER))) {
				r.push("unrecognized");
			}
		}
	}

	@Override
	public void setRuntimeParameters(String type, ProcessContextServices ctx) {
		this.context=ctx;
		editor = (CatalogEditor) ctx.getNestedTaskPresenter().getTaskContent().getMainTaskProcessor();

	}

}
