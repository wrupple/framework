package com.wrupple.vegetate.client.services.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.catalogs.client.services.ClientCatalogCacheManager;
import com.wrupple.muba.catalogs.client.services.evaluation.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.client.services.impl.*;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.client.services.logic.CatalogCache;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.impl.SimpleFilterableDataProvider;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.domain.BPMPeer;
import com.wrupple.muba.worker.shared.event.EntriesDeletedEvent;
import com.wrupple.muba.worker.shared.event.EntryCreatedEvent;
import com.wrupple.muba.worker.shared.event.EntryUpdatedEvent;
import com.wrupple.vegetate.client.services.CatalogEntryAssembler;
import com.wrupple.vegetate.client.services.CatalogServiceSerializer;
import com.wrupple.vegetate.client.services.CatalogVegetateChannel;
import com.wrupple.vegetate.client.services.RemoteStorageUnit;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.PersistentImageMetadata;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Glue implementation for catalog user oriented object storage services and
 * vegetate's remote server communication via javascript objectss
 * 
 * @author japi
 *
 */
public class VegetateStorageUnitImpl implements RemoteStorageUnit<JsCatalogActionRequest, JsCatalogEntry> {

	class LocalTimerLease extends Timer {

		@Override
		public void run() {
			if (channel != null) {
				channel.flush();
			}
		}

	}
	
	class RetryIncrementalOperation extends DataCallback<List<JsCatalogEntry>> {
		CatalogCache cache;
		JsFilterData filter;
		StateTransition<List<JsCatalogEntry>> callback;
		EventBus bus;
		int cachedResultsBeforeLoad;
		private CatalogDescriptor descriptor;
		private CatalogVegetateChannel channel;
		private String domainNamespace;

		public RetryIncrementalOperation(String domainNamespace, CatalogDescriptor descriptor, CatalogCache cache, int cachedResultsBeforeLoad,
				JsFilterData filter, StateTransition<List<JsCatalogEntry>> callback, EventBus bus, CatalogVegetateChannel channel) {
			super();
			this.domainNamespace=domainNamespace;
			this.descriptor = descriptor;
			this.cache = cache;
			this.filter = filter;
			this.callback = callback;
			this.bus = bus;
			this.cachedResultsBeforeLoad = cachedResultsBeforeLoad;
			this.channel = channel;
		}

		@Override
		public void execute() {
			fetchIncremental(domainNamespace, descriptor, cache, filter, cachedResultsBeforeLoad, callback, bus, channel);
		}
	}


	private  class RetryFetchByQuery extends DataCallback<List<JsCatalogEntry>> {
		CatalogCache cache;
		JsFilterData filter;
		CatalogDescriptor catalog;
		StateTransition<List<JsCatalogEntry>> callback;
		EventBus bus;
		CatalogVegetateChannel channel;
		int previousCacheSize;
		private String domainNamespace;
		
		public RetryFetchByQuery(String domainNamespace, CatalogCache cache, JsFilterData filter, CatalogDescriptor catalog,
				StateTransition<List<JsCatalogEntry>> callback, EventBus bus, CatalogVegetateChannel channel, int previousCacheSize) {
			super();
			this.domainNamespace=domainNamespace;
			this.cache = cache;
			this.filter = filter;
			this.catalog = catalog;
			this.callback = callback;
			this.bus = bus;
			this.channel = channel;
			this.previousCacheSize = previousCacheSize;
		}

		@Override
		public void execute() {
			fetchByQuerying(domainNamespace, cache, filter, catalog, callback, bus, channel, previousCacheSize);			
		}
		
		
	}
	class PutInCache extends DataCallback<List<JsCatalogEntry>> {
		private final CatalogCache cache;
		private final FilterData filter;
		private int startingIndex;

		public PutInCache(CatalogCache cache, FilterData filter, int startingIndex) {
			super();
			this.cache = cache;
			this.filter = filter;
			this.startingIndex = startingIndex;
		}

		@Override
		public void execute() {
			if (result != null && !result.isEmpty()) {

				if (startingIndex >= 0) {
					int sizeBefore = cache.length();
					cache.put(startingIndex, JsArrayList.unwrap(result));
					if (cache.length() > sizeBefore) {
						cache.setLastEntryCursor(filter.getCursor());
					}
				} else {
					for (JsCatalogEntry key : result) {
						cache.put(key.getId(), key);
					}
				}
			}

		}

	}

