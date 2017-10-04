package com.wrupple.muba.bpm.server.chain.command;

import com.wrupple.muba.desktop.client.services.logic.OutputHandler;
import com.wrupple.muba.desktop.shared.services.UrlParser;


/**
 * Output Handler:
 * 
 * Used to determine the next Desktop Place following user interaction after an activity has finished.
 * 
 * Assumes user output to be a Catalog Entry, or an Array of Catalog Entries. then reads the first token of the command line
 * and excecutes the command callback passing a DesktopPlace pointing to the activity specified by the first command line token, with
 * catalog entry and catalog arguments WITHOUT suspenging the current activity.
 * 
 * @author japi
 *
 */
public interface ExplicitOutputPlace extends OutputHandler {
	
	String COMMAND = UrlParser.EXPLICIT_APPLICATION_ITEM;
	String URL_PARAMETER_POSTFIX = "_urlParameter";
	
	String CONTEXT_PROPERTY_POSTFIX = "_contextProperty";

	

}
