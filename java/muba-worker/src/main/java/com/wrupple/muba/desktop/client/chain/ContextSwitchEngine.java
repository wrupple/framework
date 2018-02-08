package com.wrupple.muba.desktop.client.chain;

import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import org.apache.commons.chain.Command;

public interface ContextSwitchEngine extends Command {

    public interface Handler extends Command<ContextSwitchRuntimeContext> {

    }


}
