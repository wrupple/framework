package com.wrupple.muba.event.server.chain.command;

import com.wrupple.muba.event.domain.BroadcastContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;
import org.apache.commons.chain.Command;

import javax.inject.Provider;

/**
 * Created by japi on 22/04/17.
 */
public interface RequestInterpret extends Command<RuntimeContext> {

     Provider<? extends ServiceContext> getProvider(RuntimeContext runtime);

}
