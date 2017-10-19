package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticCatalogDescriptorProvider {


    private final Map<Object,CatalogDescriptor> keymap;
    private final Map<String,CatalogDescriptor> namemap;


    public StaticCatalogDescriptorProvider() {
        this.namemap = new HashMap<>();
        this.keymap = new HashMap<>();

    }


    protected void put(CatalogDescriptor catalog){
        if(catalog.getId()!=null){
            keymap.put(catalog.getId(),catalog);
        }
        namemap.put(catalog.getDistinguishedName(),catalog);

    }


    public final void modifyAvailableCatalogList(List<? super CatalogEntry> names, CatalogActionContext context) {
        names.addAll(namemap.values());
    }


    public final CatalogDescriptor getDescriptor(Long key, CatalogActionContext context){
        return keymap.get(key);
    }


    public final CatalogDescriptor getDescriptor(String catalogId, CatalogActionContext context) {

        return namemap.get(catalogId);


    }


}
