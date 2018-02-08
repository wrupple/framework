package com.wrupple.muba.event.server.domain.impl;

import com.wrupple.muba.event.domain.YieldContext;
import org.apache.commons.chain.impl.ContextBase;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class AbstractYieldContext extends ContextBase implements YieldContext{

    //@Sentence
    List<String> sentence;
    ListIterator<String> wordIterator;


    private ListIterator<String> assertSentenceIterator() {
        if (wordIterator == null) {
            wordIterator = sentence.listIterator();
        }
        return wordIterator;

    }

    @Override
    public void reset() {
        wordIterator=null;
    }


    @Override
    public void setNextWordIndex(int nextTokenIndex) {
        wordIterator = sentence.listIterator(nextTokenIndex);
    }



    @Override
    public List<String> getSentence() {
        return sentence;
    }


    @Override
    public boolean hasNext() {
        return assertSentenceIterator().hasNext();
    }

    @Override
    public String next() {
        return assertSentenceIterator().next();
    }

    @Override
    public boolean hasPrevious() {
        return assertSentenceIterator().hasPrevious();
    }

    @Override
    public String previous() {

        return assertSentenceIterator().previous();
    }

    @Override
    public int nextIndex() {
        return assertSentenceIterator().nextIndex();
    }

    @Override
    public int previousIndex() {
        return assertSentenceIterator().previousIndex();
    }

    @Override
    public void remove() {
        assertSentenceIterator().remove();
    }

    @Override
    public void set(String e) {
        assertSentenceIterator().set(e);
    }

    @Override
    public void add(String e) {
        assertSentenceIterator().add(e);
    }


}
