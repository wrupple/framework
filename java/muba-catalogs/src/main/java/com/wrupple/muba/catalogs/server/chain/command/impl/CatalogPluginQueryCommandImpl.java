package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogPluginQueryCommand;
import com.wrupple.muba.catalogs.server.chain.command.PluginConsensus;
import com.wrupple.muba.catalogs.server.domain.CatalogException;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class CatalogPluginQueryCommandImpl implements CatalogPluginQueryCommand {

    protected static final Logger log = LoggerFactory.getLogger(CatalogPluginQueryCommandImpl.class);

    private final CatalogPlugin[] plugins;

    @Inject
    public CatalogPluginQueryCommandImpl(@Named("catalog.plugins") Provider<Object> pluginProvider) {
        this.plugins = (CatalogPlugin[]) pluginProvider.get();
    }


    @Override
    public boolean execute(Context ctx) throws Exception {
        CatalogActionContext context = (CatalogActionContext) ctx;
        CatalogDescriptor catalogDescriptor = context.getCatalogDescriptor();
        if(catalogDescriptor.getDistinguishedName().equals(CatalogDescriptor.CATALOG_ID)){
            FilterData filter = context.getRequest().getFilter();
            Object entry = context.getRequest().getEntry();
            if(entry==null){
                if (filter == null) {
                    //FIXME CATALOG_TYPE (DTO) SHOULD EXPOSE A SEPARATE DATASOURCE FOR THIS
                    //no puede ser que el dto sea del mismo catalogo porque contaminaría el cache
                    //debe ser una entidad separada, de la que catlogos hereda
                    List<CatalogEntry> results = getAvailableCatalogs(context);
                    context.setResults(results);
                }else if (filter.containsKey(catalogDescriptor.getKeyField())) {
                    List<Long> keys = (List) filter.fetchCriteria(catalogDescriptor.getKeyField()).getValues();
                    //numeric id
                    List<CatalogDescriptor> results = keys.stream().
                            map(key -> {
                                try {
                                    return getDescriptorForKey(key, context);
                                } catch (Exception e) {
                                    throw new CatalogException(e);
                                }
                            }).
                            filter(descriptor -> {
                                return descriptor != null;
                            })
                            .collect(Collectors.toList());

                    consenseResults(context, results);
                } else if (filter.containsKey(HasDistinguishedName.FIELD)) {
                    //distinguished name
                    List<String> names = (List) filter.fetchCriteria(HasDistinguishedName.FIELD).getValues();
                    //numeric id

                    List<CatalogDescriptor> results = names.stream().
                            map(key -> {
                                try {
                                    return getDescriptorForName(key, context);
                                } catch (Exception e) {
                                    throw new CatalogException(e);
                                }
                            }).
                            filter(descriptor -> {
                                return descriptor != null;
                            }).collect(Collectors.toList());

                    consenseResults(context, results);
                }
            }else{
                CatalogDescriptor result;
                if(entry instanceof String){
                     result = getDescriptorForName((String) entry, context);
                }else{
                    result = getDescriptorForKey((Long) entry, context);
                }

                consenseResults(context, Collections.singletonList(result));
            }
        }

        

        return CONTINUE_PROCESSING;
    }

    private void consenseResults(CatalogActionContext context, List<CatalogDescriptor> results) throws Exception {
        context.setResults(results);
    }


    public List<CatalogEntry> getAvailableCatalogs(CatalogActionContext context) throws Exception {
        List<CatalogEntry> names = new ArrayList<CatalogEntry>();
            for (CatalogPlugin module : plugins) {
                module.modifyAvailableCatalogList(names, context);
            }

        return names;
    }


    private CatalogDescriptor getDescriptorForKey(Long primariKEy, CatalogActionContext context) throws Exception{
        // POLLING plugins
        for (CatalogPlugin plugin : plugins) {
            log.trace("asking {} for descriptor", plugin);
            CatalogDescriptor regreso = plugin.getDescriptor(primariKEy,context);
            if (regreso != null) {
                return regreso;
            }
        }
        return null;
    }

    private CatalogDescriptor getDescriptorForName(String distinguishedName, CatalogActionContext context) throws Exception  {
        // POLLING plugins

        CatalogDescriptor regreso;

        for (CatalogPlugin plugin : plugins) {
            log.trace("asking {} for descriptor", plugin);
            regreso = plugin.getDescriptor(distinguishedName, context);
            if (regreso != null) {
               return regreso;
            }
        }
        return null;
    }
}
