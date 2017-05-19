package com.wrupple.muba.desktop.server.chain;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import com.wrupple.muba.desktop.server.chain.command.DesktopRequestReader;
import com.wrupple.muba.desktop.server.chain.command.DesktopResponseConfigure;

@Singleton
public class DesktopEngineImpl extends ChainBase implements DesktopEngine {

	@Inject
	public DesktopEngineImpl(DesktopRequestReader reader, DesktopResponseConfigure configure) {
		super(new Command[] { reader, configure });
	}
}
