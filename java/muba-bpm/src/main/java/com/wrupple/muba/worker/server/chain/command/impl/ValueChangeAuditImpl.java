package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.worker.domain.impl.FieldValueChangeImpl;
import com.wrupple.muba.worker.server.chain.command.AbstractComparationCommand;
import com.wrupple.muba.worker.server.chain.command.ValueChangeAudit;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ValueChangeAuditImpl extends AbstractComparationCommand implements ValueChangeAudit {

	@Inject
	public ValueChangeAuditImpl(FieldAccessStrategy accessStrategy) {
        super(accessStrategy);
    }


	@Override
	protected void compare(String codedFinalValue, String codedInitialValue, Object initialValue, Object finalValue,
                           FieldDescriptor field, CatalogDescriptor catalog,CatalogActionContext context) throws Exception {


		FieldValueChangeImpl fieldValueChange = new FieldValueChangeImpl();
		//FIXME value of the changed entry
        fieldValueChange.setDomain((Long) context.getNamespaceContext().getId());
        fieldValueChange.setCatalog(catalog.getDistinguishedName());
        fieldValueChange.setName(field.getFieldId());
        fieldValueChange.setOldValue(codedInitialValue);
        fieldValueChange.setValue(codedFinalValue);
		List<String> sentence = context.getRuntimeContext().getServiceBus().getIntentInterpret().resolveContractSentence(fieldValueChange);

		//REQUEST FOR PERMISSION CHANGING VALUE OF A FIELD

		String permission= String.join(":",sentence);
		
		if (!(context.getRuntimeContext().getSession().hasPermission(permission))) {
			throw new SecurityException("[BPM] state violation: " + permission);
		}		
	}
}
