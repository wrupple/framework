package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogRelation;
import com.wrupple.muba.catalogs.domain.DataJoinContext;
import com.wrupple.muba.catalogs.server.chain.command.BuildResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by japi on 5/05/18.
 */
@Singleton
public class BuildResultImpl implements BuildResult {

    protected static final Logger log = LogManager.getLogger(BuildResultImpl.class);

    private BuildRelationImpl chain;

    @Inject
    public BuildResultImpl(BuildRelationImpl chain) {
        this.chain = chain;
    }

    @Override
    public boolean execute(DataJoinContext context) throws Exception {

        List<CatalogRelation> joins = context.getJoins();

        CatalogRelation joinSentence;
        for (int i = 0; i < joins.size(); i++) {
            joinSentence = joins.get(i);
            context.setWorkingRelation(joinSentence);
            chain.execute(context);
        }

        return CONTINUE_PROCESSING;
    }


}
