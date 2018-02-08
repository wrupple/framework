package com.wrupple.muba.desktop.client.chain;

import com.wrupple.muba.desktop.domain.ContainerContext;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

public interface LaunchWorkerEngine extends Chain<ContainerContext> {

    public interface  Handler extends Command<ContainerContext>{

    }
}
