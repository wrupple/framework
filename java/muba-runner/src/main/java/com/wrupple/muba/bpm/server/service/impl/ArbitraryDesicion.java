package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.server.service.SolverPlugin;
import com.wrupple.muba.bpm.server.service.VariableConsensus;

import javax.inject.Singleton;

@Singleton
public class ArbitraryDesicion implements VariableConsensus {
    @Override
    public SolverPlugin apply(SolverPlugin solverPlugin, SolverPlugin solverPlugin2) {
          if(solverPlugin!=null) return solverPlugin;
          throw new NullPointerException();
        //FIXME return compositeWeightedPlugin;
    }
}
