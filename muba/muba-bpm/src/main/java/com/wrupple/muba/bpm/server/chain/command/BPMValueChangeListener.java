package com.wrupple.muba.bpm.server.chain.command;

import org.apache.commons.chain.Command;

public interface BPMValueChangeListener extends Command {
	 String CONTEXT_TRIGGERS_KEY = "VChangeL-triggers";
	 String WILDCARD = "\\*";
	
	
}
