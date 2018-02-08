package com.wrupple.muba.worker.shared.services.impl;

import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.service.data.StorageManager;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.worker.domain.BPMPeer;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.shared.event.*;

import javax.inject.Provider;
import java.util.*;

/**
 * Entry Point for the Muba Storage Managemente System
 *
 * @author japi
 */

// @Container
public class StorageManagerImpl implements StorageManager {

    /**
     * names of all publicly registered catalogs
     */
    private static Map<String, Map<String, List<CatalogEntry>>> catalogNames;

    /**
     * convinience storage for CatalogDescriptors fetched from the server
     */
    private static Map<String, Map<String, Map<String, CatalogDescriptor>>> cache;
    private final Provider<CreditCardStorageUnit> ccc;
    private final Provider<RemoteStorageUnit<? super CatalogActionRequest, ? super CatalogEntry>> vvv;
    private final Provider<LocalWebStorageUnit> www;
    private final Provider<PeerManager> pmp;
    private Map<String, RemoteStorageUnit<? super CatalogActionRequest, ? super CatalogEntry>> hostMap;
    private EventBus bus;

    @Inject
    public StorageManagerImpl(Provider<PeerManager> pmp, Provider<CreditCardStorageUnit> ccc,
                              Provider<LocalWebStorageUnit> www, Provider<RemoteStorageUnit> vvv, EventBus bus) {
        super();
        this.pmp = pmp;
        this.ccc = ccc;
        this.www = www;
        this.vvv = (Provider) vvv;
        this.bus = bus;
        InvalidateCache handler = new InvalidateCache();
        bus.addHandler(EntriesDeletedEvent.getType(), handler);
        bus.addHandler(EntryUpdatedEvent.getType(), handler);
        bus.addHandler(EntryCreatedEvent.getType(), handler);
    }

    private static void put(String host, String domain, List<JsCatalogEntry> regreso) {

        if (catalogNames == null) {
            catalogNames = new HashMap<>(1);
        }

        Map<String, List<JsCatalogEntry>> hostCache = catalogNames.get(host);
        if (hostCache == null) {
            hostCache = new HashMap<>(1);
            catalogNames.put(host, hostCache);

        }

        hostCache.put(domain, regreso);
    }

    private static void clearCache(String host, String domain) {
        clearCatalogsCache(host, domain);
        clearNamesCache(host, domain);
    }

    private static void clearCatalogsCache(String host, String domain) {
        if (cache != null) {
            Map<String, Map<String, CatalogDescriptor>> hostCache = cache.get(host);
            if (hostCache != null) {
                Map<String, CatalogDescriptor> domainCache = hostCache.get(domain);
                if (domainCache != null) {
                    domainCache.clear();
                }
            }
        }

    }

    private static void clearNamesCache(String host, String domain) {
        if (catalogNames != null) {
            Map<String, List<JsCatalogEntry>> hostCache = catalogNames.get(host);
            if (hostCache != null) {
                List<JsCatalogEntry> domainCache = hostCache.get(domain);
                if (domainCache != null) {
                    hostCache.remove(domain);
                }
            }
        }
    }

    public static JsArrayString putCatalogs(String host, String domain, JsArray<JsCatalogDescriptor> d) {
        if (d == null) {
            return null;
        }
        if (cache == null) {
            cache = new HashMap<>(1);
        }
        Map<String, Map<String, CatalogDescriptor>> hostCache = cache.get(host);
        if (hostCache == null) {
            hostCache = new HashMap<>(1);
            cache.put(host, hostCache);
        }
        Map<String, CatalogDescriptor> domainCache = hostCache.get(domain);
        if (domainCache == null) {
            domainCache = new HashMap<>();
            hostCache.put(domain, domainCache);
        }

        JsArrayString regreso = JsArrayString.createArray().cast();
        JsCatalogDescriptor temp;
        for (int i = 0; i < d.length(); i++) {
            temp = d.get(i);
            domainCache.put(temp.getCatalogId(), temp);
            regreso.push(temp.getCatalogId());
        }

        return regreso;
    }

