package com.wrupple.muba.bootstrap.server.chain.command;

import org.apache.commons.chain.Command;

import com.wrupple.muba.bootstrap.domain.UserContext;

public interface UserCommand extends Command {
	
	// is async?

	void undo(UserContext context) throws Exception;
	
}
