package com.wrupple.muba.worker.server.chain.command;

import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.service.StateTransition;
import org.apache.commons.chain.Command;

/**
 * Created by rarl on 11/05/17.
 */
public interface SolveTask extends Command {

    interface Callback extends StateTransition<ApplicationContext> {

    }
}