    @Override
    public void create(String host, final String domain, String catalog, final JsCatalogEntry entry, final StateTransition<JsCatalogEntry> callback) {
        loadCatalogDescriptor(host, domain, catalog, new DataCallback<CatalogDescriptor>() {

            @Override
            public void execute() {
                String storage = result.getStorage();
                Unit unit = getStorage(storage, result.getHost());
                unit.create(domain, entry, result, callback);
            }
        });

    }

    @Override
    public <T extends JavaScriptObject> void read(String host, final String domainNamespace, String catalog, final String id, final StateTransition<T> callback) {
        loadCatalogDescriptor(host, domainNamespace, catalog, new DataCallback<CatalogDescriptor>() {

            @Override
            public void execute() {
                String storage = result.getStorage();
                Unit unit = getStorage(storage, result.getHost());
                unit.read(domainNamespace, id, result, callback);
            }
        });

    }

    @Override
    public <T extends JavaScriptObject> void read(String host, final String domainNamespace, String catalog, final JsFilterData filter, final StateTransition<List<T>> callback) {
        if (validFilters(filter)) {
            loadCatalogDescriptor(host, domainNamespace, catalog, new DataCallback<CatalogDescriptor>() {

                @Override
                public void execute() {
                    String storage = result.getStorage();
                    Unit unit = getStorage(storage, result.getHost());
                    unit.read(domainNamespace, filter, result, callback);
                }
            });
        }
    }

    @Override
    public void read(String host, final String domainNamespace, String catalog, final List<String> ids, final StateTransition<List<JsCatalogEntry>> retailersCallback) {
        loadCatalogDescriptor(host, domainNamespace, catalog, new DataCallback<CatalogDescriptor>() {

            @Override
            public void execute() {
                String storage = result.getStorage();
                Unit unit = getStorage(storage, result.getHost());
                unit.read(domainNamespace, ids, result, retailersCallback);
            }
        });
    }

    @Override
    public void update(String host, final String domainNamespace, String catalog, final String id, final JsCatalogEntry entry, final StateTransition<JsCatalogEntry> callback) {
        loadCatalogDescriptor(host, domainNamespace, catalog, new DataCallback<CatalogDescriptor>() {

            @Override
            public void execute() {
                String storage = result.getStorage();
                Unit unit = getStorage(storage, result.getHost());
                unit.update(domainNamespace, id, entry, result, callback);
            }
        });
    }

    @Override
    public void delete(String host, final String domainNamespace, String catalog, final String id, final StateTransition<JsCatalogEntry> callback) {
        loadCatalogDescriptor(host, domainNamespace, catalog, new DataCallback<CatalogDescriptor>() {

            @Override
            public void execute() {
                String storage = result.getStorage();
                Unit unit = getStorage(storage, result.getHost());
                unit.delete(domainNamespace, id, result, callback);
            }
        });
    }

    @Override
    public RemoteStorageUnit<? super CatalogActionRequest, ? super CatalogEntry> getRemoteStorageUnit(String host) {
        PeerManager pm = null;
        if (host == null) {
            pm = pmp.get();
            host = pm.getHost();
        }
        RemoteStorageUnit<? super CatalogActionRequest, ? super CatalogEntry> storageUnit = hostMap == null ? null : hostMap.get(host);
        if (storageUnit == null) {

            storageUnit = vvv.get();
            if (pm == null) {
                pm = pmp.get();
            }
            BPMPeer peer = (BPMPeer) pm.getPeer(host);
            if (peer == null) {
                throw new NullPointerException("No data for host " + host);
            }
            storageUnit.setHost(peer);
            if (hostMap == null) {
                hostMap = new HashMap<String, RemoteStorageUnit<? super CatalogActionRequest, ? super CatalogEntry>>(2);
            }
            hostMap.put(host, storageUnit);
        }

        return storageUnit;

    }

