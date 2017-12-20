package com.wrupple.muba.desktop.client.services.presentation;

import com.wrupple.muba.desktop.client.services.command.CommandService;
import com.wrupple.muba.desktop.shared.services.UserInteractionStateModifier;

/**
 * 
 * Mediates visual alteration to the user interaction state (Visible widgets). No data related processing
 * 
 * @author japi
 *
 */
public interface ModifyUserInteractionStatePanelCommand extends CommandService,UserInteractionStateModifier {
	String COMMAND = "modifyDesktop";

	

}
