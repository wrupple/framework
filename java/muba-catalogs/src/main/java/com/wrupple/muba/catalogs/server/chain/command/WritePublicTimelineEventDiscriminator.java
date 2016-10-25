package com.wrupple.muba.catalogs.server.chain.command;

import org.apache.commons.chain.Command;

public interface WritePublicTimelineEventDiscriminator extends Command {

	String getDiscriminatorField();
	
	String getCatalogField();

}
