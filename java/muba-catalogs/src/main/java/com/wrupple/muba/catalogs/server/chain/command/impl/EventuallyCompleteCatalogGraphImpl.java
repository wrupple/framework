package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogRelation;
import com.wrupple.muba.catalogs.domain.DataJoinContext;
import com.wrupple.muba.catalogs.server.chain.command.BuildResult;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class EventuallyCompleteCatalogGraphImpl implements CompleteCatalogGraph {

    protected static final Logger log = LogManager.getLogger(EventuallyCompleteCatalogGraphImpl.class);

    private static final String PROPERTY = "com.wrupple.catalog.readGraph";

    private final FieldAccessStrategy access;
	private final BuildResult build;
    private final CatalogKeyServices keydelegate;

    public static class Key {
         final CatalogEntry entry;

        public Key(CatalogEntry entry) {
            this.entry = entry;
        }

        public Long getDomain() {
            return entry.getDomain();
        }

        public Object getId() {
            return entry.getId();
        }

        public String getCatalog() {
            return entry.getCatalogType();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(getDomain(), key.getDomain()) &&
                    Objects.equals(getId(), key.getId()) &&
                    Objects.equals(getCatalog(), key.getCatalog());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getDomain(), getId(), getCatalog());
        }
    }

	@Inject
	public EventuallyCompleteCatalogGraphImpl(FieldAccessStrategy access, BuildRelationImpl build, CatalogKeyServices keydelegate, ReflectOnFieldsImpl reflect) {
		this.access = access;
        build.addCommand(reflect);
		this.build =new BuildResultImpl(build);
        this.keydelegate = keydelegate;
    }

	@Override
	public boolean execute(CatalogActionContext context) throws Exception {
		if (!(context.getResults() == null || context.getResults().isEmpty())) {
            Map<Key,CatalogEntry> workingSet = assertSet(context);
            List < Key > keys = context.getResults().stream().
                    map(e -> new Key(e)).
                    filter(e -> !workingSet.containsKey(e)).collect(Collectors.toList());
            if(keys.isEmpty()){
                return CONTINUE_PROCESSING;
            }else{
                addAll(workingSet,keys);
                List<CatalogEntry> results = keys.stream().map(k -> k.entry).collect(Collectors.toList());
                DataJoinContext childContext = new DataJoinContext(results,context,access.newSession(null));
                CatalogDescriptor descriptor = context.getCatalogDescriptor();
                List<CatalogRelation> joins = keydelegate.getJoins(context, null, descriptor, null, context, null);
                childContext.setJoins(joins);
                boolean regreso = build.execute(childContext);
                removeAll(workingSet,keys);
                return regreso;
            }
		}

        return CONTINUE_PROCESSING;
	}

    private void removeAll(Map<Key, CatalogEntry> workingSet, List<Key> keys) {

        keys.forEach(e->workingSet.remove(e));
    }

    private void addAll(Map<Key, CatalogEntry> workingSet, List<Key> keys) {
        keys.forEach(e->workingSet.put(e,e.entry));

    }


    private Map<Key,CatalogEntry> assertSet(CatalogActionContext c) {
        RuntimeContext context = c.getRuntimeContext().getRootAncestor();
        Map<Key,CatalogEntry> set = (Map<Key,CatalogEntry>) context.get(PROPERTY);
        if(set==null){
            set = new HashMap<>();
            context.put(PROPERTY,set);
        }
        return  set;
    }
}
