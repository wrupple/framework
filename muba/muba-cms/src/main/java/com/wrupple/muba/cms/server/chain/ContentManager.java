package com.wrupple.muba.cms.server.chain;

import org.apache.commons.chain.Command;

import com.wrupple.muba.catalogs.server.chain.command.FormatResultSet;

public interface ContentManager extends Command {
	/**
	 * @deprecated Use {@link FormatResultSet#EMPTY_RESPONSE} instead
	 */
	static final String EMPTY_RESPONSE = FormatResultSet.EMPTY_RESPONSE;
}
