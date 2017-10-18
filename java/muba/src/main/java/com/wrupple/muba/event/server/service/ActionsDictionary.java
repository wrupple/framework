package com.wrupple.muba.event.server.service;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;

/**
 * 
 * 
 * @author japi
 *
 */
public interface ActionsDictionary extends Catalog {
	Command getNew();

	Command getRead();

	Command getWrite();

	Command getDelete();
}
