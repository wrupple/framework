package com.wrupple.muba.worker.server.chain;

import com.wrupple.muba.worker.server.chain.command.ExplicitOutputPlace;
import org.apache.commons.chain.Command;

public interface WorkflowEngine extends Command{


    String NEXT_APPLICATION_ITEM = "next";
    String EXPLICIT_APPLICATION_ITEM = ExplicitOutputPlace.COMMAND;
    String GOTO_OUTPUT_ITEM = "goTo";
}
