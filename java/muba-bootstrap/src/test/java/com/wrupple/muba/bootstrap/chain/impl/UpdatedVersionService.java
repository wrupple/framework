package com.wrupple.muba.bootstrap.chain.impl;

import com.wrupple.muba.BootstrapTest;
import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bootstrap.domain.reserved.HasResult;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by japi on 21/08/17.
 */
public abstract class UpdatedVersionService implements Command {

    protected Logger log = LoggerFactory.getLogger(UpdatedVersionService.class);

    public UpdatedVersionService() {
    }


    @SuppressWarnings("unchecked")
    @Override
    public boolean execute(Context ctx) throws Exception {
        RuntimeContext context = (RuntimeContext) ctx;
        String first = (String) context.get(BootstrapTest.FIRST_OPERAND_NAME);
        String second = (String) context.get(BootstrapTest.SECOND_OPERAND_NAME);
        // is there an operation named like this?
        if (context.getApplication().getRootService().getVersions(second) != null) {
            log.trace("delegating to {}, to find the second term", second);

            context.setNextWordIndex(context.nextIndex() - 1);
            context.process();

            log.trace("RESUMING WITH OPERANDS {},{}", first, ((HasResult) context).getConvertedResult());
            ((HasResult) context).setResult(
                    operation(Double.parseDouble(first), (Double) ((HasResult) context).getConvertedResult()));
            return CONTINUE_PROCESSING;
        } else {
            log.trace("new OPERANDS {},{}", first, second);
            ((HasResult) context).setResult(operation(Double.parseDouble(first), Double.parseDouble(second)));
            return CONTINUE_PROCESSING;
        }

    }

    protected abstract Double operation(Double first, Double second);
}
