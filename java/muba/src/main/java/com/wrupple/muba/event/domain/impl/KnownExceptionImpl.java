package com.wrupple.muba.event.domain.impl;

/**
 * Created by japi on 3/08/17.
 */
public class KnownExceptionImpl extends Exception {

    private final int errorCode;

    public KnownExceptionImpl(int errorCode) {
        this.errorCode = errorCode;
    }

    public KnownExceptionImpl(String s, int errorCode) {
        super(s);
        this.errorCode = errorCode;
    }

    public KnownExceptionImpl(String s, Throwable throwable, int errorCode) {
        super(s, throwable);
        this.errorCode = errorCode;
    }

    public KnownExceptionImpl(Throwable throwable, int errorCode) {
        super(throwable);
        this.errorCode = errorCode;
    }

    public KnownExceptionImpl(String s, Throwable throwable, boolean b, boolean b1, int errorCode) {
        super(s, throwable, b, b1);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
