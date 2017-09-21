package com.wrupple.muba.bpm.server.chain.command;

import java.util.Collection;
import java.util.Date;

import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.HasAccesablePropertyValues;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospection;

public abstract class AbstractComparationCommand implements Command {



	@Override
	public final boolean execute(Context c) throws Exception {
		CatalogActionContext context = ((CatalogActionContext) c);

		FieldAccessStrategy accesor = context.getCatalogManager().access();
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		CatalogEntry old = context.getOldValue();
		CatalogEntry neew = (CatalogEntry) context.getEntryValue();

		Collection<FieldDescriptor> fields = catalog.getFieldsValues();
		Object finalValue, initialValue;
		String codedFinalValue, codedInitialValue;
		boolean accesible = old instanceof HasAccesablePropertyValues;
		Instrospection instrospection = null;
		if (!accesible) {
			instrospection = accesor.newSession(old);
		}
		for (FieldDescriptor field : fields) {
			if (!field.isMultiple() && field.isWriteable()) {
				if (accesible) {
					initialValue = ((HasAccesablePropertyValues) old).getPropertyValue(field.getFieldId());
					finalValue = ((HasAccesablePropertyValues) neew).getPropertyValue(field.getFieldId());
				} else {
					initialValue = accesor.getPropertyValue(field, old, null, instrospection);
					finalValue = accesor.getPropertyValue(field, neew, null, instrospection);
				}

				if (!(initialValue == null && finalValue == null)
						&& ((initialValue != null && !initialValue.equals(finalValue))
								|| (finalValue != null && !finalValue.equals(initialValue)))) {
					if (CatalogEntry.DATE_DATA_TYPE == field.getDataType()) {
						if (finalValue == null) {
							codedFinalValue = null;
						} else {
							codedFinalValue = String.valueOf(((Date) finalValue).getTime());
						}
						if (initialValue == null) {
							codedInitialValue = null;
						} else {
							codedInitialValue = String.valueOf(((Date) initialValue).getTime());
						}
					} else {
						codedFinalValue = finalValue == null ? null : String.valueOf(finalValue);
						codedInitialValue = initialValue == null ? null : String.valueOf(initialValue);
					}
					compare(codedFinalValue, codedInitialValue, initialValue, finalValue, field,catalog, context);
				}
			}
		}

		return CONTINUE_PROCESSING;
	}

	protected abstract void compare(String codedFinalValue, String codedInitialValue, Object initialValue,
			Object finalValue, FieldDescriptor field,CatalogDescriptor catalog, CatalogActionContext context) throws Exception;

}
