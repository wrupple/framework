package com.wrupple.vegetate.domain;

public interface VegetateUserException {
	final String HIDE_STACK = "vegetate.hideStack";
	int DENIED = 401;
	int USER_UNKNOWN = 403;
	int UNKNOWN = 911;
	final int INVALID_METADATA =400;

	int getErrorCode();
}
