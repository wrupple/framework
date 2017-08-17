package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bpm.domain.ProcessRequest;
import com.wrupple.muba.bpm.server.chain.command.BusinessRequestInterpret;
import com.wrupple.muba.bpm.server.domain.BusinessContext;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Created by japi on 16/08/17.
 */
@Singleton
public class BusinessRequestInterpretImpl implements BusinessRequestInterpret {

    private Provider<BusinessContext> proveedor;

    @Inject
    public BusinessRequestInterpretImpl(Provider<BusinessContext> proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {

        return proveedor.get().setRuntimeContext(requestContext);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        RuntimeContext requestContext = (RuntimeContext) ctx;
        ProcessRequest request = (ProcessRequest) requestContext.getServiceContract();
        BusinessContext context = requestContext.getServiceContext();

        BeanUtils.copyProperties(context,request);

        return CONTINUE_PROCESSING;
    }
}
