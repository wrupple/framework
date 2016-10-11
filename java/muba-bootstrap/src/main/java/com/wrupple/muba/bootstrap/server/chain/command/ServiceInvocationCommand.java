package com.wrupple.muba.bootstrap.server.chain.command;

import org.apache.commons.chain.Command;

import com.wrupple.muba.bootstrap.domain.Bootstrap;

public interface ServiceInvocationCommand extends Command {
	
	public void setRootService(Bootstrap rootService);
}