    private void openNewChannel(final StateTransition<CatalogVegetateChannelImpl> callback) {

        if (getAvailableChannel() == null) {
            // TODO preload all available service manifests the same way we
            // preload some catalog descriptors?
            if (manifest == null) {
                // Catalog service manifest is not loaded

                if (temp == null) {
                    // create a temporary channel setRuntimeContext an empty manifest to load
                    // the real manifest
                    JsVegetateServiceManifest emptyManifest = JavaScriptObject.createObject().cast();
                    emptyManifest.setServiceName(CatalogServiceManifest.SERVICE_NAME);
                    temp = new CatalogVegetateChannelImpl(host, ssl, emptyManifest, bus, serializer);
                }

                if (tempcallback == null) {
                    tempcallback = callback;
                    temp.getServiceManifest(new DataCallback<JsVegetateServiceManifest>() {
                        @Override
                        public void execute() {
                            manifest = result;
                            temp = null;
                            tempcallback = null;

                            callback.setResultAndFinish(new CatalogVegetateChannelImpl(host, ssl, manifest, bus, serializer));
                        }
                    });
                } else {
                    tempcallback.hook(callback);
                }

            } else {
                callback.setResultAndFinish(new CatalogVegetateChannelImpl(host, ssl, manifest, bus, serializer));
            }
        } else {
            callback.setResultAndFinish(getAvailableChannel());
        }
    }

    private void readFilteredFromLocalCache(String domainNamespace, CatalogDescriptor descriptor, JsFilterData filter, StateTransition<List<JsCatalogEntry>> callback,
                                            EventBus bus, CatalogVegetateChannel channel) {
        // TODO wrapp callback to evaluate ephemeral fields setRuntimeContext a lower cache
        // policy (default is to evaluate on every read)
        String catalogid = descriptor.getCatalogId();

        CatalogCache cache = ccm.getCache((JsCatalogDescriptor) descriptor, filter);

        if (cache == null) {
            // no choice but to call the server without further ado
            remoteRead(domainNamespace, catalogid, filter, callback, bus, channel);
        } else {
            JsArray<JsFilterCriteria> criteriaArray = filter.getFilterArray();
            boolean incrementalCriteria = GWTUtils.getAttributeAsBoolean(filter, SimpleFilterableDataProvider.LOCAL_FILTERING)
                    || descriptor.getCachePolicy() == null || CatalogActionRequest.FULL_CACHE.equals(descriptor.getCachePolicy());

            if (incrementalCriteria || criteriaArray == null || criteriaArray.length() == 0) {
                if (criteriaArray.length() == 1) {
                    // if the only available criteria is id field
                    JsFilterCriteria onlyAvailableCriteria = criteriaArray.get(0);
                    String onlyAvailableCriteriaField = onlyAvailableCriteria.getPath(0);
                    if (descriptor.getKeyField().equals(onlyAvailableCriteriaField)) {
                        // check if we can satisfy all id's setRuntimeContext cache
                        JsArray<JsCatalogEntry> satisfiedEntries = delegate.getCachedEntriesByKeyCriteria(cache, onlyAvailableCriteria, bus);
                        if (satisfiedEntries == null) {
                            // Unable to satisfy criteria setRuntimeContext cached
                            // entries
                            // SKIP CACHE
                            callback.hook(new PutInCache(cache, filter, -1));
                            filter.setConstrained(false);
                            remoteRead(domainNamespace, catalogid, filter, callback, bus, channel);
                        } else {
                            // Criteria was satisfied setRuntimeContext available entries
                            // Use cached results
                            List<JsCatalogEntry> result = JsArrayList.arrayAsList(satisfiedEntries);
                            callback.setResultAndFinish(result);
                        }
                    } else {
                        fetchIncremental(domainNamespace, descriptor, cache, copyData(filter, descriptor), -1, callback, bus, channel);
                    }
                } else {
                    fetchIncremental(domainNamespace, descriptor, cache, copyData(filter, descriptor), -1, callback, bus, channel);
                }

            } else {
                if (criteriaArray.length() == 1) {
                    // if the only available criteria is id field
                    JsFilterCriteria onlyAvailableCriteria = criteriaArray.get(0);
                    String onlyAvailableCriteriaField = onlyAvailableCriteria.getPath(0);
                    if (descriptor.getKeyField().equals(onlyAvailableCriteriaField)) {
                        // check if we can satisfy all id's setRuntimeContext cache
                        JsArray<JsCatalogEntry> satisfiedEntries = delegate.getCachedEntriesByKeyCriteria(cache, onlyAvailableCriteria, bus);
                        if (satisfiedEntries == null) {
                            // Unable to satisfy criteria setRuntimeContext cached
                            // entries
                            // SKIP CACHE
                            callback.hook(new PutInCache(cache, filter, -1));
                            filter.setConstrained(false);
                            remoteRead(domainNamespace, catalogid, filter, callback, bus, channel);
                        } else {
                            // Criteria was satisfied setRuntimeContext available entries
                            // Use cached results
                            List<JsCatalogEntry> result = JsArrayList.arrayAsList(satisfiedEntries);
                            callback.setResultAndFinish(result);
                        }
                    } else {
                        fetchByQuerying(domainNamespace, cache, filter, descriptor, callback, bus, channel, -1);
                    }
                } else {
                    fetchByQuerying(domainNamespace, cache, filter, descriptor, callback, bus, channel, -1);
                }
            }
        }
    }

