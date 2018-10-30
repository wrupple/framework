package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.WriteFormatedDocument;
import org.apache.commons.chain.CatalogFactory;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.server.service.FormatDictionary;
import com.wrupple.muba.event.server.service.impl.Dictionary;

@Singleton
public class FormatDictionaryImpl extends Dictionary implements FormatDictionary {


	@Inject
	public FormatDictionaryImpl(CatalogFactory factory, WriteFormatedDocument documentWriter) {
		super(documentWriter /*default writer*/);
		factory.addCatalog(CatalogActionRequest.CATALOG_FIELD, this);
		addCommand(WruppleDomainHTMLPage.CATALOG, documentWriter);
		addCommand(WruppleDomainJavascript.CATALOG, documentWriter);
		addCommand(WruppleDomainStyleSheet.CATALOG, documentWriter);
		addCommand(WrupleSVGDocument.CATALOG, documentWriter);
	}

}
