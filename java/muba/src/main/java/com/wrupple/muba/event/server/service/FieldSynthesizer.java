package com.wrupple.muba.event.server.service;

import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.*;
import org.apache.commons.chain.Context;

import java.io.PrintWriter;
import java.util.ListIterator;

public interface FieldSynthesizer {

    void evalTemplate(String value, PrintWriter out, String locale, ServiceContext ccontext);

    Object synthethizeFieldValue(ListIterator<String> split, Context context, CatalogEntry subject, ContractDescriptor subjectType, FieldDescriptor generated, Instrospection intro,ServiceBus serviceBus) throws Exception;

}
