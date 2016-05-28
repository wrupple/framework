package com.wrupple.muba.bpm.server.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.bpm.domain.BusinessEvent;
import com.wrupple.muba.bpm.server.service.BusinessEventSuscriptionManager;
import com.wrupple.muba.catalogs.server.chain.command.PublishEvents.CatalogBroadcastData;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.desktop.client.services.CatalogTokenInterpret;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.HasAccesablePropertyValues;
import com.wrupple.vegetate.domain.Person;

public abstract class AbstractBusinessEventSuscriptionManager implements BusinessEventSuscriptionManager {

	private Provider<BPMPeer> bpmclientData;
	private final CatalogTokenInterpret catalogDescriptor;
	private final BPMClientDAO clientDao;
	private final CatalogPropertyAccesor accessor;
	/**
	 * 1 in shipped configuration
	 */
	private int outputChannel;
	public static boolean ENABLE_IMPLICIT_SUSCRIPTIONS = true;

	@Inject
	public AbstractBusinessEventSuscriptionManager(Provider<BPMPeer> bpmclientData, CatalogPropertyAccesor accessor, CatalogTokenInterpret catalogDescriptor,
			BPMClientDAO clientDao,@Named("muba.bpm.desktopMessageChannel") Integer outputChannel) {
		super();
		this.outputChannel=outputChannel;
		this.bpmclientData = bpmclientData;
		this.catalogDescriptor = catalogDescriptor;
		this.accessor = accessor;
		this.clientDao = clientDao;
	}

	public interface BPMClientDAO {

		List<BPMPeer> getOnlineClientsFromOwnerList(Collection<Long> concernedPeople);

	}

	@Override
	public List<BPMPeer> getConcernedClients(CatalogBroadcastData event, long domain) throws Exception {
		List<BPMPeer> concernedClients = getImplicitSuscriptions(event.getCatalogId(), event.getEntry(), domain);

		// TODO a CLIENT (person?) can suscribe explicitely
		// getExplicitSuscriptions(event,domain);
		// Offline suscribed parties (domains, roles, persons ) could get
		// notifications to read when user appears online
		// THE BPM SUSCRIPTION CATALOG MUST BE MEMORY-CACHED BASED to make it
		// more responsive, the dao itself does not provide any persistance but
		// rather rellies on overlying mechanisims to cache created entries and
		// retrive them by id. the dao can provider lists of cached entries
		// since the cache manager provides an unreliable collection of stored
		// keys

		return concernedClients;
	}

