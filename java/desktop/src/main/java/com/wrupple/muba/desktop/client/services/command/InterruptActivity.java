package com.wrupple.muba.desktop.client.services.command;

/**
 * Exits current Activity without commiting
 * 
 * Used to determine the next Desktop Place following user interaction after an activity has finished.
 * Assumes user output to be a Catalog Entry, or an Array of Catalog Entries. then reads the first token of the command line
 * and excecutes the command callback passing a DesktopPlace pointing to the activity specified by the first command line token, with
 * catalog entry and catalog arguments WITHOUT commiting the current activity.
 * 
 * @author japi
 *
 */
public interface InterruptActivity extends CommandService {
	
	String COMMAND ="interrupt"; // formerly: "gotoActivity";
	
	/**
	 * @param targetActivity separated by slashes  in the form of "token/token/token"
	 */
	public void setTargetActivity(String targetActivity);

}
