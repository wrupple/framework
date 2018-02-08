package com.wrupple.muba.desktop.client.chain;

import com.wrupple.muba.desktop.domain.DesktopRequestContext;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

public interface ContainerRequestEngine extends Chain {

    public interface Handler extends Command<DesktopRequestContext>{

    }

}
