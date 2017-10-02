package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.generic.LookupCommand;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class PublishEventsImpl  extends LookupCommand   implements PublishEvents {
	
	private final EventSuscriptionChain chain;
	public static boolean ENABLE_IMPLICIT_SUSCRIPTIONS = true;

	@Inject
	public PublishEventsImpl(EventSuscriptionChain a,CatalogFactory factory) {
		super(factory);
		this.chain = a;
		super.setNameKey(PublishEvents.CHANNEL_DICTIONARY);
		super.setCatalogName(PublishEvents.CHANNEL_DICTIONARY);
	}




    @Override
    public boolean execute(Context ctx) throws Exception {
        BroadcastContext context = (BroadcastContext) ctx;
        EventBroadcastQueueElement queueElement=context.getEventValue();
        List<FilterCriteria> explicitObservers = queueElement.getObserversValues();
        Event event = queueElement.getEventValue();

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

        System.err.println("[Publish Request Events]");

        // UPDATE CLIENT'S ACTIVITY STATUS



        Host client = (Host) context.getRuntimeContext().getSession().getPeerValue();

        // String remembermeToken = peer.getPublicKey();

        if (client != null) {
            if (client.getSubscriptionStatus() != null
                    && client.getSubscriptionStatus().intValue() != CatalogPeer.STATUS_ONLINE) {
                client.setSubscriptionStatus(CatalogPeer.STATUS_ONLINE);

            }
        }

        List<CatalogEvent> broadcastable = context.getRootAncestor().getEvents();

        // BROADCAST EVENT'S TO CONCERNED CLIENTS

        if (broadcastable != null && !broadcastable.isEmpty()) {
            Collection<CatalogPeer> concernedClients;
            for (CatalogEvent data : broadcastable) {

                // push to online clients
                concernedClients = getConcernedClients(data, context);
                Session session = context.getCatalogManager().access().newSession(null);
                if (concernedClients != null) {
                    publishImplicitEvents(data, concernedClients,context,session);
                }

            }
        }

        return CONTINUE_PROCESSING;

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
		
		




	private Collection<CatalogPeer> getConcernedClients(CatalogEvent event, CatalogActionContext context) throws Exception {

		context.put(EventSuscriptionChain.CONCERNED_CLIENTS, new HashSet<CatalogPeer>());
		context.put(EventSuscriptionChain.CURRENT_EVENT, event);
		
		chain.execute(context);

		return (Collection<CatalogPeer>) context.get(EventSuscriptionChain.CONCERNED_CLIENTS);
	}


	private void publishImplicitEvents(CatalogEvent event, Collection<CatalogPeer> concernedClients,CatalogActionContext context, Session session) throws Exception {
		boolean goAhead = actionRequiresNotifying(event.getName());
		if (goAhead && concernedClients!=null  &&! concernedClients.isEmpty() ){
			for(CatalogPeer client : concernedClients){
				context.put(EventSuscriptionChain.CONCERNED_CLIENTS, client);
				context.put(EventSuscriptionChain.CURRENT_EVENT, event);
				context.put(CHANNEL_DICTIONARY, context.getCatalogManager().getDenormalizedFieldValue(client,CatalogPeer.CHANNEL_FIELD, session, context));
				super.execute(context);
			}
		}
	}

	
	private boolean actionRequiresNotifying(String targetAction) {
		return CatalogActionRequest.WRITE_ACTION.equals(targetAction)
				|| CatalogActionRequest.CREATE_ACTION.equals(targetAction)
				|| CatalogActionRequest.DELETE_ACTION.equals(targetAction);
	}
	
	/*
	 * CONFIGURE DESKTOP RESPONSE
	 *
	protected String attemptToReuseExistingChannel(CatalogPeer desktopSession, CatalogDataAccessObject<CatalogPeer> dsm,
			CatalogActionContext context) throws Exception {

		List<CatalogPeer> reusableCandidates = getReusableSocketCandidates(context.getDomain());
		if (reusableCandidates == null || reusableCandidates.isEmpty()) {
			return null;
		} else {
			boolean isUsable;
			String newsocketChannel = null;
			for (CatalogPeer offlineSession : reusableCandidates) {
				isUsable = checkCachedChannelUsability(offlineSession);

				if (isUsable) {
					newsocketChannel = offlineSession.getPublicKey();
					Date newexpirationDate = offlineSession.getExpirationDate();

					offlineSession.setPublicKey(null);
					offlineSession.setExpirationDate(null);
					applyChanges(offlineSession);
					desktopSession.setPublicKey(newsocketChannel);
					desktopSession.setExpirationDate(newexpirationDate);
					break;
				} else {
					// all reusable channels have an unexpired status and
					// unexpired date of channel expiration
					newsocketChannel = null;
				}

			}
			if (newsocketChannel == null) {
				return null;
			} else {
				return newsocketChannel;
			}

		}
	}

	

	private boolean assertSocketIsUsable(CatalogPeer desktopSession, CatalogDataAccessObject<CatalogPeer> dsm,
			CatalogActionContext context) {

	}

	protected abstract List<CatalogPeer> getReusableSocketCandidates(long userDomain);

	*/

}
