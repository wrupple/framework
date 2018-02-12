package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.worker.server.chain.command.ConfigureView;
import com.wrupple.muba.worker.shared.domain.HumanApplicationContext;
import com.wrupple.muba.worker.shared.services.ConfigurationDictionary;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.generic.LookupCommand;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ConfigureViewImpl extends LookupCommand<HumanApplicationContext> implements ConfigureView {

    static final String CATALOG = "com.wrupple.runner.human.configure.catalogName";
    @Inject
    public ConfigureViewImpl(CatalogFactory factory, ConfigurationDictionary dictionary) {
        super(factory);
        factory.addCatalog(CATALOG,dictionary);
        super.setCatalogName(CATALOG);
        super.setNameKey(HumanApplicationContext.ACTION_DISCRIMINATOR);
    }
}