	class SingleEntryFieldEvaluatingCallback extends DataCallback<JsCatalogEntry> {

		final StateTransition<JsCatalogEntry> callback;
		private CatalogDescriptor catalog;

		public SingleEntryFieldEvaluatingCallback(StateTransition<JsCatalogEntry> callback, CatalogDescriptor result) {
			this.callback = callback;
			this.catalog = result;
		}

		@Override
		public void execute() {
			if (result == null) {
				callback.setResultAndFinish(null);
			} else {
				JsCatalogEntry processedResult = result.cast();
				Collection<FieldDescriptor> fields = catalog.getOwnedFieldsValues();
				String formula;
				for (FieldDescriptor field : fields) {
					if (field.isEphemeral() && field.getFormula() != null) {
						formula = field.getFormula();
						delegate.eval(catalog, field, processedResult, formula);
					}
				}
				callback.setResultAndFinish(processedResult);
			}
		}
	}

	class LinkChildren extends DataCallback<JsCatalogEntry> {

		final StateTransition<JsCatalogEntry> callback;
		final EventBus bus;
		private CatalogCache cache;
		private CatalogVegetateChannel channel;
		private String domainNamespace;
		private CatalogDescriptor catalog;

		public LinkChildren(String domainNamespace, StateTransition<JsCatalogEntry> callback, EventBus bus, CatalogCache cache, CatalogVegetateChannel channel, CatalogDescriptor result) {
			this.callback = callback;
			this.catalog=result;
			this.domainNamespace=domainNamespace;
			this.bus = bus;
			this.cache = cache;
			this.channel = channel;
		}

		@Override
		public void execute() {
			// TODO support children assembly (all levels deep on indexed trees)
			JsContentNode node = result.cast();
			linkWithChildren(domainNamespace, node, callback, bus, cache, channel,catalog);
		}
	}

	class JoinAndEval extends DataCallback<List<JsCatalogEntry>> {
		private final JsFilterData filter;
		private final CatalogDescriptor descriptor;

		public JoinAndEval(JsFilterData filter, CatalogDescriptor descriptor) {
			super();
			this.filter = filter;
			this.descriptor = descriptor;
		}

		@Override
		public void execute() {
			if (result != null && !result.isEmpty()) {

				JsArray<JsCatalogEntry> fullCache = JsArrayList.unwrap(result);
				JsArray<JsArrayString> joins = filter.getJoinsArray(false);
				delegate.processJoinData(fullCache, filter, joins, descriptor, ccm);

				Collection<FieldDescriptor> fields = descriptor.getOwnedFieldsValues();
				String formula;
				for (FieldDescriptor field : fields) {
					if (field.getFormula() != null) {
						formula = field.getFormula();
						delegate.eval(descriptor, field, fullCache, formula);
					}
				}

			}

		}

	}


	private final EventBus bus;
	private final ClientCatalogCacheManager ccm;
	private final CatalogEvaluationDelegate delegate;
	private final CatalogEntryAssembler assembler;
	private final CatalogServiceSerializer serializer;

	private final LocalTimerLease lease;
	private final boolean ssl;
	private BPMPeer host;

	/*
	 * Manifest loading
	 */
	private CatalogVegetateChannelImpl channel;
	private JsVegetateServiceManifest manifest;
	private CatalogVegetateChannelImpl temp;
	private StateTransition<CatalogVegetateChannelImpl> tempcallback;

