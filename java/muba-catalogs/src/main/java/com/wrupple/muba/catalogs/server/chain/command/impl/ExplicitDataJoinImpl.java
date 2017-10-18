package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.catalogs.server.chain.command.ExplicitDataJoin;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
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
	public ExplicitDataJoinImpl(EntrySynthesizer inheritanceDelegate, CatalogKeyServices keydelegateValue, FieldAccessStrategy accessStrategy) {
		super(inheritanceDelegate, keydelegateValue, accessStrategy);
	}

	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		if (context.getResults() == null || context.getResults().isEmpty()) {
			return CONTINUE_PROCESSING;
		}
		List<CatalogEntry> result = context.getResults();
        Instrospection instrospection = access.newSession(result.get(0));
        CatalogColumnResultSet resultSet = super.createResultSet(result, context.getCatalogDescriptor(),
				(String) context.getRequest().getCatalog(), context, instrospection);
		FilterData filter = context.getRequest().getFilter();
		if (filter != null) {
			resultSet.setCursor(filter.getCursor());
		}
		String[][] joins = filter.getJoins();
		Map<JoinQueryKey, Set<Object>> filterMap = createFilterMap(joins, context);
		ArrayList<CatalogColumnResultSet> regreso = new ArrayList<CatalogColumnResultSet>(filterMap.size() + 1);
		regreso.add(resultSet);
		context.put(CompleteCatalogGraph.JOINED_DATA, regreso);

		joinWithGivenJoinData(result, context.getCatalogDescriptor(), joins, context, filterMap, instrospection);
		return CONTINUE_PROCESSING;
	}

	@Override
	protected void workJoinData(List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog, List<CatalogEntry> joins,
			CatalogDescriptor joinCatalog, CatalogActionContext context, Instrospection instrospection) throws Exception {
		CatalogColumnResultSet resultSet = super.createResultSet(joins, joinCatalog, joinCatalog.getDistinguishedName(),
				context, instrospection);
		List<CatalogColumnResultSet> joinsThusFar = (List<CatalogColumnResultSet>) context
				.get(CompleteCatalogGraph.JOINED_DATA);
		joinsThusFar.add(resultSet);

	}

}
