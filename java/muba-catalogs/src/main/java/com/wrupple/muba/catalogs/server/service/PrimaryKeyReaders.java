package com.wrupple.muba.catalogs.server.service;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;

public interface PrimaryKeyReaders extends Catalog {

    Command getDefault();
}
