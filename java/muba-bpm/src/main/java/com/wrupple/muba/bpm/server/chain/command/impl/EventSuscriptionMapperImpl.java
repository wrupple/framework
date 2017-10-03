package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
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

@Singleton
public class EventSuscriptionMapperImpl  implements EventSuscriptionMapper {

    private final SystemCatalogPlugin catalog;

    @Inject
    public EventSuscriptionMapperImpl(SystemCatalogPlugin catalog) {
        this.catalog = catalog;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        BroadcastContext context = (BroadcastContext) ctx;
        BroadcastEvent queueElement=context.getEventValue();
        List<FilterCriteria> explicitObservers = queueElement.getObserversValues();
        Event event = queueElement.getEventValue();


        CatalogEntry entry = event;
        if(entry instanceof HasEntryId){
            entry = (CatalogEntry) ((HasEntryId)entry).getEntryValue();
        }
        //FIXME read explicitly suscribed observers from event? (does observer dat belong in the event?)
        //FIXME Spawn Catalog context with system privileges:
        // if event stake holder has no permissions to see Observer or it's host, then the event wont get broadcasted to those poeple
        CatalogActionContext catalogContext = catalog.spawn(context.getRuntimeContext());
        CatalogDescriptor descriptor = catalog.getDescriptorForName(entry.getCatalogType(),catalogContext);

        Collection<FieldDescriptor> fields = descriptor.getFieldsValues();
        FieldAccessStrategy accessor = catalog.access();
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
            CatalogActionContext read = catalogContext;
            read.setCatalog(Host.CATALOG);
            read.setFilter(concernedPeopleClients);
            read.getCatalogManager().getRead().execute(read);
            if(read.getResults()!=null){
                Collection<? extends Host> results=read.getResults();
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
