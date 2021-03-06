package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.Trigger;
import com.wrupple.muba.catalogs.server.chain.command.PluginConsensus;
import com.wrupple.muba.catalogs.server.chain.command.TriggerPluginQueryCommand;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorService;
import com.wrupple.muba.catalogs.server.service.TriggerCreationScope;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;


import static com.wrupple.muba.catalogs.server.chain.command.PluginConsensus.OVERRIDE_SCOPE;

@Singleton
public class TriggerPluginQueryCommandImpl implements TriggerPluginQueryCommand {

    protected static final Logger log = LogManager.getLogger(CatalogPluginQueryCommandImpl.class);

    private final PluginConsensus plugins;
    private final CatalogDescriptorService metadataService;

    static class PluginTriggerCrationScope implements TriggerCreationScope{
        private final List<Trigger> triggers;
        private final Long action;
        PluginTriggerCrationScope(Long action) {
            this.action = action;
            this.triggers = new ArrayList<>();
        }

        @Override
        public void add(Trigger e, CatalogDescriptor catalog, CatalogActionContext context) throws Exception {
            if(action.equals(
                    e.getAction())){
                e.setCatalog(catalog.getDistinguishedName());
                triggers.add(e);
            }
        }
    }

    @Inject
    public TriggerPluginQueryCommandImpl(PluginConsensus plugins, CatalogDescriptorService metadataService) {
        this.plugins = plugins;
        this.metadataService = metadataService;
    }


    @Override
    public boolean execute(CatalogActionContext context) throws Exception {

        CatalogDescriptor catalogDescriptor = context.getCatalogDescriptor();
        if(catalogDescriptor.getDistinguishedName().equals(Trigger.CATALOG)){
            FilterData filter = context.getRequest().getFilter();
                if (filter.containsKey(HasCatalogId.CATALOG_FIELD)) {
                    FilterCriteria catalogIds = filter.fetchCriteria(HasCatalogId.CATALOG_FIELD);
                    FilterCriteria actionCriteria = filter.fetchCriteria(Trigger.ACTION_FIELD);
                    Long action = (Long) actionCriteria.getValue();
                    List<String> catalogs = (List)catalogIds.getValues();

                    PluginTriggerCrationScope scope = new PluginTriggerCrationScope(action);
                    context.put(OVERRIDE_SCOPE,scope);

                    CatalogDescriptor catalog;
                    for(String catalogId : catalogs){
                        catalog = metadataService.getDescriptorForName(catalogId,context);
                        context.getRequest().setEntryValue(catalog);
                        plugins.execute(context);

                    }
                    context.getRequest().setEntryValue(null);


                    List<Trigger> results = scope.triggers;
                    if(results==null || results.isEmpty()){
                        results = null;
                    }
                    context.setResults(results);
                }

        }



        return CONTINUE_PROCESSING;
    }



}
