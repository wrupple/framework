package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.DataJoinContext;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * Created by japi on 5/05/18.
 */
public class BuildRelationImpl extends ChainBase<DataJoinContext> {

    @Inject
    public BuildRelationImpl(GatherFieldValuesImpl gatherFieldValues,ProcessJoinsImpl processJoin, BuildColumnResultSetImpl reflect)
    {
        super(Arrays.asList(gatherFieldValues,processJoin,reflect));
    }
}
