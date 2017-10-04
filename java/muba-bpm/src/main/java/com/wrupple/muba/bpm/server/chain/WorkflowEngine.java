package com.wrupple.muba.bpm.server.chain;

import org.apache.commons.chain.Command;

public interface WorkflowEngine extends Command{


    final String NEXT_APPLICATION_ITEM = "next";
    final String EXPLICIT_APPLICATION_ITEM = "explicit";
    final String GOTO_OUTPUT_ITEM = "goTo";
}
