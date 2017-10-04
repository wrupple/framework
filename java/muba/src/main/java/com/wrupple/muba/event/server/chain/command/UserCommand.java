package com.wrupple.muba.event.server.chain.command;

import com.wrupple.muba.event.domain.ServiceContext;
import org.apache.commons.chain.Command;

public interface UserCommand extends Command {
	
	// is async?

	void undo(ServiceContext context) throws Exception;
	
}