	/**
	 * 
	 * current implementations reads all "Person" fields from event's entry,
	 * looks for connected clients assigned to those people, and assumes they
	 * are concerned participants of the entry
	 * 
	 * @param catalogId
	 * @param entry
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	private List<BPMPeer> getImplicitSuscriptions(String catalogId, CatalogEntry entry, long domain) throws Exception {
		if (ENABLE_IMPLICIT_SUSCRIPTIONS) {
			// TODO think chat scenario

			CatalogDescriptor descriptor = catalogDescriptor.getDescriptorForName(catalogId, domain);

			Collection<FieldDescriptor> fields = descriptor.getFieldsValues();

			Set<Long> concernedPeople = null;
			Session session = accessor.newSession(entry);
			for (FieldDescriptor field : fields) {
				if (Person.CATALOG.equals(field.getForeignCatalogName())) {
					// TODO user may choose not to notify people listed in a
					// certain
					// field
					// field.getProperties().isBlindToBPMNotifications()
					if (concernedPeople == null) {
						concernedPeople = new HashSet<Long>(3);
					}
					addFieldValuesToConcernedPeopleList(entry, descriptor, field, concernedPeople, session);
				}
			}

			if (concernedPeople == null || concernedPeople.isEmpty()) {
				return null;
			} else {
				List<BPMPeer> clients = clientDao.getOnlineClientsFromOwnerList(concernedPeople);
				return clients;
			}
		} else {
			return null;
		}

	}

	private void addFieldValuesToConcernedPeopleList(CatalogEntry entry, CatalogDescriptor descriptor, FieldDescriptor field, Set<Long> concernedPeople,
			Session session) {
		boolean accesable = HasAccesablePropertyValues.class.isAssignableFrom(entry.getClass());
		if (field.isMultiple()) {
			List<Long> value;
			if (accesable) {
				value = (List<Long>) ((HasAccesablePropertyValues) entry).getPropertyValue(field.getFieldId());
			} else {
				value = (List<Long>) accessor.getPropertyValue(descriptor, field, entry, null, session);
			}
			if (value != null) {
				concernedPeople.addAll(value);
			}
		} else {
			Long value;
			if (accesable) {
				value = (Long) ((HasAccesablePropertyValues) entry).getPropertyValue(field.getFieldId());
			} else {
				value = (Long) accessor.getPropertyValue(descriptor, field, entry, null, session);
			}
			if (value != null) {
				concernedPeople.add(value);
			}
		}
	}

	@Override
	public void publishImplicitEvents(CatalogBroadcastData event, List<BPMPeer> concernedClients) throws Exception {
		boolean goAhead = actionRequiresNotifying(event.getAction());
		if (goAhead) {
			generateAndSendMessages(event.getCatalogId(), event.getAction(), concernedClients, event.getEntryAsSerializable(), event.getEntry().getIdAsString(),
					event.getDomain(), goAhead);
		}
	}

	@Override
	public void publishExplicitEvent(BusinessEvent event, List<BPMPeer> concernedClients) throws Exception {
		generateAndSendMessages(event.getCatalogId(), event.getName(), concernedClients, event, event.getCatalogEntryId(), event.getDomain(), false);
	}

	protected abstract void generateAndSendMessages(String catalogId, String eventName, List<BPMPeer> concernedClients, Object serializableEntry,
			String targetEntryId, long domain, boolean forceSend) throws Exception;

	@Override
	public void setPeerStakeHolder(String encodedId, long domainId, long personid) {
		BPMPeer desktopSession = encodedId == null ? null : getPeerByEncodedId(encodedId);
		if (desktopSession == null) {
			// no session available, nothing to invalidate for this client
		} else {
			// overwrite owner of session
			desktopSession.setStakeHolder(personid);
			desktopSession.setDomain(domainId);
			applyChanges(desktopSession);
		}
	}

	@Override
	public String getPeerEncodedId(BPMPeer desktopSession) {
		String regreso = Long.toString((Long)desktopSession.getId(), 36);
		return regreso;
	}

	protected Long decodeId(String rememberMeToken) {
		return Long.parseLong(rememberMeToken, 36);
	}

	/*
	 * CONFIGURE DESKTOP RESPONSE
	 */

	// configure session for a new client
	@Override
	public BPMPeer registerPeer(long domainId, long personid, int stakeHolderIndex, String userAgent, String publicKey, String privateKey) {
		BPMPeer newSession = bpmclientData.get();
		newSession.setAnonymouslyVisible(false);
		newSession.setDomain(domainId);
		// newSession.setDraft(false);
		newSession.setStakeHolder(personid);
		newSession.setTimestamp(new Date());
		newSession.setSubscriptionStatus(BPMPeer.STATUS_OFFLINE);

		newSession.setPublicKey(publicKey);
		newSession.setPrivateKey(privateKey);
		newSession.setChannel(outputChannel);
		newSession.setName(null);
		newSession.setExpirationDate(null);
		newSession.setAgent(userAgent);

		applyChanges(newSession);
		return newSession;
	}

	

	protected String attemptToReuseExistingChannel(BPMPeer desktopSession, CatalogDataAccessObject<BPMPeer> dsm, CatalogExcecutionContext context)
			throws Exception {

		List<BPMPeer> reusableCandidates = getReusableSocketCandidates(context.getDomain());
		if (reusableCandidates == null || reusableCandidates.isEmpty()) {
			return null;
		} else {
			boolean isUsable;
			String newsocketChannel = null;
			for (BPMPeer offlineSession : reusableCandidates) {
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

	/*
	 * private methods
	 */

	private boolean assertSocketIsUsable(BPMPeer desktopSession, CatalogDataAccessObject<BPMPeer> dsm, CatalogExcecutionContext context) {

	}

	protected abstract List<BPMPeer> getReusableSocketCandidates(long userDomain);

	

	private boolean actionRequiresNotifying(String targetAction) {
		return CatalogActionRequest.WRITE_ACTION.equals(targetAction) || CatalogActionRequest.CREATE_ACTION.equals(targetAction)
				|| CatalogActionRequest.DELETE_ACTION.equals(targetAction);
	}

}
