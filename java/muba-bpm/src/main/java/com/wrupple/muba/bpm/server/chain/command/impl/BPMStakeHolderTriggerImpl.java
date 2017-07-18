package com.wrupple.muba.bpm.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.KnownException;
import com.wrupple.muba.bootstrap.domain.KnownExceptionImpl;
import com.wrupple.muba.bootstrap.domain.reserved.HasStakeHolder;
import com.wrupple.muba.bpm.server.chain.command.BPMStakeHolderTrigger;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;

public class BPMStakeHolderTriggerImpl implements BPMStakeHolderTrigger {

	private static final String CHANGE_STAKEHOLDER = "com.wrupple.muba.bpm.stakeHolder";
	private final Provider<CatalogEvaluationDelegate> accessorProvider;
	private final boolean anonStakeHolder;

	@Inject
	public BPMStakeHolderTriggerImpl(Provider<CatalogEvaluationDelegate> accessorProvider,@Named("security.anonStakeHolder")Boolean anonStakeHolder) {
		this.accessorProvider=accessorProvider;
		this.anonStakeHolder=anonStakeHolder;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		//get person Id
		CatalogActionContext context = (CatalogActionContext) c;
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		CatalogEntry old = context.getOldValue();
		long actualStakeHolder = context.getRuntimeContext().getSession().getStakeHolderPrincipal(Long.class);
		if(actualStakeHolder==CatalogEntry.PUBLIC_ID && ! anonStakeHolder){
			throw new KnownExceptionImpl("User Identity Unknown", KnownException.USER_UNKNOWN, null);
		}
		//get fieldWriting session, see ocurrences
		CatalogEvaluationDelegate accessor = this.accessorProvider.get();
		//write into old the person id
		FieldDescriptor field = catalog.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
		Session session=accessor.newSession(old);
		Long stakeHolder= (Long) accessor.getPropertyValue(catalog, field, old, null, session);
		if(stakeHolder==null || !context.getRuntimeContext().getSession().hasPermission(CHANGE_STAKEHOLDER+":"+catalog.getDistinguishedName())){
			System.err.println("[set stakeHolder]"+actualStakeHolder);
			accessor.setPropertyValue(catalog, field, (CatalogEntry) old, actualStakeHolder, accessor.newSession((CatalogEntry) old));
		}
		
		return CONTINUE_PROCESSING;
	}


}
