package com.wrupple.muba.event.server.service.impl;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;

/**
 * a dictionary is a catalog with a default outcome
 * 
 * @author japi
 *
 */
public class Dictionary extends CatalogBase {

	protected final Command defaultCommand;

	public Dictionary(Command defaultCommand) {
		this.defaultCommand = defaultCommand;
	}

	@Override
	public Command getCommand(String name) {
		Command r = super.getCommand(name);
		if (r == null) {
			return defaultCommand;
		} else {
			return r;
		}
	}
}
