package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.wrupple.muba.bpm.server.chain.command.impl.WriteOutputImpl;
import com.wrupple.muba.catalogs.server.chain.command.WriteOutput;

public class ContentModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(WriteOutput.class).to(WriteOutputImpl.class);
	}

}
