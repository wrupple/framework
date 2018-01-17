package com.wrupple.muba.worker.server.chain.command;


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

}