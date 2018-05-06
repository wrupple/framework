package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.catalogs.domain.CatalogRelation;
import com.wrupple.muba.catalogs.domain.DataJoinContext;
import com.wrupple.muba.catalogs.server.service.ResultSetService;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

/**
 * Created by japi on 5/05/18.
 */
@Singleton
public class BuildColumnResultSetImpl implements Command<DataJoinContext> {

    private final ResultSetService delegate;

    @Inject
    public BuildColumnResultSetImpl(ResultSetService delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean execute(DataJoinContext context) throws Exception {
        if(context.isBuildResultSet()){
            CatalogRelation relation = context.getWorkingRelation();
            CatalogDescriptor joinCatalog = relation.getForeignCatalogValue();
            CatalogColumnResultSet resultSet = delegate.createResultSet(relation.getResults(), joinCatalog, joinCatalog.getDistinguishedName(),
                    context.getMain(), context.getIntrospectionSession());
            List<CatalogColumnResultSet> joinsThusFar = context.getMain().getResultSet();
            joinsThusFar.add(resultSet);
        }

        return CONTINUE_PROCESSING;
    }


}
