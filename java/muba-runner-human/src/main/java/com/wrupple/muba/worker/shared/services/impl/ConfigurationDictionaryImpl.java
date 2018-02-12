package com.wrupple.muba.worker.shared.services.impl;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.Task;
import com.wrupple.muba.worker.server.chain.command.ConfigureBrowser;
import com.wrupple.muba.worker.server.chain.command.FormConfiguration;
import com.wrupple.muba.worker.shared.services.ConfigurationDictionary;
import org.apache.commons.chain.impl.CatalogBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ConfigurationDictionaryImpl extends CatalogBase implements ConfigurationDictionary {
    @Inject
    public ConfigurationDictionaryImpl(FormConfiguration form, ConfigureBrowser browser) {
        super.addCommand(Task.SELECT_COMMAND,browser);
        super.addCommand(CatalogActionRequest.CREATE_ACTION,form);
        super.addCommand(CatalogActionRequest.WRITE_ACTION,form);
        super.addCommand(CatalogActionRequest.READ_ACTION,form);
    }


}
