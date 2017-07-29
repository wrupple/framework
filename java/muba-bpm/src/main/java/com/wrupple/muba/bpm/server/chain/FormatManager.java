package com.wrupple.muba.bpm.server.chain;

import org.apache.commons.chain.Command;

import com.wrupple.muba.catalogs.server.chain.command.FormatResultSet;

public interface FormatManager extends Command {
	/**
	 * @deprecated Use {@link FormatResultSet#EMPTY_RESPONSE} instead
	 */
	static final String EMPTY_RESPONSE = FormatResultSet.EMPTY_RESPONSE;

}
