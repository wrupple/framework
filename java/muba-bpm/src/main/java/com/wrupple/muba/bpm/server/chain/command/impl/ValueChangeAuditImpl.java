package com.wrupple.muba.bpm.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.bpm.domain.impl.FieldValueChangeImpl;
import com.wrupple.muba.bpm.server.chain.command.AbstractComparationCommand;
import com.wrupple.muba.bpm.server.chain.command.ValueChangeAudit;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

import java.util.List;

@Singleton
public class ValueChangeAuditImpl extends AbstractComparationCommand implements ValueChangeAudit {

	@Inject
	public ValueChangeAuditImpl() {
	}


	@Override
	protected void compare(String codedFinalValue, String codedInitialValue, Object initialValue, Object finalValue,
                           FieldDescriptor field, CatalogDescriptor catalog,CatalogActionContext context) throws Exception {


		FieldValueChangeImpl fieldValueChange = new FieldValueChangeImpl();
        fieldValueChange.setDomain((Long) context.getDomain());
        fieldValueChange.setCatalog(catalog.getDistinguishedName());
        fieldValueChange.setName(field.getFieldId());
        fieldValueChange.setOldValue(codedInitialValue);
        fieldValueChange.setValue(codedFinalValue);
		List<String> sentence = context.getRuntimeContext().getApplication().getIntentInterpret().resolveContractSentence(fieldValueChange);

		//REQUEST FOR PERMISSION CHANGING VALUE OF A FIELD

		String permission= String.join(":",sentence);
		
		if (!(context.getRuntimeContext().getSession().hasPermission(permission))) {
			throw new SecurityException("[BPM] state violation: " + permission);
		}		
	}
}
