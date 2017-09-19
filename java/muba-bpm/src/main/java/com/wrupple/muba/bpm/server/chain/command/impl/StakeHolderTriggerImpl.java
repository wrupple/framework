package com.wrupple.muba.bpm.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
import com.wrupple.muba.event.domain.Instrospector;
import org.apache.commons.chain.Context;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.KnownExceptionImpl;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.bpm.server.chain.command.StakeHolderTrigger;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;

public class StakeHolderTriggerImpl implements StakeHolderTrigger {

	private static final String CHANGE_STAKEHOLDER = "com.wrupple.muba.bpm.stakeHolder";
	private final boolean anonStakeHolder;
	private final int unknownUser;

	@Inject
	public StakeHolderTriggerImpl(@Named("security.anonStakeHolder")Boolean anonStakeHolder, @Named("com.wrupple.errors.unknownUser") Integer unknownUser) {
		this.anonStakeHolder=anonStakeHolder;
		this.unknownUser=unknownUser;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		//get person Id
		CatalogActionContext context = (CatalogActionContext) c;
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		CatalogEntry old = context.getOldValue();
		long actualStakeHolder = context.getRuntimeContext().getSession().getStakeHolderPrincipal(Long.class);
		if(actualStakeHolder==CatalogEntry.PUBLIC_ID && ! anonStakeHolder){
			throw new KnownExceptionImpl("User Identity Unknown",null, unknownUser);
		}
		//get fieldWriting instrospector, see ocurrences
        FieldAccessStrategy accessor = context.getCatalogManager().access();
		//write into old the person id
		FieldDescriptor field = catalog.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
        Instrospector instrospector = accessor.newSession(old);
		Long stakeHolder= (Long) accessor.getPropertyValue(field, old, null, instrospector);
		if(stakeHolder==null || !context.getRuntimeContext().getSession().hasPermission(CHANGE_STAKEHOLDER+":"+catalog.getDistinguishedName())){
			System.err.println("[set stakeHolder]"+actualStakeHolder);
			accessor.setPropertyValue( field, (CatalogEntry) old, actualStakeHolder, accessor.newSession((CatalogEntry) old));
		}
		
		return CONTINUE_PROCESSING;
	}


}
