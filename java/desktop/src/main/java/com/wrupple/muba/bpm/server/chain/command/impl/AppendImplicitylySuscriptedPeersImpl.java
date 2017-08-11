package com.wrupple.muba.bpm.server.chain.command.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.CatalogChangeEvent;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.HasAccesablePropertyValues;
import com.wrupple.muba.bootstrap.domain.Host;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.reserved.HasStakeHolder;
import com.wrupple.muba.bpm.server.chain.command.AppendImplicitylySuscriptedPeers;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogPeer;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.EventSuscriptionChain;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;

@Singleton
public class AppendImplicitylySuscriptedPeersImpl implements AppendImplicitylySuscriptedPeers {



	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		Collection<CatalogPeer> concernedClients = (Collection<CatalogPeer>) context
				.get(EventSuscriptionChain.CONCERNED_CLIENTS);
		CatalogChangeEvent event = (CatalogChangeEvent) context.get(EventSuscriptionChain.CURRENT_EVENT);
		CatalogEntry entry = event.getEntryValue();
		CatalogDescriptor descriptor = context.getCatalogManager().getDescriptorForName((String)event.getCatalog(), context);

		Collection<FieldDescriptor> fields = descriptor.getFieldsValues();
		FieldAccessStrategy accessor = context.getCatalogManager().access();
		Set<Long> concernedPeople = null;
		FieldAccessStrategy.Session session = accessor.newSession(entry);
		for (FieldDescriptor field : fields) {
			if (Person.CATALOG.equals(field.getCatalog())) {
				// TODO user may choose not to notify people listed in a
				// certain
				// field
				// "ImplicitSuscriptionRules"
				if (concernedPeople == null) {
					concernedPeople = new HashSet<Long>(3);
				}
				addFieldValuesToConcernedPeopleList(entry, descriptor, field, concernedPeople, session, accessor);
			}
		}

		if (concernedPeople != null && ! concernedPeople.isEmpty()) {
			FilterData concernedPeopleClients = FilterDataUtils.createSingleKeyFieldFilter(HasStakeHolder.STAKE_HOLDER_FIELD,new ArrayList<Long>( concernedPeople));
			CatalogActionContext read = context.getCatalogManager().spawn(context);
			read.setCatalog(Host.CATALOG);
			read.setFilter(concernedPeopleClients);
			read.getCatalogManager().getRead().execute(read);
			if(read.getResults()!=null){
				Collection<? extends CatalogPeer> results=read.getResults();
				concernedClients.addAll(results);
			}
		}

		return CONTINUE_PROCESSING;
	}

	private void addFieldValuesToConcernedPeopleList(CatalogEntry entry, CatalogDescriptor descriptor,
			FieldDescriptor field, Set<Long> concernedPeople, FieldAccessStrategy.Session session, FieldAccessStrategy accessor) throws ReflectiveOperationException {
		boolean accesable = HasAccesablePropertyValues.class.isAssignableFrom(entry.getClass());
		if (field.isMultiple()) {
			List<Long> value;
			if (accesable) {
				value = (List<Long>) ((HasAccesablePropertyValues) entry).getPropertyValue(field.getFieldId());
			} else {
				value = (List<Long>) accessor.getPropertyValue(field,entry,null,session);
			}
			if (value != null) {
				concernedPeople.addAll(value);
			}
		} else {
			Long value;
			if (accesable) {
				value = (Long) ((HasAccesablePropertyValues) entry).getPropertyValue(field.getFieldId());
			} else {
				value = (Long) accessor.getPropertyValue( field, entry, null, session);
			}
			if (value != null) {
				concernedPeople.add(value);
			}
		}
	}
}
