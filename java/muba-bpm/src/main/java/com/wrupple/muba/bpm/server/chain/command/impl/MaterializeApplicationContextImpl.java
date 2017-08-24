package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.BusinessEvent;
import com.wrupple.muba.bpm.domain.impl.ApplicationStateImpl;
import com.wrupple.muba.bpm.server.chain.command.MaterializeApplicationContext;
import com.wrupple.muba.bpm.server.domain.BusinessContext;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by japi on 16/08/17.
 */
public class MaterializeApplicationContextImpl implements MaterializeApplicationContext {

    protected Logger log = LoggerFactory.getLogger(MaterializeApplicationContextImpl.class);


    @Override
    public boolean execute(Context ctx) throws Exception {
        BusinessContext context = (BusinessContext) ctx;
        BusinessEvent contractExplicitIntent = (BusinessEvent) context.getRuntimeContext().getServiceContract();
        Long existingApplicationState = (Long) contractExplicitIntent.getState();

        ApplicationState applicationContext;
        CatalogActionRequestImpl request= new CatalogActionRequestImpl();
        //FIXME create/read application context of the right type
        request.setCatalog(ApplicationState.CATALOG);
        if(existingApplicationState==null){
            //create new application state
            applicationContext= new ApplicationStateImpl();
            try{
                BeanUtils.copyProperties(applicationContext,contractExplicitIntent);
            }catch(IllegalAccessException|InvocationTargetException e){
                log.warn("error while copying contract properties to new application context",e);
            }

            request.setEntryValue(applicationContext);
            request.setName(CatalogActionRequest.CREATE_ACTION);
        }else{
            //recover application state
            request.setEntry(existingApplicationState);
            request.setName(CatalogActionRequest.READ_ACTION);
        }

        applicationContext=context.getRuntimeContext().spawnProcess(request);

        context.getRuntimeContext().setResult(applicationContext);


        return CONTINUE_PROCESSING;
    }
}
