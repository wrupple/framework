package com.wrupple.muba.desktop.shared.services.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.impl.CatalogBase;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.desktop.server.chain.command.BrowserWriter;
import com.wrupple.muba.desktop.server.chain.command.DetailWriter;
import com.wrupple.muba.desktop.server.chain.command.FormWriter;
import com.wrupple.muba.desktop.shared.services.UserTaskWriterDictionary;

@Singleton
public class UserTaskWriterDictionaryImpl extends CatalogBase implements UserTaskWriterDictionary {
	// could have been used in a LookupCommand... but it isnt
	@Inject
	public UserTaskWriterDictionaryImpl(FormWriter editWriter, DetailWriter detailWriter, BrowserWriter selectionWriter) {
		super();
		super.addCommand(CatalogActionRequest.CREATE_ACTION, editWriter);
		super.addCommand(CatalogActionRequest.WRITE_ACTION, editWriter);
		super.addCommand(CatalogActionRequest.READ_ACTION, detailWriter);
		super.addCommand(ProcessTaskDescriptor.SELECT_COMMAND, selectionWriter);
	}
}
