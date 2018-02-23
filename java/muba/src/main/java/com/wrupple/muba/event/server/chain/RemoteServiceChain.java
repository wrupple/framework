package com.wrupple.muba.event.server.chain;

import com.wrupple.muba.event.domain.RemoteServiceContext;
import org.apache.commons.chain.Command;

public interface RemoteServiceChain extends Command<RemoteServiceContext>{

    public interface Link extends Command<RemoteServiceContext>{

    }
}
