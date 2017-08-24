package com.wrupple.muba.event.domain;

/**
 * Created by japi on 21/08/17.
 */
public interface UserEvent extends ExplicitIntent {
    <T > T getConvertedResult();

    /**
     *
     * @return alias to getHandle()
     */
    String[] getSentence();

    void setResult(Object result);
}
