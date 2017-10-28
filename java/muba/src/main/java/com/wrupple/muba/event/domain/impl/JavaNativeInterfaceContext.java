package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.event.domain.reserved.HasResult;
import org.apache.commons.chain.impl.ContextBase;

import java.util.ListIterator;

/**
 * Created by rarl on 11/05/17.
 */
public class JavaNativeInterfaceContext extends ContextBase implements HasResult<Object>, HasDistinguishedName {
    public Object subject, result;


    private String distinguishedName;
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

    @Override
    public String getDistinguishedName() {
        return distinguishedName;
    }

    @Override
    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }
}
