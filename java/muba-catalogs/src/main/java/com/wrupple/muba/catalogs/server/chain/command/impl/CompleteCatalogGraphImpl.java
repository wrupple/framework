package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogRelation;
import com.wrupple.muba.catalogs.domain.DataJoinContext;
import com.wrupple.muba.catalogs.domain.FieldFromCatalog;
import com.wrupple.muba.catalogs.server.chain.command.BuildResult;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class CompleteCatalogGraphImpl  implements CompleteCatalogGraph {

    protected static final Logger log = LogManager.getLogger(CompleteCatalogGraphImpl.class);


    private final FieldAccessStrategy access;
	private final BuildResult build;
    private final CatalogKeyServices keydelegate;

	@Inject
	public CompleteCatalogGraphImpl(FieldAccessStrategy access, BuildRelationImpl build, CatalogKeyServices keydelegate,ReflectOnFieldsImpl reflect) {
		this.access = access;
        build.addCommand(reflect);
		this.build =new BuildResultImpl(build);
        this.keydelegate = keydelegate;
    }

	@Override
	public boolean execute(CatalogActionContext context) throws Exception {


		if (!(context.getResults() == null || context.getResults().isEmpty())) {

			CatalogDescriptor descriptor = context.getCatalogDescriptor();

			List<CatalogRelation> joins = keydelegate.getJoins(context, null, descriptor, null, context, null);

			DataJoinContext childContext = new DataJoinContext(context,access.newSession(null));
            childContext.setJoins(joins);

            return build.execute(childContext);
		}

		return CONTINUE_PROCESSING;
	}


}
