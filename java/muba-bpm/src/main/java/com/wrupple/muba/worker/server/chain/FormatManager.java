package com.wrupple.muba.worker.server.chain;

import com.wrupple.muba.catalogs.server.chain.command.FormatResultSet;
import org.apache.commons.chain.Command;

public interface FormatManager extends Command {
	/**
	 * @deprecated Use {@link FormatResultSet#EMPTY_RESPONSE} instead
	 */
    String EMPTY_RESPONSE = FormatResultSet.EMPTY_RESPONSE;

}
