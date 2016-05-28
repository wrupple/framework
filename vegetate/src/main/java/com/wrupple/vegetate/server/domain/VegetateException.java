package com.wrupple.vegetate.server.domain;

import com.wrupple.vegetate.domain.VegetateUserException;

public class VegetateException extends RuntimeException implements VegetateUserException {

	private static final long serialVersionUID = 5745506172353568944L;
	private static final StackTraceElement[] HIDDEN_TRACE = new StackTraceElement[]{new StackTraceElement("Class", "method", "file", 0)};
	private final int errorCode;
	
	public VegetateException(String message,int errorCode, Throwable cause) {
		super(message, cause);
		this.errorCode=errorCode;
	}

	@Override
	public int getErrorCode() {
		return errorCode;
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		if(System.getProperty(HIDE_STACK)==null){
			return super.getStackTrace();
		}else{
			return HIDDEN_TRACE;
		}
	}
	
}
