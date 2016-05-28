package com.wrupple.muba.bpm.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.bpm.server.chain.command.AbstractComparationCommand;
import com.wrupple.muba.bpm.server.chain.command.BPMValidationTrigger;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.cms.server.services.ContentDeterminationService;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;

@Singleton
public class BPMValidationTriggerImpl extends AbstractComparationCommand implements BPMValidationTrigger {
	private final ContentDeterminationService serviceManifest;

	@Inject
	public BPMValidationTriggerImpl(ContentDeterminationService serviceManifest, CatalogPropertyAccesor accesor) {
		super(accesor);
		this.serviceManifest = serviceManifest;
	}

	@Override
	protected void compare(String codedFinalValue, String codedInitialValue, Object initialValue, Object finalValue, CatalogEntry old, CatalogEntry neew,
			CatalogDescriptor catalog, FieldDescriptor field, CatalogExcecutionContext context) {
		String permission = serviceManifest.buildServiceRequestUri(':', context.getDomain(), catalog.getCatalogId(), old.getIdAsString(), field.getFieldId(),
				codedFinalValue, codedInitialValue);
		if (!(context.getSession().hasPermission(permission))) {
			throw new SecurityException("[BPM] state violation: " + permission);
		}
	}

}
