package com.wrupple.muba.catalogs.server.chain.command;

import com.wrupple.muba.event.server.chain.command.UserCommand;

public interface DataCreationCommand extends UserCommand {

    boolean isSequential();
}
