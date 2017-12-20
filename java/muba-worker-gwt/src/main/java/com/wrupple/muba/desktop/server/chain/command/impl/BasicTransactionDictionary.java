package com.wrupple.muba.desktop.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.LazyCommand;
import com.wrupple.muba.catalogs.server.service.impl.TransactionDictionaryImpl;
import com.wrupple.muba.desktop.server.chain.command.CatalogPublishingTransaction;

import javax.inject.Inject;
import javax.inject.Provider;

public class BasicTransactionDictionary extends TransactionDictionaryImpl {

	@Inject
	public BasicTransactionDictionary(Provider<CatalogCreateTransaction> create, Provider<CatalogReadTransaction> read,
			Provider<CatalogUpdateTransaction> write, Provider<CatalogDeleteTransaction> delete, Provider<CatalogFileUploadTransaction> upload,
			Provider<CatalogFileUploadUrlHandlerTransaction> url, Provider<CatalogPublishingTransaction> publish) {
		super(create, read, write, delete, upload, url);
		super.addCommand(CatalogActionRequest.PUBLISH_ACTION, new LazyCommand(publish));
		// FIXME publish transaction (by credentials module) even if it means
		// sublassing/recreating this class
	}

}
