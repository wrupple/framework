package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.WriteFormatedDocument;
import org.apache.commons.chain.CatalogFactory;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.server.service.FormatDictionary;
import com.wrupple.muba.bootstrap.server.service.impl.Dictionary;
import com.wrupple.muba.bpm.server.chain.FormatManager;

@Singleton
public class FormatDictionaryImpl extends Dictionary implements FormatDictionary {


	@Inject
	public FormatDictionaryImpl(CatalogFactory factory, WriteFormatedDocument documentWriter) {
		super(documentWriter/*default writer*/);
		factory.addCatalog(CatalogActionRequest.CATALOG_FIELD, this);
		addCommand(WruppleDomainHTMLPage.CATALOG, documentWriter);
		addCommand(WruppleDomainJavascript.CATALOG, documentWriter);
		addCommand(WruppleDomainStyleSheet.CATALOG, documentWriter);
		addCommand(WrupleSVGDocument.CATALOG, documentWriter);
	}


    public boolean isFileField(FieldDescriptor field) {
        String catalog = field.getCatalog();
        return (field.isKey() && catalog != null
                && (catalog.equals(PersistentImageMetadata.CATALOG) || catalog.equals(WrupleSVGDocument.CATALOG)
                || catalog.equals(WruppleFileMetadata.CATALOG) || catalog.equals(WruppleAudioMetadata.CATALOG)
                || catalog.equals(WruppleVideoMetadata.CATALOG)));
    }

}
