package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.DataQueryCommand;
import com.wrupple.muba.catalogs.server.chain.command.DataReadCommand;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;


import java.util.List;
import java.util.stream.Collectors;

public class CommandStorage implements DataReadCommand,DataQueryCommand{


    private final CatalogFactory factory;
    private final String nameKey;

    protected CommandStorage(CatalogFactory factory, String nameKey) {
        this.factory = factory;
        this.nameKey=nameKey;

    }

    @Override
    public boolean execute(CatalogActionContext context) throws Exception {
        String name = (String) context.get(nameKey);
        Catalog catalog = factory.getCatalog(name);
        CatalogDescriptor catalogDescriptor = context.getCatalogDescriptor();


            FilterData filter = context.getRequest().getFilter();
            Object entry = context.getRequest().getEntry();
            if(entry==null){
                if (filter.containsKey(catalogDescriptor.getKeyField())) {
                    List<String> keys = (List) filter.fetchCriteria(catalogDescriptor.getKeyField()).getValues();
                    //numeric id
                    List<CatalogEntry> results = keys.stream().
                            map(key -> {
                                    return (CatalogEntry)catalog.getCommand(key);

                            })
                            .collect(Collectors.toList());

                    context.setResults(results);
                }
            }else{
                CatalogEntry result= (CatalogEntry)catalog.getCommand((String) entry);

                context.setResult(result);
            }

        return CONTINUE_PROCESSING;
    }



}
