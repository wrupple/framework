package com.wrupple.muba.desktop.client.services.logic;

import com.wrupple.muba.desktop.client.services.command.CommandService;

public interface ProcessSwitchCommand extends CommandService {
	String COMMAND = "processSwitch";
	String ID_PARAMETER="switchedProcessId";
}