	@Inject
	public VegetateStorageUnitImpl(ClientCatalogCacheManager ccm, CatalogEntryAssembler assembler, CatalogServiceSerializer serializer, EventBus eventBus, CatalogEvaluationDelegate delegate) {
		super();
		this.ccm = ccm;
		this.delegate=delegate;
		this.assembler = assembler;
		this.bus = eventBus;
		lease = new LocalTimerLease();
		this.serializer = serializer;
		lease.scheduleRepeating(500);
		ssl = Window.Location.getProtocol().equals("https");
		CacheInvalidationHandler invalidator = new CacheInvalidationHandler();
		bus.addHandler(EntryUpdatedEvent.getType(), new CacheUpdateHandler(bus));
		bus.addHandler(EntryCreatedEvent.getType(), invalidator);
		bus.addHandler(EntriesDeletedEvent.getType(), invalidator);
	}

	private CatalogVegetateChannelImpl getAvailableChannel() {
		return channel;
	}

    class CacheUpdateHandler implements EventHandler {
        final EventBus bus;

        public CacheUpdateHandler(EventBus bus) {
            super();
            this.bus = bus;
        }

        @Override
        public void onEntryUpdated(EntryUpdatedEvent e) {
            JsCatalogEntry entry = e.entry.cast();
            String catalog = entry.getCatalog();
            String id = entry.getId();
            CatalogCache affectedCacheUnit = ccm.getIdentityCache(catalog);
            if (affectedCacheUnit != null) {
                JavaScriptObject cachedEntry = affectedCacheUnit.read(id);
                if (cachedEntry != null) {
                    affectedCacheUnit.put(id, entry);
                    StateTransition<JsCatalogEntry> callback = new EntryRetrivingServiceHook(catalog, bus);
                    callback.setResultAndFinish(entry);
                }
			}
		}

	}

	@Override
	public void callStringArrayService(final JsCatalogActionRequest action, final StateTransition callback) {
		final StateTransition<JsCatalogActionResult> cb = new DataCallback<JsCatalogActionResult>() {

			@Override
			public void execute() {
				if (result == null) {
					callback.setResultAndFinish(null);
				} else {
					JsArrayString regreso = result.asList();
					callback.setResultAndFinish(regreso);
				}
			}
		};
		if (getAvailableChannel() == null) {
			openNewChannel(new DataCallback<CatalogVegetateChannelImpl>() {
				@Override
				public void execute() {
					channel = result;
					getAvailableChannel().send(action, cb);
				}
			});
		} else {
			getAvailableChannel().send(action, cb);
		}
	}

	@Override
	public <T extends JavaScriptObject> void callGenericService(final JsCatalogActionRequest action, final StateTransition<T> callback) {
		// TODO what server to call the generic service on (CURRENTLY THE owner
		// server manages peers)

		final StateTransition<JsCatalogActionResult> cb = new DataCallback<JsCatalogActionResult>() {

			@Override
			public void execute() {
				if (result == null) {
					callback.setResultAndFinish(null);
				} else {
					T regreso = result.cast();
					callback.setResultAndFinish(regreso);
				}

			}
		};
		if (getAvailableChannel() == null) {
			openNewChannel(new DataCallback<CatalogVegetateChannelImpl>() {
				@Override
				public void execute() {
					channel = result;
					getAvailableChannel().send(action, cb);
				}
			});
		} else {
			getAvailableChannel().send(action, cb);
		}
	}

	@Override
	public <T extends JavaScriptObject> void read(final String domainNamespace, final String id, final CatalogDescriptor catalog, final StateTransition<T> callback) {
		assert id != null;
		if (getAvailableChannel() == null) {
			openNewChannel(new DataCallback<CatalogVegetateChannelImpl>() {
				@Override
				public void execute() {
					channel = result;
					readIdFromLocalCache(domainNamespace, id, catalog, (StateTransition) callback);
				}
			});
		} else {
			readIdFromLocalCache(domainNamespace, id, catalog, (StateTransition) callback);
		}
	}

	@Override
	public void read(String domainNamespace, List<String> ids, CatalogDescriptor catalog, StateTransition<List<JsCatalogEntry>> callback) {
		JsFilterData filter = JsFilterData.createSingleFieldFilter(JsCatalogEntry.ID_FIELD, ids);

		read(domainNamespace, filter, catalog, callback);
	}

