package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.worker.server.chain.command.AssembleView;
import com.wrupple.muba.worker.shared.domain.HumanApplicationContext;
import com.wrupple.muba.worker.shared.services.AssemblerDictionary;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.generic.LookupCommand;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class AssembleViewImpl extends LookupCommand<HumanApplicationContext> implements AssembleView {

    static final String CATALOG = "com.wrupple.runner.human.assemble";

    @Inject
    public AssembleViewImpl(CatalogFactory factory, AssemblerDictionary dictionary){
        super.setCatalogName(CATALOG);
        factory.addCatalog(CATALOG,dictionary);
        super.setNameKey(HumanApplicationContext.ACTION_DISCRIMINATOR);
    }
    /*TODO
            if (transactionViewClass != null) {
                transactionView.addStyleName(transactionViewClass);
            }
            */



}
