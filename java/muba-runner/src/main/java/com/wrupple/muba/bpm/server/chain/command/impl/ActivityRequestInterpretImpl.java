package com.wrupple.muba.bpm.server.chain.command.impl;

import com.sun.xml.internal.bind.v2.TODO;
import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.bpm.server.service.TaskRunnerPlugin;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import org.apache.commons.chain.Context;

import javax.inject.Inject;

/**
 * Created by japi on 11/05/17.
 */
public class ActivityRequestInterpretImpl implements ActivityRequestInterpret {

    private TaskRunnerPlugin plugin;
    private SystemCatalogPlugin catalog;

    @Inject public ActivityRequestInterpretImpl(TaskRunnerPlugin plugin,SystemCatalogPlugin catalog){
        this.plugin=plugin;
        this.catalog=catalog;
    }

    @Override
    public Context materializeBlankContext(ExcecutionContext requestContext) {
        ProcessTaskDescriptor activity = (ProcessTaskDescriptor) requestContext.getServiceContract();
        //TODO task plugin is used as a shorthand for the more verbose catalog engine

        //TODO get task descriptor

        // 1. Create a Model inside plugin the context references it by dn
        // 2. Create variables, by default use all variables defined in task
        //TODO constriant using Min, Max,
        //IntVar x = model.intVar("x", 0, 5); // x in [0,5]
        //TODO and field domain
        //IntVar y = model.intVar("y", new int[]{2, 3, 8}); // y in {2, 3, 8}
        // 3. Post constraints
        //TODO from activity definition sentence
        model.arithm(x, "+", y, "<", 5).post(); // x + y < 5
        //TODO and from request's sentence
        model.times(x,y,4).post(); // x * y = 4

//TODO 4. delegate the actual solving of the problem to the chain
        model.getSolver().solve();
// TODO commit solution (default is autoCommit)
        System.out.println(x); // Prints X = 2
        System.out.println(y); // Prints Y = 2
        return activityContext;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        ExcecutionContext requestContext = (ExcecutionContext) ctx;
        ProcessTaskDescriptor request = (ProcessTaskDescriptor) requestContext.getServiceContract();
        ActivityContext context = requestContext.getServiceContext();
        TODO
        return CONTINUE_PROCESSING;
    }
}
