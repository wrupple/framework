package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDeleteTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.service.TransactionsDictionary;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransactionsDictionaryImpl extends CatalogBase implements TransactionsDictionary {

    protected final Command create;

    protected final Command write;

    protected final Command delete;


    @Inject
    public TransactionsDictionaryImpl(CatalogCreateTransaction create,CatalogUpdateTransaction write, CatalogDeleteTransaction delete) {
        this.create = create;
        this.write = write;
        this.delete = delete;


        addCommand(CatalogActionRequest.CREATE_ACTION, create);
        addCommand(CatalogActionRequest.WRITE_ACTION, write);
        addCommand(CatalogActionRequest.DELETE_ACTION, delete);
    }

    @Override
    public Command getNew() {
        return create;
    }

    @Override
    public Command getWrite() {
        return write;
    }

    @Override
    public Command getDelete() {
        return delete;
    }
}
