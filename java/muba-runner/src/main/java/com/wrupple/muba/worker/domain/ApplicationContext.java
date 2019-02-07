package com.wrupple.muba.worker.domain;


import com.wrupple.muba.event.domain.*;
import org.apache.commons.chain.Command;

import java.util.ListIterator;

/**
 * Yields task grammar tokens
 *
 * Created by japi on 11/05/17.
 */
public interface ApplicationContext extends ServiceContext {
    String CATALOG = "ApplicationContext";


    ApplicationState getStateValue();

    void setStateValue(ApplicationState state);


    void setName(String command);

    ListIterator<String> getTaskGrammar();

    ListIterator<String> getWorkerSentence();
}