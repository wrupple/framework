package com.wrupple.muba.event;

import com.google.inject.AbstractModule;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import com.wrupple.muba.event.server.chain.command.impl.JavaSentenceNativeInterface;
import com.wrupple.muba.event.server.chain.command.impl.PublishEventsImpl;
import com.wrupple.muba.event.server.service.impl.EventBusImpl;
import com.wrupple.muba.event.server.service.impl.IntentDelegateImpl;
import com.wrupple.muba.event.server.service.impl.PublishEventsImplStreamingDelegateImpl;
import com.wrupple.muba.event.server.service.impl.SimplJavaSentenceNativeInterfaceDelegate;
import org.apache.commons.chain.Context;

public class LegacyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EventBusImpl.IntentDelegate.class).to(IntentDelegateImpl.class);
        bind(EventSuscriptionMapper.class).toInstance(new EventSuscriptionMapper() {
            @Override
            public boolean execute(Context context) throws Exception {
                return CONTINUE_PROCESSING;
            }
        });
        bind(JavaSentenceNativeInterface.Delegate.class).to(SimplJavaSentenceNativeInterfaceDelegate.class);
        bind(PublishEventsImpl.StreamingDelegate.class).to(PublishEventsImplStreamingDelegateImpl.class);
    }
}
