package com.wrupple.muba.event.server;

import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.ContainerContext;
import com.wrupple.muba.event.domain.RuntimeContext;
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
    public void resolve(ListIterator<String> sentence, Context context, String interpretGivenName) throws Exception {

        List<String> tokens = new ArrayList<String>();
        while (sentence.hasNext()) {
            tokens.add(sentence.next());
        }

        if (context instanceof ContainerContext) {
            context = new RuntimeContextImpl(bus, (ContainerContext) context);
        }
        ((RuntimeContext) context).setSentence(tokens);
        ((RuntimeContext) context).setServiceContract(null);
        ((RuntimeContext) context).process();
    }
}