	@Override
	public <T extends JavaScriptObject> void read(final String domainNamespace, final JsFilterData filter, final CatalogDescriptor catalog, final StateTransition<List<T>> callback) {

		if (getAvailableChannel() == null) {
			openNewChannel(new DataCallback<CatalogVegetateChannelImpl>() {
				@Override
				public void execute() {
					channel = result;
					readFilteredFromLocalCache(domainNamespace, catalog, filter, (StateTransition)  callback, bus, channel);
				}
			});
		} else {
			readFilteredFromLocalCache(domainNamespace, catalog, filter, (StateTransition)  callback, bus, channel);
		}

	}

	@Override
	public void create(final String domainNamespace,final JsCatalogEntry entry,final CatalogDescriptor catalog, final StateTransition<JsCatalogEntry> callback) {

		if (getAvailableChannel() == null) {
			//openNewChannel(new ChannelCreateEntryCallback(entry, catalog.getCatalogId(), callback));
			openNewChannel(new DataCallback<CatalogVegetateChannelImpl>() {

				@Override
				public void execute() {
					channel = result;
					createEntry(domainNamespace,entry, catalog.getCatalogId(), callback, bus, result);
				}
			});
		} else {
			createEntry(domainNamespace,entry, catalog.getCatalogId(), callback, bus, getAvailableChannel());
		}

	}


	@Override
	public void update(final String namespace,final String id, final JsCatalogEntry entry, final CatalogDescriptor catalog, final StateTransition<JsCatalogEntry> callback) {
		assert id != null;
		assert entry != null;
		if (getAvailableChannel() == null) {
			openNewChannel(new DataCallback<CatalogVegetateChannelImpl>() {
				@Override
				public void execute() {
					channel = result;
					updateEntry(namespace,entry, id, catalog.getCatalogId(), callback, bus, result);
				}
			});
		} else {
			updateEntry(namespace,entry, id, catalog.getCatalogId(), callback, bus, getAvailableChannel());
		}

	}

	@Override
	public void delete(final String nameSpace, final String id, final CatalogDescriptor catalog, final StateTransition<JsCatalogEntry> callback) {
		if (getAvailableChannel() == null) {
			openNewChannel(new DataCallback<CatalogVegetateChannelImpl>() {
				@Override
				public void execute() {
					channel = result;
					deleteEntry(nameSpace, id, catalog.getCatalogId(), callback, bus, result);
				}
			});
		} else {
			deleteEntry(nameSpace, id, catalog.getCatalogId(), callback, bus, getAvailableChannel());
		}

	}

	@Override
	public Transaction startTransaction() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String buildServiceUrl(JsCatalogActionRequest request) {
		if (getAvailableChannel() == null) {
			if (manifest == null) {
				throw new NullPointerException("catalog service manifest not loaded");
			}
			channel = new CatalogVegetateChannelImpl(host, ssl, manifest, bus, serializer);
		}

		return getAvailableChannel().buildServiceUrl(request);
	}

	@Override
	public SafeUri getImageUri(String domain, String imageId, int customSize) {
		JsCatalogActionRequest request = JsCatalogActionRequest.newRequest(domain, CatalogActionRequest.LOCALE, PersistentImageMetadata.CATALOG,
				CatalogActionRequest.READ_ACTION, imageId, String.valueOf(customSize), null, null);
		String url = buildServiceUrl(request);
		SafeUri safe = UriUtils.fromString(url);
		return safe;
	}

	@Override
	public void assertManifest(final DataCallback<Void> dataCallback) {
		openNewChannel(new DataCallback<CatalogVegetateChannelImpl>() {
			@Override
			public void execute() {
				dataCallback.setResultAndFinish(null);
			}
		});
	}

	@Override
	public void setHost(BPMPeer peer) {
		this.host = peer;
	}
	
	private void createEntry(String domainNamespace,JsCatalogEntry entry, String catalog, StateTransition<JsCatalogEntry> callback, EventBus bus, CatalogVegetateChannel channel) {
		callback.hook(new EntryCreationServiceHook(bus,host.getHost(),domainNamespace));
		StateTransition<List<JsCatalogEntry>> singletonSelector = new SingletonListDataCallbackWrapper<JsCatalogEntry>(callback);
		StateTransition<JsCatalogActionResult> vegetateCallback = new ResultSetCallbackWrapper(ccm, assembler, singletonSelector);
		
		JsCatalogActionRequest action = JsCatalogActionRequest.newRequest(domainNamespace, CatalogActionRequest.LOCALE, catalog, CatalogActionRequest.CREATE_ACTION, null, null, entry, null);

		channel.send(action, vegetateCallback);
		
	}
	
