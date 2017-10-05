package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogActionTrigger;
import com.wrupple.muba.catalogs.server.service.TriggerStorageStrategy;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class TriggerStorageStrategyImpl implements TriggerStorageStrategy {
    private static final Logger log = LoggerFactory.getLogger(TriggerStorageStrategyImpl.class);

    private final Map<String,List<CatalogActionTrigger>> catalogScope;
    private final Map<Object,Map<String,List<CatalogActionTrigger>>> namespaceScope;

    @Inject
    public TriggerStorageStrategyImpl() {

        this.catalogScope = new HashMap<>();
        this.namespaceScope= new HashMap<>();
    }


    /**
     * before == trigger.isAdvice()
     * @param context
     * @param advise
     * @return
     */
    @Override public List<CatalogActionTrigger> getTriggersValues(CatalogActionContext context, boolean advise){
        List<CatalogActionTrigger> catalogScopedTriggers = catalogScope.
                get(context.getCatalogDescriptor().getDistinguishedName());
        Map<String, List<CatalogActionTrigger>> subScope = namespaceScope.get(context.getNamespaceContext().getId());
        List<CatalogActionTrigger> namespaceScoped = subScope.get(context.getCatalogDescriptor().getDistinguishedName());

        Stream<CatalogActionTrigger> stream;

        if(catalogScopedTriggers==null&&namespaceScoped==null){
            return null;
        }else if(catalogScopedTriggers!=null&&namespaceScoped!=null){
            List<CatalogActionTrigger> all = new ArrayList<>(namespaceScoped.size()+catalogScopedTriggers.size());
            all.addAll(namespaceScoped);
            all.addAll(catalogScopedTriggers);
            stream =  all.
                    stream();
        }else if(namespaceScoped==null){
                stream =  catalogScopedTriggers.
                        stream();
        }else {
                stream =  namespaceScoped.
                        stream();
        }

        return stream.
        filter(t -> t.isAdvice()==advise).
                collect(Collectors.toList());
    }

    @Override public void addCatalogScopeTrigger(CatalogActionTrigger trigger, CatalogDescriptor catalog){
        addCatalogScopeTrigger(trigger,catalog,catalogScope);
    }

    private void addCatalogScopeTrigger(CatalogActionTrigger trigger, CatalogDescriptor catalog,  Map<String,List<CatalogActionTrigger>> catalogScope){
        log.debug("[new {} scoped trigger] {}",catalog.getDistinguishedName(),trigger);
        List<CatalogActionTrigger> triggers = catalogScope.get(catalog.getDistinguishedName());
        if(triggers==null){
            triggers = new ArrayList<>(2);
            catalogScope.put(catalog.getDistinguishedName(),triggers);
        }
        triggers.add(trigger);
    }

    @Override
    public void addNamespaceScopeTrigger(CatalogActionTrigger trigger, CatalogDescriptor catalog, CatalogActionContext context) {

        log.debug("[adding trigger in namespace] {}",catalog.getDistinguishedName(),context.getNamespaceContext().getId());

        Map<String,List<CatalogActionTrigger>> catalogScope = namespaceScope.get(context.getNamespaceContext().getId());;
        addCatalogScopeTrigger(trigger,catalog,catalogScope);

    }
}
