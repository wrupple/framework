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


    private final Command create;

    private final Command read;

    private final Command write;

    private final Command delete;


    @Inject
    public ActionsDictionaryImpl(CatalogFactory factory, CatalogCreateTransaction create, CatalogReadTransaction read, CatalogUpdateTransaction write, CatalogDeleteTransaction delete
            , PluginConsensus consensus, GarbageCollection collect, RestoreTrash restore, TrashDeleteTrigger dump, FieldDescriptorUpdateTrigger invalidateAll, CatalogDescriptorUpdateTrigger invalidate, EntryDeleteTrigger trash, UpdateTreeLevelIndex treeIndexHandler, Timestamper timestamper, WritePublicTimelineEventDiscriminator inheritanceHandler, IncreaseVersionNumber increaseVersionNumber, @Named("catalog.plugins") Provider<Object> pluginProvider) {
        this.create = create;
        this.read = read;
        this.write = write;
        this.delete = delete;

        factory.addCatalog(CatalogActionRequest.NAME_FIELD, this);

        CatalogPlugin[] plugins = (CatalogPlugin[]) pluginProvider.get();
        Command[] actions;
        for(CatalogPlugin plugin: plugins){
            actions = plugin.getCatalogActions();
            if(actions!=null){
                for(Command action:actions){
                    addCommand(action.getClass().getSimpleName(),action);
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

        addCommand(CatalogActionRequest.CREATE_ACTION, create);
        addCommand(CatalogActionRequest.READ_ACTION, read);
        addCommand(CatalogActionRequest.WRITE_ACTION, write);
        addCommand(CatalogActionRequest.DELETE_ACTION, delete);
        addCommand(CatalogFileUploadTransaction.class.getSimpleName(), null/*FIXME*/);

    }



    @Override
    public Command getRead() {
        return read;
    }

    @Override
    public Command getWrite() {
        return write;
    }

    @Override
    public Command getDelete() {
        return delete;
    }

    @Override
    public Command getNew() {
        return create;
    }
}
