package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.server.service.ActionsDictionary;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ActionsDictionaryImpl extends CatalogBase implements ActionsDictionary{

    private final Command read;
    private final CatalogTransactionState transactionState;

    @Inject
    public ActionsDictionaryImpl(CatalogFactory factory, CatalogTransactionState transactionState, CatalogReadTransaction read
            , PluginConsensus consensus, GarbageCollection collect, RestoreTrash restore, TrashDeleteTrigger dump, FieldDescriptorUpdateTrigger invalidateAll, CatalogDescriptorUpdateTrigger invalidate, EntryDeleteTrigger trash, UpdateTreeLevelIndex treeIndexHandler, Timestamper timestamper, WritePublicTimelineEventDiscriminator inheritanceHandler, IncreaseVersionNumber increaseVersionNumber, @Named("catalog.plugins") Provider<Object> pluginProvider) {
        this.read = read;
        this.transactionState=transactionState;

        factory.addCatalog(CatalogActionRequest.NAME_FIELD, this);

        CatalogPlugin[] plugins = (CatalogPlugin[]) pluginProvider.get();
        Command[] actions;
        for(CatalogPlugin plugin: plugins){
            actions = plugin.getCatalogActions();
            if(actions!=null){
                String commandName;
                for(Command action:actions){
                    commandName = action.getClass().getSimpleName();
                    if(commandName.endsWith("Impl")){
                        commandName = commandName.substring(0,commandName.length()-4);
                    }
                    addCommand(commandName,action);
                }
            }

        }

        addCommand(CatalogDescriptorUpdateTrigger.class.getSimpleName(), invalidate);
        addCommand(FieldDescriptorUpdateTrigger.class.getSimpleName(), invalidateAll);
        addCommand(EntryDeleteTrigger.class.getSimpleName(), trash);
        addCommand(TrashDeleteTrigger.class.getSimpleName(), dump);
        addCommand(PluginConsensus.class.getSimpleName(),consensus);
        addCommand(RestoreTrash.class.getSimpleName(), restore);
        addCommand(GarbageCollection.class.getSimpleName(), collect);
        addCommand(IncreaseVersionNumber.class.getSimpleName(), increaseVersionNumber);
        addCommand(UpdateTreeLevelIndex.class.getSimpleName(), treeIndexHandler);
        addCommand(WritePublicTimelineEventDiscriminator.class.getSimpleName(), inheritanceHandler);
        addCommand(Timestamper.class.getSimpleName(), timestamper);
        addCommand(CatalogFileUploadUrlHandlerTransaction.class.getSimpleName(), null/*FIXME*/);


        addCommand(CatalogActionRequest.READ_ACTION, read);


        addCommand(CatalogActionRequest.CREATE_ACTION, transactionState);
        addCommand(CatalogActionRequest.WRITE_ACTION, transactionState);
        addCommand(CatalogActionRequest.DELETE_ACTION, transactionState);
        addCommand(CatalogFileUploadTransaction.class.getSimpleName(), null/*FIXME*/);

    }



    @Override
    public Command getRead() {
        return read;
    }

    @Override
    public Command getWrite() {
        return transactionState;
    }

    @Override
    public Command getDelete() {
        return transactionState;
    }

    @Override
    public Command getNew() {
        return transactionState;
    }
}
