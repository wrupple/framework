package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.BuildResult;
import com.wrupple.muba.catalogs.server.service.ResultSetService;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
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
public class ExplicitDataJoinImpl implements ExplicitDataJoin {

	private final FieldAccessStrategy access;
	private final BuildResult build;
	private final ResultSetService delegate;

	@Inject
	public ExplicitDataJoinImpl(FieldAccessStrategy access, BuildResult build, ResultSetService delegate) {
		this.access = access;
		this.build = build;
        this.delegate = delegate;
    }


	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		if (context.getResults() == null || context.getResults().isEmpty()) {
			return CONTINUE_PROCESSING;
		}
		List<CatalogEntry> result = context.getResults();
        Instrospection instrospection = access.newSession(result.get(0));
        CatalogColumnResultSet resultSet = delegate.createResultSet(result, context.getCatalogDescriptor(),
				(String) context.getRequest().getCatalog(), context, instrospection);
		FilterData filter = context.getRequest().getFilter();
		if (filter != null) {
			resultSet.setCursor(filter.getCursor());
		}
		String[][] rawjoins = filter.getJoins();
		if(rawjoins==null){
		    return CONTINUE_PROCESSING;
        }
		List<CatalogRelation> joins = new ArrayList<>(rawjoins.length);
		for(String[] rawjoin:rawjoins){
		    joins.add(new CatalogRelation(rawjoin[0],rawjoin[1],2<rawjoin.length?rawjoin[2]:rawjoin[1]));
        }


        DataJoinContext childContext = new DataJoinContext(context,access.newSession(null));
        childContext.setJoins(joins);
        childContext.setBuildResultSet(true);

		Map<FieldFromCatalog, Set<Object>> filterMap = childContext.getFieldValueMap();
		ArrayList<CatalogColumnResultSet> regreso = new ArrayList<CatalogColumnResultSet>(filterMap.size() + 1);
		regreso.add(resultSet);
		context.setResultSet(regreso);

		return build.execute(childContext);
	}



}
