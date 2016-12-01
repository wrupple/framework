package com.wrupple.muba.catalogs.server.chain.command;

import org.apache.commons.chain.Command;

public interface PublishEvents extends Command {

	final String CHANNEL_DICTIONARY = "catalog.event.channel";

}
