package com.wrupple.muba.bpm.server.chain;

import com.wrupple.muba.bpm.server.chain.command.ExplicitOutputPlace;
import org.apache.commons.chain.Command;

public interface WorkflowEngine extends Command{


    final String NEXT_APPLICATION_ITEM = "next";
    final String EXPLICIT_APPLICATION_ITEM = ExplicitOutputPlace.COMMAND;
    final String GOTO_OUTPUT_ITEM = "goTo";
}
