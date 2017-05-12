package com.wrupple.muba.bootstrap.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasResult;
import org.apache.commons.chain.impl.ContextBase;

import java.util.ListIterator;

/**
 * Created by rarl on 11/05/17.
 */
public class JavaNativeInterfaceContext extends ContextBase implements HasResult<Object> {
    public Object subject, result;
    public ListIterator<String> sentenceIterator;

    public JavaNativeInterfaceContext(Object subject, ListIterator<String> sentenceIterator) {
        this.subject = subject;
        this.sentenceIterator = sentenceIterator;
    }


    @Override
    public <T> T getConvertedResult() {
        return (T) result;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void setResult(Object o) {
        this.result=o;
    }
}
