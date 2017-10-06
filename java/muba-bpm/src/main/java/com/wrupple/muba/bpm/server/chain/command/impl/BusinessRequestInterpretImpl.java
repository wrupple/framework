package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.BusinessIntent;
import com.wrupple.muba.bpm.domain.impl.ApplicationStateImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.bpm.server.chain.command.BusinessRequestInterpret;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by japi on 16/08/17.
 */
@Singleton
public class BusinessRequestInterpretImpl implements BusinessRequestInterpret {

    protected Logger log = LoggerFactory.getLogger(BusinessRequestInterpretImpl.class);


    private Provider<ApplicationContext> proveedor;

    @Inject
    public BusinessRequestInterpretImpl(Provider<ApplicationContext> proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {

        return proveedor.get().setRuntimeContext(requestContext);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        RuntimeContext requestContext = (RuntimeContext) ctx;
        BusinessIntent contractExplicitIntent = (BusinessIntent) requestContext.getServiceContract();
        ApplicationContext context = requestContext.getServiceContext();

        ApplicationState applicationContext = (ApplicationState) contractExplicitIntent.getStateValue();

        if(applicationContext==null){
            Object existingApplicationStateId = contractExplicitIntent.getState();



            if(existingApplicationStateId==null){
                //create new application state
                applicationContext= context.getProcessManager().acquireContext(contractExplicitIntent,requestContext.getSession());

            }else{
                //recover application state
                CatalogActionRequestImpl request= new CatalogActionRequestImpl();
                //FIXME create/read application context of the right type
                request.setCatalog(ApplicationState.CATALOG);
                request.setEntry(existingApplicationStateId);
                request.setName(CatalogActionRequest.READ_ACTION);

                applicationContext=context.getRuntimeContext().getEventBus().fireEvent(request,context.getRuntimeContext(),null);
            }


        }
        application staate is not comming back from read
        context.setStateValue(applicationContext);

        return CONTINUE_PROCESSING;
    }
}
