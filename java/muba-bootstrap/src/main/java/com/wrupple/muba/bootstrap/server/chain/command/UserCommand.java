package com.wrupple.muba.bootstrap.server.chain.command;

import com.wrupple.muba.bootstrap.domain.ServiceContext;
import org.apache.commons.chain.Command;

import com.wrupple.muba.bootstrap.domain.ServiceContext;

public interface UserCommand extends Command {
	
	// is async?

	void undo(ServiceContext context) throws Exception;
	
}
