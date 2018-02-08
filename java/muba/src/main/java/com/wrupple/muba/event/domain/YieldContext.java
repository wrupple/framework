package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasSentence;
import org.apache.commons.chain.Context;

import java.util.ListIterator;

public interface YieldContext extends Context,ListIterator<String>,HasSentence {

    /*
     *
     *
     */

    void setNextWordIndex(int i);

    public void reset();
}
