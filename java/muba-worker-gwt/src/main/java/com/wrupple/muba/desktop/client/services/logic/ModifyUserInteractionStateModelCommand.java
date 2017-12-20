package com.wrupple.muba.desktop.client.services.logic;

import com.wrupple.muba.desktop.client.services.command.CommandService;
import com.wrupple.muba.desktop.shared.services.UserInteractionStateModifier;

/**
 * All about modifieng the model of the user interaction state (Visible widgets on the screen)
 * 
 * So it wires how data passes from one widget to another, basically. No Widget alterations per se.
 * 
 * @author japi
 *
 */
public interface ModifyUserInteractionStateModelCommand extends CommandService,
		UserInteractionStateModifier {
	String COMMAND = "modifyModel";
	
	void setModelAlterationFunction(String f);
}
