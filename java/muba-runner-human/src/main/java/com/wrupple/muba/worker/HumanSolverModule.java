package com.wrupple.muba.worker;

import com.google.inject.AbstractModule;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.DefineSolutionCriteria;
import com.wrupple.muba.worker.server.chain.command.SolveTask;
import com.wrupple.muba.worker.server.chain.command.impl.DefineSolutionCriteriaImpl;
import com.wrupple.muba.worker.shared.services.FieldConversionStrategy;
import com.wrupple.muba.worker.shared.services.impl.FieldConversionStrategyImpl;

/**
 * Created by japi on 11/05/17.
 */
public class HumanSolverModule extends AbstractModule {
    @Override
    protected void configure() {
        //this provides the "model" or in this case human interaction context
        bind(ApplicationContext.class).to(HumanApplicationContextImpl.class);


        bind(SolveTask.class).to(UserInteractionState.class);

        bind(FieldConversionStrategy.class).to(FieldConversionStrategyImpl.class);

        bind(DefineSolutionCriteria.class).to(DefineSolutionCriteriaImpl.class);

    }

    /*
     * FIXME public static String buildVanityToken(HasDistinguishedName task) {
     *
     * String name = task.getDistinguishedName(); if (name == null) { name =
     * task.getName(); if (name == null) { name = task.getIdAsString(); } else {
     * //[^a-zA-Z0-9/] replace all except / name =
     * name.replaceAll("[^a-zA-Z0-9]", "-"); } }
     *
     * return name; }
     *
     * public static String getNameFromVanityToken(String vanityToken){ return
     * vanityToken.replace('-',' '); }
     */
}
