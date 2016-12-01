package com.wrupple.muba.bpm.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.generic.LookupCommand;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bpm.server.chain.ContentManager;
import com.wrupple.muba.catalogs.server.chain.command.FormatResultSet;

@Singleton
public class ContentManagerImpl extends LookupCommand implements ContentManager {

	private final FormatResultSet defaultFormat;

	@Inject
	public ContentManagerImpl(CatalogFactory factory, FormatResultSet defaultFormat) {
		super(factory);
		this.defaultFormat = defaultFormat;
		super.setNameKey(CatalogActionRequest.FORMAT_PARAMETER);
		super.setCatalogName(CatalogActionRequest.FORMAT_PARAMETER /*ContentFormatDictionary*/);
	}

	@Override
	public boolean execute(Context c) throws Exception {

		String format = super.getCommandName(c);

		if (format == null) {
			return defaultFormat.execute(c);
		} else {
			/*ideally ContentFormatDictionary should pick commands by regex, return null as fallback*/
			Command command = super.getCatalogFactory().getCatalog(super.getCatalogName()).getCommand(format);
			if (command == null) {
				return defaultFormat.execute(c);
			} else {
				return command.execute(c);
			}
		}

	}

}