    @Override
    public void loadGraphDescription(String host, String domain, String rootCatalog, StateTransition<CatalogDescriptor> onDone) {
        Set<String> visited = new HashSet<String>();
        visited.add(rootCatalog);
        loadCatalogDescriptor(host, domain, rootCatalog, new VisitCatalogNeighbors(host, domain, bus, onDone, this, visited));
    }

    @Override
    public void loadCatalogDescriptor(String host, String domain, final String catalogId, final StateTransition<CatalogDescriptor> onDone) {
        assert catalogId != null : "Attemped to load the descriptor of a null identifier";
        assert onDone != null : "Attempted to load a catalog descriptor but no load callback was provided";
        if (cache.containsKey(catalogId)) {
            onDone.setResultAndFinish(loadFromCache(host, domain, catalogId));
        } else {
            DataCallback<JsCatalogDescriptor> callback = new LoadCatalogDescriptor(onDone, domain, host);
            JsCatalogActionRequest request = JsCatalogActionRequest.newRequest(domain, CatalogActionRequest.LOCALE, CatalogActionRequest.LIST_ACTION_TOKEN,
                    catalogId, null, null, null, null);
            // this method should never call catalog descriptor or else a
            // circular dependency will ocurr obviously
            getRemoteStorageUnit(host).callGenericService(request, callback);
        }
    }

    @Override
    public CatalogDescriptor loadFromCache(String host, String domain, String catalog) {
        CatalogDescriptor regreso = null;

        if (cache != null) {
            Map<String, Map<String, CatalogDescriptor>> hostCache = cache.get(host);
            if (hostCache != null) {
                Map<String, CatalogDescriptor> domainCache = hostCache.get(domain);
                if (domainCache != null) {
                    regreso = domainCache.get(catalog);
                }
            }
        }

        if (regreso == null) {
            GWT.log("Catalog Descriptor setRuntimeContext id " + catalog + " not found in cache, expect subsequent errors");
        }
        return regreso;
    }

    @Override
    public void loadCatalogNames(String host, String domain, StateTransition<List<JsCatalogEntry>> callback) {
        if (catalogNames != null) {
            Map<String, List<JsCatalogEntry>> hostCache = catalogNames.get(host);
            if (hostCache != null) {
                List<JsCatalogEntry> domainCache = hostCache.get(domain);
                if (domainCache != null) {
                    callback.setResult(domainCache);
                    callback.execute();
                    return;
                }
            }
        }

        JsCatalogActionRequest request = JsCatalogActionRequest.newRequest(domain, CatalogActionRequest.LOCALE, CatalogActionRequest.LIST_ACTION_TOKEN,
                null, null, null, null, null);
        getRemoteStorageUnit(host).callGenericService(request, new OnCatalogNamesReceived(callback, host, domain));
    }

    @Override
    public void putInCache(String host, String domain, CatalogDescriptor djso) {
        if (cache == null) {
            cache = new HashMap<>(1);
        }
        Map<String, Map<String, CatalogDescriptor>> hostCache = cache.get(host);
        if (hostCache == null) {
            hostCache = new HashMap<>(1);
            cache.put(host, hostCache);
        }
        Map<String, CatalogDescriptor> domainCache = hostCache.get(domain);
        if (domainCache == null) {
            domainCache = new HashMap<>();
            hostCache.put(domain, domainCache);
        }

        domainCache.put(djso.getCatalogId(), djso);
    }

    protected Unit<? extends CatalogEntry> getStorage(String unit, String host) {
        if (unit == null || unit.equals(RemoteStorageUnit.UNIT)) {
            return getRemoteStorageUnit(host);
        } else if (unit.equals(LocalWebStorageUnit.UNIT)) {
            return www.get();
        } else if (unit.equals(CreditCardStorageUnit.UNIT)) {
            return ccc.get();
        } else {
            throw new IllegalArgumentException(unit);
        }
    }

