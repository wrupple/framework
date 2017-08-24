package com.wrupple.muba.event.chain.impl;

import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wrupple.muba.BootstrapTest.FIRST_OPERAND_NAME;
import static com.wrupple.muba.BootstrapTest.SECOND_OPERAND_NAME;

/**
 * Created by japi on 21/08/17.
 */
public abstract class OldVesionService implements Command {
    protected Logger log = LoggerFactory.getLogger(UpdatedVersionService.class);

    @SuppressWarnings("unchecked")
    @Override
    public boolean execute(Context context) throws Exception {
        String first = (String) context.get(FIRST_OPERAND_NAME);
        String second = (String) context.get(SECOND_OPERAND_NAME);
        log.trace("default OPERANDS {},{}", first, second);
        ((RuntimeContext) context).setResult(operation(Integer.parseInt(first), Integer.parseInt(second)));
        return CONTINUE_PROCESSING;

    }

    protected abstract int operation(int first, int second);
}