package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.ServiceContext;
import com.wrupple.muba.event.domain.reserved.HasResult;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.event.domain.reserved.HasResults;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;

public interface CatalogActionContext
        extends ServiceContext, HasResult<CatalogEntry>, HasResults<CatalogEntry> {

    String CATALOG = "CatalogActionContext";
    String INCOMPLETO = "_incompleto";

    CatalogActionRequest getRequest();

    CatalogDescriptor getCatalogDescriptor() throws RuntimeException;

    NamespaceContext getNamespaceContext();

    CatalogResultCache getCache(CatalogDescriptor catalog, CatalogActionContext context);

    void addResult(CatalogEntry result);

    CatalogEntry getOldValue();

    void setOldValue(CatalogEntry old);

    void setOldValues(List<CatalogEntry> originalEntries);

    List<CatalogEntry> getOldValues();

    void addResuls(List<CatalogEntry> result);

    <T extends CatalogEntry> T getEntryResult();

    void setCatalogDescriptor(CatalogDescriptor catalog);

    <T extends CatalogEntry> T get(String catalogId, Object entryId) throws Exception;

    <T extends CatalogEntry> T triggerGet(String catalogId, Object entryId) throws Exception;

    <T extends CatalogEntry> List<T> read(String catalogId, FilterData all) throws Exception;

    <T extends CatalogEntry> List<T> triggerRead(String catalogId, FilterData all) throws Exception;

    <T extends CatalogEntry> List<T> delete(String catalog, FilterData o, Object id) throws Exception;

    <T extends CatalogEntry> List<T> triggerDelete(String catalog, FilterData o, Object id) throws Exception;

    <T extends CatalogEntry> T write(String catalogId, Object entryId, CatalogEntry updatedEntry) throws Exception;

    <T extends CatalogEntry> T triggerWrite(String catalogId, Object entryId, CatalogEntry updatedEntry) throws Exception;

    <T extends CatalogEntry> T create(String catalogId, CatalogEntry createdEntry) throws Exception;

    <T extends CatalogEntry> T triggerCreate(String catalogId, CatalogEntry createdEntry) throws Exception;

    <T extends CatalogEntry> List<T> triggerRead(String catalogId, FilterData filter, boolean assemble) throws Exception;

    CatalogDescriptor getDescriptorForKey(Long numericId) throws Exception;

    CatalogDescriptor getDescriptorForName(String catalogId) throws Exception;

    <T extends CatalogEntry> T triggerGet(String catalogId, Object key, boolean assemble) throws Exception;

    List<CatalogEntry> getAvailableCatalogs() throws Exception;

    boolean isMetadataReady();

    void switchContract(CatalogActionRequest childContext);
}