	private void updateEntry(final String domainToken,JsCatalogEntry data, String originalEntry, String catalog, StateTransition<JsCatalogEntry> onDone,final EventBus bus,
			final CatalogVegetateChannel channel) {
		onDone.hook(new DataCallback<JsCatalogEntry>() {
			@Override
			public void execute() {
				if(result!=null){
					bus.fireEvent(new EntryUpdatedEvent(result, host.getHost(), domainToken));
				}
			}
		});
		StateTransition<List<JsCatalogEntry>> singletonSelector = new SingletonListDataCallbackWrapper<JsCatalogEntry>(onDone);
		StateTransition<JsCatalogActionResult> vegetateCallback = new ResultSetCallbackWrapper(ccm, assembler, singletonSelector);
		
		JsCatalogActionRequest action = JsCatalogActionRequest.newRequest(domainToken, CatalogActionRequest.LOCALE, catalog, CatalogActionRequest.WRITE_ACTION, originalEntry, null, data, null);

		channel.send(action, vegetateCallback);
	}

	private void deleteEntry(String domainNamespace, String id, String catalog, StateTransition<JsCatalogEntry> callback, EventBus bus,
			CatalogVegetateChannel channel) {
		callback.hook(new EntryDeleteServiceHook(catalog, Collections.singletonList(id), bus, host.getHost(),domainNamespace));

		StateTransition<List<JsCatalogEntry>> singletonSelector = new SingletonListDataCallbackWrapper<JsCatalogEntry>(callback);
		StateTransition<JsCatalogActionResult> vegetateCallback = new ResultSetCallbackWrapper(ccm, assembler, singletonSelector);

		JsCatalogActionRequest action = JsCatalogActionRequest.newRequest(domainNamespace, CatalogActionRequest.LOCALE, catalog,
				CatalogActionRequest.DELETE_ACTION, id, null, null, null);
		channel.send(action, vegetateCallback);
	}
	
	private void remoteRead(String domainNamespace, String entryId, String catalog, StateTransition<JsCatalogEntry> callback, EventBus eventBus, CatalogVegetateChannel channel) {
		callback.hook(new EntryRetrivingServiceHook(catalog, eventBus));
		StateTransition<List<JsCatalogEntry>> singletonSelector = new SingletonListDataCallbackWrapper<JsCatalogEntry>(callback);
		StateTransition<JsCatalogActionResult> vegetateCallback = new ResultSetCallbackWrapper(ccm, assembler, singletonSelector);
		
		JsCatalogActionRequest action = JsCatalogActionRequest.newRequest(domainNamespace, CatalogActionRequest.LOCALE, catalog, CatalogActionRequest.READ_ACTION, entryId, null, null, null);
		channel.send(action, vegetateCallback);

	}

	private void remoteRead(String domainNamespace, String catalogid, JsFilterData filter, StateTransition<List<JsCatalogEntry>> callback, EventBus bus, CatalogVegetateChannel channel) {
		callback.hook(new EntryRetrivingServiceHook.List(catalogid, bus));
		StateTransition<JsCatalogActionResult> vegetateCallback = new ResultSetCallbackWrapper(ccm, assembler, callback,filter);
		
		JsCatalogActionRequest action = JsCatalogActionRequest.newRequest(domainNamespace, CatalogActionRequest.LOCALE, catalogid, CatalogActionRequest.READ_ACTION, null, null, null, filter);
		channel.send(action, vegetateCallback);
	}
	
	private void readIdFromLocalCache(String domainNamespace,String entryId,CatalogDescriptor result, StateTransition<JsCatalogEntry> cbk){
		StateTransition<JsCatalogEntry> callback = new SingleEntryFieldEvaluatingCallback(cbk, result);

		CatalogCache cache = ccm.getIdentityCache(result.getCatalogId());
		if (cache == null) {
			remoteRead(domainNamespace, entryId, result.getCatalogId(), callback, bus, channel);
		} else {

			boolean isIndexedTree = result.getFieldDescriptor(ContentNode.CHILDREN_TREE_LEVEL_INDEX) != null;
			if (isIndexedTree) {
				callback = new LinkChildren(domainNamespace, callback, bus, cache, channel,result);
			}
			JavaScriptObject cachedResult = cache.read(entryId);
			if (cachedResult == null) {
				callback.hook(new EntryRetrivingCachingHook(cache));
				remoteRead(domainNamespace, entryId, result.getCatalogId(), callback, bus, channel);
			} else {
				JsCatalogEntry r = cachedResult.cast();
				callback.setResult(r);
				callback.execute();
			}
		}
	}

