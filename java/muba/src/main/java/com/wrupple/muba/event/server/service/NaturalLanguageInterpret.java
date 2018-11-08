package com.wrupple.muba.event.server.service;

import org.apache.commons.chain.Context;

import java.util.ListIterator;

public interface NaturalLanguageInterpret {
    final String ASSIGNATION = "=";
    void resolve(ListIterator<String> sentence, Context context, String interpretGivenName) throws Exception;
}
