package com.wrupple.vegetate.server.services;

/**
 * 
 * Reports errors when attempting to communicate with another vegetate instance
 * 
 * @author japi
 *
 * @param
 * 			<P>
 *            type of the vegetate payload
 */
public interface ErrorAccuser<P> {
	void report(Exception e, P request);
}
