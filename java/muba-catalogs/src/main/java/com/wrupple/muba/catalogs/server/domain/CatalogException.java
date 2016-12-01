package com.wrupple.muba.catalogs.server.domain;

public class CatalogException extends RuntimeException {

	private static final long serialVersionUID = -4115566289769776164L;

	public CatalogException() {
		super();
	}

	public CatalogException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CatalogException(String message, Throwable cause) {
		super(message, cause);
	}

	public CatalogException(String message) {
		super(message);
	}

	public CatalogException(Throwable cause) {
		super(cause);
	}

}
