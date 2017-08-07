package com.wrupple.muba.bpm.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.impl.FieldValueChangeImpl;
import com.wrupple.muba.bpm.server.chain.command.AbstractComparationCommand;
import com.wrupple.muba.bpm.server.chain.command.BPMValidationTrigger;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import org.apache.commons.lang3.StringUtils;

@Singleton
public class BPMValidationTriggerImpl extends AbstractComparationCommand implements BPMValidationTrigger {

	@Inject
	public BPMValidationTriggerImpl() {
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
		String[] sentence = context.getRuntimeContext().getApplication().getContractSentence(fieldValueChange);

		//REQUEST FOR PERMISSION CHANGING VALUE OF A FIELD
		String permission= StringUtils.join(sentence,':');
		
		if (!(context.getRuntimeContext().getSession().hasPermission(permission))) {
			throw new SecurityException("[BPM] state violation: " + permission);
		}		
	}
}
