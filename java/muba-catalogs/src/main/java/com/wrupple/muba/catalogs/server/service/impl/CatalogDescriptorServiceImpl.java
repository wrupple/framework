package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.domain.CatalogException;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorService;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.DataContract;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.impl.CatalogQueryRequestImpl;
import com.wrupple.muba.event.domain.impl.CatalogReadRequestImpl;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class CatalogDescriptorServiceImpl implements CatalogDescriptorService {
    private static Logger log = LogManager.getLogger(CatalogDescriptorServiceImpl.class);

    private final Provider<CatalogDescriptor> metadataDescriptorProvider;
    private final Provider<CatalogReadTransaction> readerProvider;

    @Inject
    public CatalogDescriptorServiceImpl(
            @Named(CatalogDescriptor.CATALOG_ID) Provider<CatalogDescriptor> metadataDescriptorProvider,
            Provider<CatalogReadTransaction> readerProvider
    ) {
        this.metadataDescriptorProvider = metadataDescriptorProvider;
        this.readerProvider = readerProvider;
    }

    @Override
    public CatalogDescriptor getDescriptorForKey(Long numericId, CatalogActionContext context) throws Exception {
        log.trace("[get metadata] {}",numericId);
        CatalogDescriptor metadataDescriptor = metadataDescriptorProvider.get();
        CatalogResultCache metadataCache = context.getCache(metadataDescriptor, context);

        if(numericId.equals(metadataDescriptor.getId())){
            CatalogDescriptor result = metadataCache.get(context, CatalogDescriptor.CATALOG_ID,numericId);
            if (result == null) {
                result = informCache(metadataDescriptor,context,metadataCache);
            }
            return result;
        }else{
            return readDescriptor(context, numericId, metadataCache);
        }
    }

    @Override
    public CatalogDescriptor getDescriptorForName(String catalogid, CatalogActionContext context) throws Exception {
        log.trace("[get metadata] {}",catalogid);

        CatalogDescriptor metadataDescriptor = metadataDescriptorProvider.get();
        CatalogResultCache metadataCache = context.getCache(metadataDescriptor, context);

        CatalogDescriptor result = metadataCache.get(context,catalogid,HasDistinguishedName.FIELD,catalogid);

        if(result==null){

            if (CatalogDescriptor.CATALOG_ID.equals(catalogid)) {

                result = metadataCache.get(context, HasDistinguishedName.FIELD, catalogid);
                if (result == null) {
                   result = informCache(metadataDescriptor,context,metadataCache);
                }
            }else{
                result = readDescriptor(context, catalogid, metadataCache);
                if(result==null){
                    throw new CatalogException("No such catalog "+catalogid);
                }
            }
        }

        return result;
    }

    private CatalogDescriptor informCache(CatalogDescriptor metadataDescriptor, CatalogActionContext context,CatalogResultCache metadataCache) {
        metadataCache.put(context,CatalogDescriptor.CATALOG_ID,metadataDescriptor.getDistinguishedName(),HasDistinguishedName.FIELD,metadataDescriptor);
        metadataCache.put(context, CatalogDescriptor.CATALOG_ID, metadataDescriptor);
        return metadataDescriptor;
    }


    private CatalogDescriptor readDescriptor(CatalogActionContext context, Object catalogid,  CatalogResultCache metadataCache) throws Exception {
        CatalogActionRequest parentContext = context.getRequest();
        CatalogReadRequestImpl childRequest = new CatalogReadRequestImpl(catalogid,CatalogDescriptor.CATALOG_ID);
        childRequest.setDomain(parentContext.getDomain());
        childRequest.setParentValue(parentContext);

        context.getRuntimeContext().getServiceBus().fireEvent(childRequest,context.getRuntimeContext(),null);

        CatalogDescriptor  result = (CatalogDescriptor) childRequest.getResults().get(0);
        informCache(result,context,metadataCache);
        return result;
    }


}
