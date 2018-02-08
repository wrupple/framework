package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.BindApplication;
import com.wrupple.muba.desktop.domain.WorkerRequest;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.ServiceManifest;

import java.util.List;

public class BindApplicationImpl implements BindApplication {
    @Override
    public boolean execute(WorkerRequestContext context) throws Exception {


        WorkerRequest request = context.getRequest();

        List<String> sentence = request.getSentence();

        WorkerState container = context.getWorkerState();

        container.setSentence(sentence);

        int currentIndex = 0;

        Application initialState = getInitialActivity(sentence,currentIndex,container.getApplicationTree());

        container.setWordIndex(new Long(currentIndex));

        container.getStateValue().setApplicationValue(initialState);
        container.getStateValue().setApplication(initialState.getId());

        return CONTINUE_PROCESSING;
    }



     Application getInitialActivity(List<String> sentence, int index, Application applicationTree) throws Exception {

        List<ServiceManifest> children = applicationTree.getChildrenValues();
        String token = sentence.get(index);
        for(ServiceManifest current : children){
            String dn = current.getVersionDistinguishedName();
            if(dn==null && dn.equals(token)){
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
        return applicationTree;
    }
}
