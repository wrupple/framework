package com.wrupple.muba.desktop.domain.exceptions;

/**
 * Thrown when the DEsktopController is unable to find a suitable
 * sequentialViewWrapper to render the desktop in, usually because no default
 * sequentialViewWrapper name is set, and no vewProcessingFilters are set wither
 * 
 * @author japi
 * 
 */
public class NoDesktopViewResolvedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5770761549492403768L;

	public NoDesktopViewResolvedException() {
		super();
	}

	public NoDesktopViewResolvedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoDesktopViewResolvedException(String message) {
		super(message);
	}

	public NoDesktopViewResolvedException(Throwable cause) {
		super(cause);
	}

}