    private boolean validFilters(JsFilterData filter) {

        JsArray<JsFilterCriteria> criterias = filter.getFilterArray();
        if (criterias != null) {
            JsFilterCriteria criteria;
            for (int i = 0; i < criterias.length(); ) {
                criteria = criterias.get(i);
                if (criteria == null) {
                    remove(criterias, i);
                } else {
                    if (criteria.getOperator() == null) {
                        remove(criterias, i);
                    } else if (criteria.getPathArray() == null || criteria.getPathArray().length() == 0) {
                        remove(criterias, i);
                    } else if (criteria.getValuesArray() == null || criteria.getValuesArray().length() == 0) {
                        remove(criterias, i);
                    } else {
                        i++;
                    }
                }
            }
        }

        return true;
    }

    private native void remove(JsArray<JsFilterCriteria> criterias, int i) /*-{
                                                                            criterias.splice(i);
																			}-*/;

    @Override
    public CatalogDescriptor getDescriptorForName(String name, Long domain) throws Exception {
        throw new IllegalArgumentException("unsupported client side");
    }

    @Override
    public void invalidateCache(String nodifiedCatalog) {
        throw new IllegalArgumentException("unsupported client side");
    }

    static class OnCatalogNamesReceived extends DataCallback<JsArray<JsCatalogEntry>> {
        StateTransition<List<JsCatalogEntry>> callback;
        String host, domain;

        public OnCatalogNamesReceived(StateTransition<List<JsCatalogEntry>> callback, String host, String domain) {
            this.callback = callback;
            this.host = host;
            this.domain = domain;
        }

        @Override
        public void execute() {
            List<JsCatalogEntry> regreso = JsArrayList.arrayAsList(result);
            put(host, domain, regreso);
            callback.setResult(regreso);
            callback.execute();
        }

    }

    static class ConvertJsArray extends DataCallback<JsArray<JsCatalogEntry>> {
        DataCallback<List<JsCatalogEntry>> callback;

        public ConvertJsArray(DataCallback<List<JsCatalogEntry>> callback) {
            super();
            this.callback = callback;
        }

        @Override
        public void execute() {

            List<JsCatalogEntry> regreso = JsArrayList.arrayAsList(result);
            callback.setResultAndFinish(regreso);
        }

    }

    class InvalidateCache implements HandlesCatalogEvents {

        @Override
        public void onEntriesDeleted(EntriesDeletedEvent e) {
            if (CatalogDescriptor.CATALOG_ID.equals(e.catalog) || FieldDescriptor.CATALOG_ID.equals(e.catalog)) {
                clearCache(e.host, e.domain);
            }
        }

        @Override
        public void onEntryUpdated(EntryUpdatedEvent e) {
            if (CatalogDescriptor.CATALOG_ID.equals(e.entry.getCatalogType()) || FieldDescriptor.CATALOG_ID.equals(e.entry.getCatalogType())) {
                clearCache(e.host, e.domain);
            }
        }

        @Override
        public void onEntryCreated(EntryCreatedEvent e) {
            if (CatalogDescriptor.CATALOG_ID.equals(e.entry.getCatalogType())) {
                clearNamesCache(e.host, e.domain);
            }
        }

        @Override
        public void onEntriesRetrived(EntriesRetrivedEvent e) {

        }

    }

    class LoadCatalogDescriptor extends StateTransition<CatalogDescriptor> {
        private StateTransition<CatalogDescriptor> onDone;
        private String host;
        private String domain;

        public LoadCatalogDescriptor(StateTransition<CatalogDescriptor> onDone, String domain, String host) {
            super();
            this.host = host;
            this.onDone = onDone;
            this.domain = domain;
        }

        @Override
        public void execute() {
            if (result == null) {
                // StaticDesktopAccess.addUIError(constants.catalogLoadError());
                return;
            }
			/*
			 * FIXME if the catalog's host is not the BPMPeer's local host, load
			 * the foreign host's peer, Authenticate if necesary (attempting to
			 * reuse current credentials first?), and load cryptography
			 * libraries used to sign JSON requests
			 */
            putInCache(host, domain, result);
            onDone.setResult(result);
            onDone.execute();
        }
    }

}
