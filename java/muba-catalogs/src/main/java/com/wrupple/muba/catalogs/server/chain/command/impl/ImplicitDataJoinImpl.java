package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.BuildResult;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.catalogs.server.service.ResultSetService;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.ImplicitDataJoin;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class ImplicitDataJoinImpl  implements ImplicitDataJoin {

    private final ResultSetService delegate;
	private final FieldAccessStrategy access;
    private final CatalogKeyServices keydelegate;

    private final BuildResult build;

	@Inject
	public ImplicitDataJoinImpl(ResultSetService delegate, FieldAccessStrategy access, CatalogKeyServices keydelegate, BuildResult build) {
        this.delegate = delegate;
        this.access = access;
        this.keydelegate = keydelegate;
        this.build = build;
	}


	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		if (context.getResults() == null || context.getResults().isEmpty()) {
			return CONTINUE_PROCESSING;
		}
		DataJoinContext childContext = new DataJoinContext(context.getResults(),context,access.newSession(null));
		List<CatalogEntry> result = context.getResults();
        CatalogColumnResultSet resultSet = delegate.createResultSet(result, context.getCatalogDescriptor(),
				(String) context.getRequest().getCatalog(), context, childContext.getIntrospectionSession());

		CatalogDescriptor descriptor = context.getCatalogDescriptor();
        List<CatalogRelation> joins = keydelegate.getJoins(context, null, descriptor, null,
                context, null);
        childContext.setJoins(joins);
        childContext.setBuildResultSet(true);
		Map<FieldFromCatalog, Set<Object>> filterMap = childContext.getFieldValueMap();

		ArrayList<CatalogColumnResultSet> regreso = new ArrayList<CatalogColumnResultSet>(filterMap.size() + 1);
		regreso.add(resultSet);
		context.setResultSet(regreso);

		return build.execute(childContext);
	}


}
