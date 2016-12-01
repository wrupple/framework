package com.wrupple.muba.bpm.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.ContentDeterminationManifest;
import com.wrupple.muba.bpm.server.chain.command.AbstractComparationCommand;
import com.wrupple.muba.bpm.server.chain.command.BPMValidationTrigger;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;

@Singleton
public class BPMValidationTriggerImpl extends AbstractComparationCommand implements BPMValidationTrigger {
	private final ContentDeterminationManifest serviceManifest;

	@Inject
	public BPMValidationTriggerImpl(ContentDeterminationManifest serviceManifest, CatalogEvaluationDelegate accesor) {
		super(accesor);
		this.serviceManifest = serviceManifest;
	}


	@Override
	protected void compare(String codedFinalValue, String codedInitialValue, Object initialValue, Object finalValue,
			FieldDescriptor field, CatalogActionContext context) throws Exception {
		CatalogEntry old = context.getOldValue();
		String permission = serviceManifest.buildServiceRequestUri(':', context.getDomain(), catalog.getDistinguishedName(), old.getIdAsString(), field.getFieldId(),
				codedFinalValue, codedInitialValue);
		if (!(context.getExcecutionContext().getSession().hasPermission(permission))) {
			throw new SecurityException("[BPM] state violation: " + permission);
		}		
	}
}
