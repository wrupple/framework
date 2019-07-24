package com.wrupple.muba.event;

import com.google.inject.AbstractModule;
import com.wrupple.muba.event.server.chain.command.impl.JavaSentenceNativeInterface;
import com.wrupple.muba.event.server.chain.command.impl.PublishEventsImpl;
import com.wrupple.muba.event.server.service.impl.ServiceBusImpl;
import com.wrupple.muba.event.server.service.impl.IntentDelegateImpl;
import com.wrupple.muba.event.server.service.impl.PublishEventsImplStreamingDelegateImpl;
import com.wrupple.muba.event.server.service.impl.SimplJavaSentenceNativeInterfaceDelegate;

public class LegacyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ServiceBusImpl.IntentDelegate.class).to(IntentDelegateImpl.class);
        bind(JavaSentenceNativeInterface.Delegate.class).to(SimplJavaSentenceNativeInterfaceDelegate.class);
        bind(PublishEventsImpl.StreamingDelegate.class).to(PublishEventsImplStreamingDelegateImpl.class);
    }
}
