package com.wrupple.muba.bootstrap.server.chain.command;

import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 22/04/17.
 */
public interface RequestInterpret extends Command {
    Context materializeBlankContext(ExcecutionContext requestContext);
}
