package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.CommitSubmission;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by japi on 18/08/17.
 */
public class CommitSubmissionImpl implements CommitSubmission {

    private final FieldAccessStrategy aceess;
    private final CatalogKeyServices keyServices;

    @Inject
    public CommitSubmissionImpl(FieldAccessStrategy aceess, CatalogKeyServices keyServices) {
        this.aceess = aceess;
        this.keyServices = keyServices;
    }


    @Override
    public boolean execute(Context ctx) throws Exception {

        ApplicationContext context = (ApplicationContext) ctx;
        ApplicationState applicationState = context.getStateValue();


        //is there submission material?
        CatalogEntry userOutput = applicationState.getEntryValue();
        if(userOutput==null){

        }else{
            Task task = applicationState.getTaskDescriptorValue();
            String producedField = task.getOutputField();
            //what task
            //what action
            //AQUI VA LO BUENO
            Instrospection instrospection = aceess.newSession(applicationState);

            //conditions from GWT desktop ( CommitEditTransaction CommitSelectTransaction )... do commit

            //FIXME this is handled on the client when an application State is updated
            if (producedField != null) {
                //TODO certain saveTo fields are reserved, like those in CatalogAction
                aceess.setPropertyValue(producedField,applicationState,userOutput.getId(), instrospection);

            }



            String transactionType = applicationState.getTaskDescriptorValue().getName();
            final String catalog = (String) applicationState.getTaskDescriptorValue().getCatalog();

            boolean canceled = applicationState.getCanceled();
            Object id =applicationState.getEntry();
            if(id ==null){
                id = userOutput.getId();
            }
            CatalogActionRequestImpl entryCommit = null;
            if(CatalogActionRequest.READ_ACTION.equals(transactionType)){
                // onDone.setResultAndFinish(context);
            }else  if(CatalogActionRequest.WRITE_ACTION.equals(transactionType)){
                /*
                 * THIS TRANSACTION HAS SOME SORT OF USER OUTPUT THAT MUST BE COMMITED
                 */

                if(userOutput==null){
                    //no user output, most likely canceled

                }else{

                /*boolean draft = entry.getDraft();
                if(draft){

                    if(canceled){
                        deleteDraft=true;
                        commitEntry=false;

                    }else{
                        //user interaction ended normally
                        commitEntry = true;
                        deleteDraft= false;
                    }
                }else{
                    if(canceled){
                        deleteDraft=true;
                        commitEntry=false;
                    }else{
                        //user interaction ended normally,
                        commitEntry = true;
                        deleteDraft= false;
                    }
                }

                //StateTransition<JsCatalogEntry> callback=new EntryUpdateCallback(context,onDone);
                if(commitEntry){
                    sm.update(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, id, entry, callback);
                }else if(deleteDraft){
                    sm.delete(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, id, callback);
                }*/

                    entryCommit= new CatalogActionRequestImpl();

                    entryCommit.setEntryValue(userOutput);
                    entryCommit.setEntry(id);
                    entryCommit.setCatalog(catalog);
                    entryCommit.setName(transactionType);
                }

            }else if(CatalogActionRequest.CREATE_ACTION.equals(transactionType)){
                if(canceled){
                    //no commiting required
                    //onDone.setResultAndFinish(context);
                }else{
                    //final StateTransition<JsCatalogEntry> callback=new EntryUpdateCallback(context,onDone);

                /*if(e.isClosed()||e.getCallback()==null){
                    sm.create(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(),catalog, entry, callback);
                }else{
                    e.getCallback().hook(new DataCallback<Void>() {

                        @Override
                        public void execute() {
                            sm.create(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(),catalog, entry, callback);
                        }
                    });

                }*/

                    entryCommit= new CatalogActionRequestImpl();

                    entryCommit.setEntryValue(userOutput);
                    entryCommit.setCatalog(catalog);
                    entryCommit.setName(transactionType);

                }
            }
            //no commit required for select
            if(entryCommit!=null){
                entryCommit.setFollowReferences(true);
                userOutput =  context.getRuntimeContext().getServiceBus().fireEvent(entryCommit,context.getRuntimeContext(),null);
            }
            CatalogDescriptor catalogDescriptor = applicationState.getCatalogValue();
            String keyfield = catalogDescriptor.getKeyField();
            applicationState.setEntry(
                    keyServices.encodeClientPrimaryKeyFieldValue(
                            userOutput.getId(),
                            catalogDescriptor.getFieldDescriptor(keyfield),
                            catalogDescriptor

                    )
            );
            applicationState.setEntryValue(userOutput);
        }

        return CONTINUE_PROCESSING;
    }


}
