package com.wrupple.muba.bpm.server.chain.command;

import java.util.Collection;
import java.util.Date;

import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasAccesablePropertyValues;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy.Session;

public abstract class AbstractComparationCommand implements Command {

	protected final SystemCatalogPlugin accesor;

	public AbstractComparationCommand(SystemCatalogPlugin accesor) {
		super();
		this.accesor = accesor;
	}

	@Override
	public final boolean execute(Context c) throws Exception {
		CatalogActionContext context = ((CatalogActionContext) c);

		CatalogDescriptor catalog = context.getCatalogDescriptor();
		CatalogEntry old = context.getOldValue();
		CatalogEntry neew = (CatalogEntry) context.getEntryValue();

		Collection<FieldDescriptor> fields = catalog.getFieldsValues();
		Object finalValue, initialValue;
		String codedFinalValue, codedInitialValue;
		boolean accesible = old instanceof HasAccesablePropertyValues;
		Session session = null;
		if (!accesible) {
			session = accesor.newSession(old);
		}
		for (FieldDescriptor field : fields) {
			if (!field.isMultiple() && field.isWriteable()) {
				if (accesible) {
					initialValue = ((HasAccesablePropertyValues) old).getPropertyValue(field.getFieldId());
					finalValue = ((HasAccesablePropertyValues) neew).getPropertyValue(field.getFieldId());
				} else {
					initialValue = accesor.getPropertyValue(field, old, null, session);
					finalValue = accesor.getPropertyValue(field, neew, null, session);
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
					compare(codedFinalValue, codedInitialValue, initialValue, finalValue, field, context);
				}
			}
		}

		return CONTINUE_PROCESSING;
	}

	protected abstract void compare(String codedFinalValue, String codedInitialValue, Object initialValue,
			Object finalValue, FieldDescriptor field, CatalogActionContext context) throws Exception;

}
