package com.wrupple.muba.event.server.chain.command;

import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 22/04/17.
 */
public interface RequestInterpret extends Command {
    Context materializeBlankContext(RuntimeContext requestContext);
}
