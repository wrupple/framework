package com.wrupple.muba.event.chain.impl;

import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.server.service.impl.FilterDataUtils;
import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.domain.reserved.HasEntryId;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

/*
FIXME use in ExplicitSuscriptionMapperImpl
		names.add(new CatalogEntryImpl(Request.CATALOG, Request.CATALOG,
				"/static/img/notification.png"));*/

@Singleton
public class ImplicitSuscriptionMapper implements EventSuscriptionMapper {

    private final FieldAccessStrategy accessor;
    private final ServiceBus bus;

    @Inject
    public ImplicitSuscriptionMapper(FieldAccessStrategy catalog, ServiceBus bus) {
        this.accessor = catalog;
        this.bus = bus;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        BroadcastContext context = (BroadcastContext) ctx;
        BroadcastEvent queueElement=context.getEventValue();
        List<FilterCriteria> explicitObservers = queueElement.getObserversValues();
        Contract contract = queueElement.getEventValue();


        CatalogEntry entry = contract;
        if(entry instanceof HasEntryId){
            entry = (CatalogEntry) ((HasEntryId)entry).getEntryValue();
        }
        //FIXME read explicitly suscribed observers from contract? (does observer dat belong in the contract?)
        //FIXME read explicit suscriptors from ExplicitEventSuscription catalog? (in BusinessPluginImpl)
        //FIXME Spawn Catalog context with system privileges:
        // if contract stake holder has no permissions to see Observer or it's host, then the contract wont get broadcasted to those poeple

        CatalogDescriptor descriptor = queueElement.getCatalogDescriptor();


        Collection<FieldDescriptor> fields = descriptor.getFieldsValues();
        Set<Long> concernedPeople = null;
        Instrospection session = accessor.newSession(entry);
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
            CatalogActionRequestImpl read = new CatalogActionRequestImpl();
            read.setCatalog(Host.CATALOG);
            read.setFilter(concernedPeopleClients);
            read.setName(DataContract.READ_ACTION);
            Collection<? extends Host> results = bus.fireEvent(read,context.getRuntimeContext(),null);
            if(results!=null){
                context.addConcernedPeers(results);
            }
        }


        return CONTINUE_PROCESSING;
    }

    private void addFieldValuesToConcernedPeopleList(CatalogEntry entry, CatalogDescriptor descriptor,
                                                     FieldDescriptor field, Set<Long> concernedPeople, Instrospection session, FieldAccessStrategy accessor) throws ReflectiveOperationException {
        boolean accesable = HasAccesablePropertyValues.class.isAssignableFrom(entry.getClass());
        if (field.isMultiple()) {
            List<Long> value;
            if (accesable) {
                value = (List<Long>) ((HasAccesablePropertyValues) entry).getPropertyValue(field.getDistinguishedName());
            } else {
                value = (List<Long>) accessor.getPropertyValue(field,entry,null,session);
            }
            if (value != null) {
                concernedPeople.addAll(value);
            }
        } else {
            Long value;
            if (accesable) {
                value = (Long) ((HasAccesablePropertyValues) entry).getPropertyValue(field.getDistinguishedName());
            } else {
                value = (Long) accessor.getPropertyValue( field, entry, null, session);
            }
            if (value != null) {
                concernedPeople.add(value);
            }
        }
    }

}
