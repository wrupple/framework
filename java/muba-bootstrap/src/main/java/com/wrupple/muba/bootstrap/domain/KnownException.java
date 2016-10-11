package com.wrupple.muba.bootstrap.domain;

public interface KnownException {
	final String HIDE_STACK = "vegetate.hideStack";
	int DENIED = 401;
	int USER_UNKNOWN = 403;
	int UNKNOWN = 911;
	final int UNAVAILABLE_METADATA =400;
	final int UNREACHABLE = 501;

	int getErrorCode();
}
