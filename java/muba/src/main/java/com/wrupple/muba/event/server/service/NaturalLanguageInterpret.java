package com.wrupple.muba.event.server.service;

import org.apache.commons.chain.Context;

import java.util.ListIterator;

public interface NaturalLanguageInterpret {
    void run(ListIterator<String> sentence, Context context) throws Exception;
}
