package com.wrupple.muba.desktop.client.services.command;

import com.wrupple.muba.desktop.client.services.logic.OutputHandler;
import com.wrupple.muba.desktop.shared.services.UrlParser;

/**
 * Output Handler:
 * 
 * Used mainly by navigation activities
 * 
 * Expects User Output to be an array of Application Items, the first of which will be used to determine 
 * the next place the desktop is pointed to. That DesktopPlace object is passed to the command callback
 * 
 * @author japi
 *
 */
public interface GoToCommand extends OutputHandler {

	String COMMAND = UrlParser.GOTO_OUTPUT_ITEM;

}