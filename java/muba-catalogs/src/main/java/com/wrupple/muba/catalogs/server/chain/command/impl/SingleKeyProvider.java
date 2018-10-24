package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.*;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

public class SingleKeyProvider implements Command<CatalogActionContext> {

    private final CatalogEntry anon;

    @Inject
    public SingleKeyProvider(CatalogEntry anon) {
        this.anon = anon;
    }

    @Override
    public boolean execute(CatalogActionContext context) throws Exception {
        CatalogDescriptor catalogDescriptor = context.getCatalogDescriptor();
        if(catalogDescriptor.getDistinguishedName().equals(anon.getCatalogType())){
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
                CatalogEntry result = getDescriptorForKey((Long) entry, context);


                context.setResult(result);
            }
        }



        return CONTINUE_PROCESSING;
    }

    private CatalogEntry getDescriptorForKey(Long key, CatalogActionContext context) {
        if(key.longValue()==CatalogEntry.PUBLIC_ID){
            return anon;
        }
        return null;    }
}
