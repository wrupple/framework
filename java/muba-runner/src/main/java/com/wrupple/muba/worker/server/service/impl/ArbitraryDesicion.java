package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.Operation;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.service.Runner;
import com.wrupple.muba.worker.server.service.Solver;
import com.wrupple.muba.worker.server.service.VariableConsensus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ArbitraryDesicion implements VariableConsensus {
    protected Logger log = LogManager.getLogger(Solver.class);

    @Override
    public Runner apply(Runner runner, Runner runner2) {
          if(runner !=null) return runner;
          throw new NullPointerException();
        //FIXME return compositeWeightedPlugin;
    }

    @Override
    public void modelOperation(List<Runner> runners, Operation result, ApplicationContext context, Instrospection intros) {
        runners.stream().forEach(plugin->
                {
                        log.warn("    ...with {}",plugin);

                    plugin.model(result,context,intros);
                }
        );
    }
}
