package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.wrupple.muba.catalogs.server.chain.command.WriteOutput;
import com.wrupple.muba.catalogs.server.chain.command.impl.WriteOutputImpl;

public class ContentModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(WriteOutput.class).to(WriteOutputImpl.class);
	}

}
