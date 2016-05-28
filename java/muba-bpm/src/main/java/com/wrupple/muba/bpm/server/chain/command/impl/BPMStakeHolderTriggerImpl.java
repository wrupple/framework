package com.wrupple.muba.bpm.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bpm.server.chain.command.BPMStakeHolderTrigger;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.HasStakeHolder;
import com.wrupple.vegetate.domain.VegetateUserException;
import com.wrupple.vegetate.server.domain.VegetateException;

public class BPMStakeHolderTriggerImpl implements BPMStakeHolderTrigger {

	private static final String CHANGE_STAKEHOLDER = "com.wrupple.muba.bpm.stakeHolder";
	private final Provider<CatalogPropertyAccesor> accessorProvider;
	private final boolean anonStakeHolder;

	@Inject
	public BPMStakeHolderTriggerImpl(Provider<CatalogPropertyAccesor> accessorProvider,@Named("security.anonStakeHolder")Boolean anonStakeHolder) {
		this.accessorProvider=accessorProvider;
		this.anonStakeHolder=anonStakeHolder;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		//get person Id
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		CatalogDescriptor catalog = (CatalogDescriptor) context.get(CatalogActionTrigger.CATALOG_CONTEXT_KEY);
		CatalogEntry old = (CatalogEntry) context.get(CatalogActionTrigger.OLD_ENTRY_CONTEXT_KEY);
		long actualStakeHolder = context.getPersonId();
		if(actualStakeHolder==CatalogEntry.PUBLIC_ID && ! anonStakeHolder){
			throw new VegetateException("User Identity Unknown", VegetateUserException.USER_UNKNOWN, null);
		}
		//get fieldWriting session, see ocurrences
		CatalogPropertyAccesor accessor = this.accessorProvider.get();
		//write into old the person id
		FieldDescriptor field = catalog.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
		Session session=accessor.newSession(old);
		Long stakeHolder= (Long) accessor.getPropertyValue(catalog, field, old, null, session);
		if(stakeHolder==null || !context.getSession().hasPermission(CHANGE_STAKEHOLDER+":"+catalog.getCatalogId())){
			System.err.println("[set stakeHolder]"+actualStakeHolder);
			accessor.setPropertyValue(catalog, field, (CatalogEntry) old, actualStakeHolder, accessor.newSession((CatalogEntry) old));
		}
		
		return CONTINUE_PROCESSING;
	}


}
