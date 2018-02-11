package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.catalogs.server.chain.command.impl.DataJoiner;
import com.wrupple.muba.desktop.client.chain.command.BindApplication;
import com.wrupple.muba.desktop.domain.WorkerRequest;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.worker.domain.impl.ApplicationStateImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BindApplicationImpl implements BindApplication {

    protected static final Logger log = LoggerFactory.getLogger(BindApplicationImpl.class);

    @Override
    public boolean execute(WorkerRequestContext context) throws Exception {


        WorkerRequest request = context.getRequest();

        List<String> sentence = request.getSentence();

        WorkerState container = context.getWorkerState();

        container.setSentence(sentence);

        int currentIndex = 0;

        Application initialState = getInitialActivity(sentence,currentIndex,container.getApplicationTree());
        ApplicationStateImpl state = new ApplicationStateImpl();
        state.setDomain(container.getDomain());
        state.setWorkerStateValue(container);
        state.setApplicationValue(initialState);
        state.setApplication(initialState.getId());
        container.setWordIndex(new Long(currentIndex));
        container.setStateValue(state);

        return CONTINUE_PROCESSING;
    }



     Application getInitialActivity(List<String> sentence, int index, Application applicationTree) throws Exception {

        if(sentence.size()>index){
            String token = sentence.get(index);

            List<ServiceManifest> children = applicationTree.getChildrenValues();
            if(children==null|| children.isEmpty()){
                log.warn("Activity has no children and will be used by defult");
            }else{


                for(ServiceManifest current : children){
                    String dn = current.getDistinguishedName();
                    if(dn!=null && dn.equals(token)){
                        index = index +1;
                        return getInitialActivity(sentence,index, (Application) current);
                    }else {
                        Object id = current.getId();
                        if(id!=null){
                            //TODO modular format
                            String idAsString = id.toString();
                            if(idAsString.equals(id)){
                                index = index +1;
                                return getInitialActivity(sentence,index, (Application) current);
                            }
                        }
                    }
                }
            }
        }else{
            log.warn("sentence had no more activity tokens");
        }

        return applicationTree;
    }
}
