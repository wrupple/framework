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
import org.apache.commons.chain.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class EventuallyCompleteCatalogGraphImpl implements CompleteCatalogGraph {

    protected static final Logger log = LogManager.getLogger(EventuallyCompleteCatalogGraphImpl.class);

    private static final String PROPERTY = "com.wrupple.catalog.readGraph";

    private final FieldAccessStrategy access;
	private final BuildResult build;
    private final CatalogKeyServices keydelegate;

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
            Set<CatalogEntry> workingSet = assertSet(context);
            List<CatalogEntry> results =
                    workingSet.isEmpty() ? context.getResults():
                    context.getResults().stream().
                    filter(e -> !workingSet.contains(e)).
                    collect(Collectors.toList());
            if(results.isEmpty()){
                return CONTINUE_PROCESSING;
            }else{
                workingSet.addAll(results);
                DataJoinContext childContext = new DataJoinContext(results,context,access.newSession(null));
                CatalogDescriptor descriptor = context.getCatalogDescriptor();
                List<CatalogRelation> joins = keydelegate.getJoins(context, null, descriptor, null, context, null);
                childContext.setJoins(joins);
                boolean regreso = build.execute(childContext);
                workingSet.removeAll(results);
                return regreso;
            }
		}

        return CONTINUE_PROCESSING;
	}



    private Set<CatalogEntry> assertSet(CatalogActionContext c) {
        RuntimeContext context = c.getRuntimeContext().getRootAncestor();
        Set<CatalogEntry> set = (Set) context.get(PROPERTY);
        if(set==null){
            set = new HashSet<>();
            context.put(PROPERTY,set);
        }
        return  set;
    }
}