    class CacheInvalidationHandler implements EventHandler {

        @Override
        public void onEntryCreated(EntryCreatedEvent e) {
            JsCatalogKey createdEntry = e.entry;
            String catalog = createdEntry.getCatalog();
            doInvalidation(catalog, createdEntry);
            if (!ccm.isInvalidationAvailable()) {
                try {
                    ccm.getIdentityCache(catalog).forceAppend(createdEntry);
                } catch (Exception ex) {
                    GWT.log("unable to append created wnetry while cache invalidation is unavailable", ex);
                }

            }
        }

        private void doInvalidation(String catalog, JsCatalogKey createdEntry) {
            ccm.invalidateCache(catalog);
        }

        @Override
        public void onEntriesDeleted(EntriesDeletedEvent e) {
            // TODO is it necesary to invalidate the entire cache or just use
            // cache.delete?
            String catalog = e.catalog;
            if (ccm.isInvalidationAvailable()) {
                doInvalidation(catalog, null);
            } else {
                ccm.forceInvalidation(catalog);
            }

		}

    }

	private void fetchByQuerying(String domainNamespace, CatalogCache cache, JsFilterData filter, CatalogDescriptor catalog,
			StateTransition<List<JsCatalogEntry>> callback, EventBus bus, CatalogVegetateChannel channel, int previousCacheSize) {
		// we may safely assume this catalog's cache policy is by-query
		if (cache.length() >= (filter.getStart() + filter.getLength()) || cache.length() == previousCacheSize) {
			JsArray<JsCatalogEntry> satisfiedEntries = cache.read(filter.getStart(), filter.getLength());
			List<JsCatalogEntry> result = JsArrayList.arrayAsList(satisfiedEntries);
			callback.hook( new JoinAndEval(filter, catalog));
			callback.setResultAndFinish(result);
		} else {
			DataCallback<List<JsCatalogEntry>> retry = new RetryFetchByQuery(domainNamespace, cache, filter, catalog, callback, bus, channel, cache.length());
			loadMoreResults(domainNamespace, catalog.getCatalogId(), cache, retry, bus, filter.getJoinsArray(false), channel, filter, null);
		}
	}


	private JsFilterData copyData(JsFilterData original, CatalogDescriptor descriptor) {
		JsFilterData filter = JsFilterData.copy(original, false);

		JsArray<JsFilterCriteria> filterCriterias = filter.getFilterArray();
		int i = 0;
		JsFilterCriteria criteria;
		JsArrayMixed rawValues;
		FieldDescriptor fieldd;
		String field;
		while (i < filterCriterias.length()) {
			criteria = filterCriterias.get(i);
			// TODO suport value translation on nested path entries?
			if (criteria.hasValues()) {
				if (criteria.getPathArray().length() == 1) {
					field = criteria.getPath(0);
					rawValues = criteria.getValuesArray();
					fieldd = descriptor.getFieldDescriptor(field);
					if (fieldd == null) {
						removeRangeImpl(filterCriterias, i);
					} else {
						JsArrayMixed values = delegate.getTranslatedFilterValues(rawValues, fieldd.getDataType(), criteria.getOperator());
						criteria.setValues(values);
						i++;
					}
				} else {
					i++;
				}
			} else {
				removeRangeImpl(filterCriterias, i);
			}
		}

		return filter;
	}

	private void fetchIncremental(String domainNamespace, CatalogDescriptor descriptor, CatalogCache cache, JsFilterData filter,
			int cacheSizeBeforeRequest, StateTransition<List<JsCatalogEntry>> callback, EventBus bus, CatalogVegetateChannel channel) {

		int length = filter.getLength();

		JsArray<JsCatalogEntry> cachedFilteredResults =delegate.getCachedFilteredEntries(descriptor, cache, filter, bus, ccm);

		int currentCacheSize = cache.length();
		int filteredResultsLength = cachedFilteredResults.length();

		boolean cacheDidNotGrow = cacheSizeBeforeRequest == currentCacheSize;
		boolean rangeHasRequestedSize = filteredResultsLength == length;

		if (rangeHasRequestedSize || cache.isComplete()) {
			callback.setResultAndFinish(JsArrayList.arrayAsList(cachedFilteredResults));
		} else {
			if (cacheDidNotGrow) {
				// cache didnt grow
				// the full table in cache now
				callback.setResultAndFinish(JsArrayList.arrayAsList(cachedFilteredResults));
				cache.setComplete(true);
			} else {
				RetryIncrementalOperation retry = new RetryIncrementalOperation(domainNamespace, descriptor, cache, currentCacheSize, filter, callback, bus, channel);
				JsArray<JsArrayString> joins = filter.getJoinsArray(false);
				loadMoreResults(domainNamespace, descriptor.getCatalogId(), cache, retry, bus, joins, channel, null, null);
			}

		}

	}
	

