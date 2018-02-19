package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.desktop.client.chain.command.BindApplication;
import com.wrupple.muba.desktop.domain.WorkerContract;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.worker.domain.impl.ApplicationStateImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class BindApplicationImpl implements BindApplication {

    protected static final Logger log = LoggerFactory.getLogger(BindApplicationImpl.class);

    private final FieldAccessStrategy plugin;

    @Inject
    public BindApplicationImpl(FieldAccessStrategy plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean execute(WorkerRequestContext context) throws Exception {


        WorkerContract request = context.getRequest();

        List<String> sentence = request.getSentence();

        WorkerState container = context.getWorkerState();

        container.setSentence(sentence);

        int currentIndex = 0;
        //FIXME ONLY READ HOME FROM TOKENS IF GRAMMAR DEMANDS IT

        Application initialState = getInitialActivity(sentence,currentIndex,container.getApplicationTree());

        String stateType = (String) initialState.getCatalog();
        ApplicationState state;
        if(stateType==null){


            state= new ApplicationStateImpl();
        }else{

            CatalogActionRequestImpl solutionTypeInquiry = new CatalogActionRequestImpl();
            solutionTypeInquiry.setEntry(stateType);
            solutionTypeInquiry.setCatalog(CatalogDescriptor.CATALOG_ID);
            solutionTypeInquiry.setName(DataContract.READ_ACTION);
            List results = context.getRuntimeContext().getServiceBus().fireEvent(solutionTypeInquiry,context.getRuntimeContext(),null);

            CatalogDescriptor solutionDescriptor = (CatalogDescriptor) results.get(0);
            state= (ApplicationState) plugin.synthesize(solutionDescriptor);

        }


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
