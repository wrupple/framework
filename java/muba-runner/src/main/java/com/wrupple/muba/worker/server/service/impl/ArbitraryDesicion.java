package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.worker.server.service.Runner;
import com.wrupple.muba.worker.server.service.VariableConsensus;

import javax.inject.Singleton;

@Singleton
public class ArbitraryDesicion implements VariableConsensus {
    @Override
    public Runner apply(Runner runner, Runner runner2) {
          if(runner !=null) return runner;
          throw new NullPointerException();
        //FIXME return compositeWeightedPlugin;
    }
}
