package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.catalogs.server.chain.command.ImplicitDataJoin;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class ImplicitDataJoinImpl extends DataJoiner implements ImplicitDataJoin {

	@Inject
	public ImplicitDataJoinImpl() {
		super();
	}

	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		if (context.getResults() == null || context.getResults().isEmpty()) {
			return CONTINUE_PROCESSING;
		}

		List<CatalogEntry> result = context.getResults();
        Instrospection instrospection = context.getCatalogManager().access().newSession(result.get(0));
        CatalogColumnResultSet resultSet = super.createResultSet(result, context.getCatalogDescriptor(),
				(String) context.getRequest().getCatalog(), context, instrospection);

		CatalogDescriptor descriptor = context.getCatalogDescriptor();
		String[][] joins = context.getCatalogManager().getJoins(context, null, descriptor, null,
				context, null);
		Map<JoinQueryKey, Set<Object>> filterMap = createFilterMap(joins, context);

		ArrayList<CatalogColumnResultSet> regreso = new ArrayList<CatalogColumnResultSet>(filterMap.size() + 1);
		regreso.add(resultSet);
		context.put(CompleteCatalogGraph.JOINED_DATA, regreso);

		joinWithGivenJoinData(context.getResults(), context.getCatalogDescriptor(), joins, context, filterMap, instrospection);
		return CONTINUE_PROCESSING;
	}

	@Override
	protected void workJoinData(List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog, List<CatalogEntry> joins,
			CatalogDescriptor joinCatalog, CatalogActionContext context, Instrospection instrospection) throws Exception {
		CatalogColumnResultSet resultSet = super.createResultSet(joins, joinCatalog, joinCatalog.getDistinguishedName(), context,
                instrospection);
		List<CatalogColumnResultSet> joinsThusFar = (List<CatalogColumnResultSet>) context
				.get(CompleteCatalogGraph.JOINED_DATA);
		joinsThusFar.add(resultSet);
	}

}