	private final native void removeRangeImpl(JsArray<JsFilterCriteria> filterCriterias, int index) /*-{
																									filterCriterias.splice(index, index);
																									}-*/;

	private void loadMoreResults(String domainNamespace, String catalogid, CatalogCache cache, DataCallback<List<JsCatalogEntry>> retry, EventBus bus,
			JsArray<JsArrayString> joins, CatalogVegetateChannel channel, JsFilterData filter, DataCallback<List<JsCatalogEntry>> preivoushooks) {
		int start = cache.length();

		if (filter == null) {
			filter = JsFilterData.newFilterData();
		}

		filter.setConstrained(true);
		filter.setStart(start);
		filter.setCursor(cache.getLastEntryCursor());
		filter.setLength(FilterData.DEFAULT_INCREMENT * 2);
		filter.setJoins(joins);

		// runs before
		retry.hook(new PutInCache(cache, filter, start));
		filter.setCursor(cache.getLastEntryCursor());
		if (preivoushooks != null) {
			retry.hook(preivoushooks);
		}
		remoteRead(domainNamespace, catalogid, filter, retry, bus, channel);
	}

	

/*	private JsArray<JsCatalogEntry> copy(JsArray<JsCatalogEntry> fullCache) {
		if (fullCache == null) {
			return null;
		} else {
			JsArray<JsCatalogEntry> regreso = JavaScriptObject.createArray().cast();
			regreso.setLength(fullCache.length());
			for (int i = 0; i < fullCache.length(); i++) {
				regreso.set(i, fullCache.get(i));
			}
			return regreso;
		}
	}*/



	public <T extends JsContentNode> void linkWithChildren(String domainNamespace, final T node, final StateTransition<JsCatalogEntry> linkCallback,
			EventBus bus, final CatalogCache cache, CatalogVegetateChannel channel,CatalogDescriptor catalog) {
		JsArrayString childrenIds = JavaScriptObject.createArray().cast();
		gatherAllAvailableChildrenRequirements(node, childrenIds);
		if (childrenIds.length() > 0) {
			JsFilterData filter = JsFilterData.createSingleFieldFilter(ContentNode.ID_FIELD, childrenIds);
			StateTransition<List<JsContentNode>> readCallback = new DataCallback<List<JsContentNode>>() {
				@Override
				public void execute() {
					linkAllAvailableCachedChildren(node, cache);
					linkCallback.setResultAndFinish(node);
				}

				private void linkAllAvailableCachedChildren(JsContentNode node, final CatalogCache cache) {
					JsArray<JsContentNode> childrenValues = node.getChildrenValues();
					if (childrenValues == null) {
						JsArrayString children = node.getChildren();
						if (children != null && children.length() > 0) {
							childrenValues = JavaScriptObject.createArray().cast();

							String childId;
							JavaScriptObject child;
							JsContentNode childNode;
							for (int i = 0; i < children.length(); i++) {
								childId = children.get(i);
								child = cache.read(childId);
								if (child != null) {
									childNode = child.cast();
									childrenValues.push(childNode);
									linkAllAvailableCachedChildren(childNode, cache);
								}
							}
							node.setChildrenValues(childrenValues);
						}
					}
				}
			};
			readFilteredFromLocalCache(domainNamespace, catalog, filter, (StateTransition) readCallback, bus, channel);
		} else {
			linkCallback.setResultAndFinish(node);
		}

	}

	private void gatherAllAvailableChildrenRequirements(JsContentNode node, JsArrayString array) {
		JsArray<JsContentNode> childrenValues = node.getChildrenValues();
		if (childrenValues != null && childrenValues.length() > 0) {
			for (int i = 0; i < childrenValues.length(); i++) {
				gatherAllAvailableChildrenRequirements(childrenValues.get(i), array);
			}
		} else {
			JsArrayString children = node.getChildren();
			if (children != null && children.length() > 0) {
				for (int i = 0; i < children.length(); i++) {
					array.push(children.get(i));
				}
			}
		}
	}
	
}
