package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogPluginQueryCommand;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import org.apache.commons.chain.Context;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class CatalogPluginQueryCommandImpl implements CatalogPluginQueryCommand {
    @Override
    public boolean execute(Context ctx) throws Exception {
        CatalogActionContext context = (CatalogActionContext) ctx;
        CatalogDescriptor catalogDescriptor = context.getCatalogDescriptor();
        if(catalogDescriptor.getDistinguishedName().equals(CatalogDescriptor.CATALOG_ID)){
            FilterData filter = context.getRequest().getFilter();


            if(filter.containsKey(catalogDescriptor.getKeyField())){
                List<Long> keys = (List)filter.fetchCriteria(catalogDescriptor.getKeyField()).getValues();
                //numeric id

                List<CatalogDescriptor> results = keys.stream().
                        map(key-> context.getCatalogManager().getDescriptorForKey(key,context)).collect(Collectors.toList());

                context.setResults(results);
            }else if(filter.containsKey(HasDistinguishedName.FIELD)){
                //distinguished name
                List<String> names = (List)filter.fetchCriteria(HasDistinguishedName.FIELD).getValues();
                //numeric id

                List<CatalogDescriptor> results = names.stream().
                        map(key-> context.getCatalogManager().getDescriptorForName(key,context)).collect(Collectors.toList());

                context.setResults(results);
            }



        }
        return CONTINUE_PROCESSING;
    }
}
