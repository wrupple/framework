package com.wrupple.muba.event.server;

import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.SessionContext;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Singleton
public class ExplicitIntentInterpret implements NaturalLanguageInterpret {

    private final EventBus bus;

    @Inject
    public ExplicitIntentInterpret(EventBus bus) {
        this.bus = bus;
    }

    @Override
    public void run(ListIterator<String> sentence, Context context, String interpretGivenName) throws Exception {

        List<String> tokens = new ArrayList<String>();
        while (sentence.hasNext()) {
            tokens.add(sentence.next());
        }

        if (context instanceof SessionContext) {
            context = new RuntimeContextImpl(bus, (SessionContext) context);
        }
        ((RuntimeContext) context).setSentence(tokens);
        ((RuntimeContext) context).setServiceContract(null);
        ((RuntimeContext) context).process();
    }
}
