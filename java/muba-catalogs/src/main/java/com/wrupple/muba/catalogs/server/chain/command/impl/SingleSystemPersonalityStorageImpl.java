package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.SystemPersonalitiesStorage;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class SingleSystemPersonalityStorageImpl implements SystemPersonalitiesStorage {

    private final Person anon;

    @Inject
    public SingleSystemPersonalityStorageImpl(@Named(SessionContext.SYSTEM) Person anon) {
        this.anon = anon;
    }

    @Override
    public boolean execute(CatalogActionContext context) throws Exception {
        CatalogDescriptor catalogDescriptor = context.getCatalogDescriptor();
        if(catalogDescriptor.getDistinguishedName().equals(Person.CATALOG)){
            FilterData filter = context.getRequest().getFilter();
            Object entry = context.getRequest().getEntry();
            if(entry==null){
                if (filter == null) {

                }else if (filter.containsKey(catalogDescriptor.getKeyField())) {
                    List<Long> keys = (List) filter.fetchCriteria(catalogDescriptor.getKeyField()).getValues();
                    //numeric id
                    List<CatalogEntry> results = new ArrayList<>(keys.size());

                    for(Long key: keys){
                        results.add(getDescriptorForKey(key,context));
                    }
                    context.setResults(results);
                }
            }else{
                Person result = getDescriptorForKey((Long) entry, context);


                context.setResult(result);
            }
        }



        return CONTINUE_PROCESSING;
    }

    private Person getDescriptorForKey(Long key, CatalogActionContext context) {
        if(key.longValue()==CatalogEntry.PUBLIC_ID){
            return anon;
        }
return null;    }
}
