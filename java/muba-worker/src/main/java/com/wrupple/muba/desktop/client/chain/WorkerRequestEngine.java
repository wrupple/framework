package com.wrupple.muba.desktop.client.chain;

import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

public interface WorkerRequestEngine extends Chain {

    public interface Handler extends Command<WorkerRequestContext>{

    }

}
