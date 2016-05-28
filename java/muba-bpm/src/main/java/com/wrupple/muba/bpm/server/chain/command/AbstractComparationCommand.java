package com.wrupple.muba.bpm.server.chain.command;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.HasAccesablePropertyValues;

public abstract class AbstractComparationCommand implements Command {

	protected final CatalogPropertyAccesor accesor;
	
	public AbstractComparationCommand(CatalogPropertyAccesor accesor) {
		super();
		this.accesor = accesor;
	}



	@Override
	public final boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = ((CatalogExcecutionContext) c);
		if (context.getSession().isMaster()) {

			CatalogDescriptor catalog = (CatalogDescriptor) context
					.get(CatalogActionTrigger.CATALOG_CONTEXT_KEY);
			CatalogEntry old = (CatalogEntry) context
					.get(CatalogActionTrigger.OLD_ENTRY_CONTEXT_KEY);
			CatalogEntry neew = (CatalogEntry) context
					.get(CatalogActionTrigger.NEW_ENTRY_CONTEXT_KEY);

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
						initialValue = ((HasAccesablePropertyValues) old)
								.getPropertyValue(field.getFieldId());
						finalValue = ((HasAccesablePropertyValues) neew)
								.getPropertyValue(field.getFieldId());
					} else {
						initialValue = accesor.getPropertyValue(catalog, field,
								old, null, session);
						finalValue = accesor.getPropertyValue(catalog, field,
								neew, null, session);
					}

					if (!(initialValue == null && finalValue == null)
							&& ((initialValue != null && !initialValue
									.equals(finalValue)) || (finalValue != null && !finalValue
									.equals(initialValue)))) {
						if (CatalogEntry.DATE_DATA_TYPE == field.getDataType()) {
							if (finalValue == null) {
								codedFinalValue = null;
							} else {
								codedFinalValue = String
										.valueOf(((Date) finalValue).getTime());
							}
							if (initialValue == null) {
								codedInitialValue = null;
							} else {
								codedInitialValue = String
										.valueOf(((Date) initialValue)
												.getTime());
							}
						} else {
							codedFinalValue = finalValue == null ? null
									: String.valueOf(finalValue);
							codedInitialValue = initialValue == null ? null
									: String.valueOf(initialValue);
						}
						compare(codedFinalValue,codedInitialValue,initialValue,finalValue,old,neew,catalog,field,context);
					}
				}
			}
		}

		return CONTINUE_PROCESSING;
	}



	protected abstract void compare(String codedFinalValue, String codedInitialValue, Object initialValue, Object finalValue, CatalogEntry old, CatalogEntry neew, CatalogDescriptor catalog, FieldDescriptor field, CatalogExcecutionContext context) throws Exception;

}
