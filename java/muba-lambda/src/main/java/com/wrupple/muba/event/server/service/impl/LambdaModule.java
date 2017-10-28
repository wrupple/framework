package com.wrupple.muba.event.server.service.impl;

import com.google.inject.AbstractModule;
import com.wrupple.muba.event.server.chain.command.impl.JavaSentenceNativeInterface;
import com.wrupple.muba.event.server.chain.command.impl.PublishEventsImpl;

public class LambdaModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EventBusImpl.IntentDelegate.class).to(StreamingIntentDelegate.class);
        bind(JavaSentenceNativeInterface.Delegate.class).to(JavaSentenceNativeInterfaceDelegate.class);
        bind(PublishEventsImpl.StreamingDelegate.class).to(StreamingDelegateImpl.class);
    }
}
