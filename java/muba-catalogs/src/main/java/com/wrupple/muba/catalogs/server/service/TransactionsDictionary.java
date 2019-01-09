package com.wrupple.muba.catalogs.server.service;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;

public interface TransactionsDictionary extends Catalog {
    Command getNew();

    Command getWrite();

    Command getDelete();
}
