package com.wrupple.muba.cms.server.chain.command.impl;

import javax.inject.Inject;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.server.chain.command.WriteFormatedDocument;
import com.wrupple.muba.cms.server.chain.ContentManager;
import com.wrupple.vegetate.domain.CatalogActionRequest;

public abstract class ContentDocumentManager implements ContentManager {
	protected static final Logger log = LoggerFactory.getLogger(ContentDocumentManager.class);
	private final ContentManager defaultCommand;
	private final WriteFormatedDocument templator;

	@Inject
	public ContentDocumentManager(ContentManager defaultCommand, WriteFormatedDocument templator) {
		this.defaultCommand = defaultCommand;
		this.templator = templator;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		String downloadActionToken = (String) c.get(CatalogActionRequest.FORMAT_PARAMETER);

		if (downloadActionToken == null) {
			return defaultCommand.execute(c);
		} else {
			return templator.execute(c);
		}

	}

}
