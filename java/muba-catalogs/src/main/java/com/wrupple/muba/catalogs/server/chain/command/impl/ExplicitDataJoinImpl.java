package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.Instrospector;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.catalogs.server.chain.command.ExplicitDataJoin;
import com.wrupple.muba.catalogs.server.service.impl.SameEntityLocalizationStrategy;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class ExplicitDataJoinImpl extends DataJoiner implements ExplicitDataJoin {

	@Inject
	public ExplicitDataJoinImpl(DiscriminateEntriesImpl separateEntityStrategy,
			SameEntityLocalizationStrategy sameEntityStrategy) {
		super(separateEntityStrategy, sameEntityStrategy);
	}

	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		if (context.getResults() == null || context.getResults().isEmpty()) {
			return CONTINUE_PROCESSING;
		}
		List<CatalogEntry> result = context.getResults();
        Instrospector instrospector = context.getCatalogManager().access().newSession(result.get(0));
        CatalogColumnResultSet resultSet = super.createResultSet(result, context.getCatalogDescriptor(),
				(String) context.getCatalog(), context, instrospector);
		FilterData filter = context.getFilter();
		if (filter != null) {
			resultSet.setCursor(filter.getCursor());
		}
		String[][] joins = filter.getJoins();
		Map<JoinQueryKey, Set<Object>> filterMap = createFilterMap(joins, context);
		ArrayList<CatalogColumnResultSet> regreso = new ArrayList<CatalogColumnResultSet>(filterMap.size() + 1);
		regreso.add(resultSet);
		context.put(CompleteCatalogGraph.JOINED_DATA, regreso);

		joinWithGivenJoinData(result, context.getCatalogDescriptor(), joins, context, filterMap, instrospector);
		return CONTINUE_PROCESSING;
	}

	@Override
	protected void workJoinData(List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog, List<CatalogEntry> joins,
			CatalogDescriptor joinCatalog, CatalogActionContext context, Instrospector instrospector) throws Exception {
		CatalogColumnResultSet resultSet = super.createResultSet(joins, joinCatalog, joinCatalog.getDistinguishedName(),
				context, instrospector);
		List<CatalogColumnResultSet> joinsThusFar = (List<CatalogColumnResultSet>) context
				.get(CompleteCatalogGraph.JOINED_DATA);
		joinsThusFar.add(resultSet);

	}

}
