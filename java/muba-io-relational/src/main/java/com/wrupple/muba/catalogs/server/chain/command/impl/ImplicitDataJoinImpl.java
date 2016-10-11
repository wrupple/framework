package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.catalogs.server.chain.command.ImplicitDataJoin;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;
import com.wrupple.muba.catalogs.server.service.impl.SameEntityLocalizationStrategy;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;

@Singleton
public class ImplicitDataJoinImpl extends DataJoiner implements ImplicitDataJoin {

	@Inject
	public ImplicitDataJoinImpl(DiscriminateEntriesImpl separateEntityStrategy,
			SameEntityLocalizationStrategy sameEntityStrategy, CatalogEvaluationDelegate propertyAccessor) {
		super(separateEntityStrategy, sameEntityStrategy, propertyAccessor);
	}

	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		if (context.getResults() == null || context.getResults().isEmpty()) {
			return CONTINUE_PROCESSING;
		}

		List<CatalogEntry> result = context.getResults();
		Session session = axs.newSession(result.get(0));
		CatalogColumnResultSet resultSet = super.createResultSet(result, context.getCatalogDescriptor(),
				context.getCatalog(), context, session);

		CatalogDescriptor descriptor = context.getCatalogDescriptor();
		String[][] joins = ImplicitJoinUtils.getJoins(context.getCatalogManager(), null, descriptor, null,
				context, null);
		Map<JoinQueryKey, Set<Object>> filterMap = createFilterMap(joins, context);

		ArrayList<CatalogColumnResultSet> regreso = new ArrayList<CatalogColumnResultSet>(filterMap.size() + 1);
		regreso.add(resultSet);
		context.put(CompleteCatalogGraph.JOINED_DATA, regreso);

		joinWithGivenJoinData(context.getResults(), context.getCatalogDescriptor(), joins, context, filterMap, session);
		return CONTINUE_PROCESSING;
	}

	@Override
	protected void workJoinData(List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog, List<CatalogEntry> joins,
			CatalogDescriptor joinCatalog, CatalogActionContext context, Session session) throws Exception {
		CatalogColumnResultSet resultSet = super.createResultSet(joins, joinCatalog, joinCatalog.getCatalog(), context,
				session);
		List<CatalogColumnResultSet> joinsThusFar = (List<CatalogColumnResultSet>) context
				.get(CompleteCatalogGraph.JOINED_DATA);
		joinsThusFar.add(resultSet);
	}

}
