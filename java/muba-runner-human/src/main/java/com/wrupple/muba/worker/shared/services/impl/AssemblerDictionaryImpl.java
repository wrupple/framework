package com.wrupple.muba.worker.shared.services.impl;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.Task;
import com.wrupple.muba.worker.server.chain.command.AssembleBrowser;
import com.wrupple.muba.worker.server.chain.command.AssembleCreator;
import com.wrupple.muba.worker.server.chain.command.AssembleEditor;
import com.wrupple.muba.worker.server.chain.command.AssembleViewer;
import com.wrupple.muba.worker.shared.services.AssemblerDictionary;
import org.apache.commons.chain.impl.CatalogBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AssemblerDictionaryImpl extends CatalogBase implements AssemblerDictionary {

    @Inject
    public AssemblerDictionaryImpl(AssembleEditor update,AssembleViewer read, AssembleCreator create,AssembleBrowser browser) {

        super.addCommand(Task.SELECT_COMMAND,browser);
        super.addCommand(CatalogActionRequest.CREATE_ACTION,create);
        super.addCommand(CatalogActionRequest.WRITE_ACTION,update);
        super.addCommand(CatalogActionRequest.READ_ACTION,read);
    }
}
