package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.bpm.server.chain.command.CommitSubmission;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;

import javax.inject.Inject;

/**
 * Created by japi on 18/08/17.
 */
public class CommitSubmissionImpl implements CommitSubmission {

    private final FieldAccessStrategy aceess;

    @Inject
    public CommitSubmissionImpl(FieldAccessStrategy aceess) {
        this.aceess = aceess;
    }


    @Override
    public boolean execute(Context ctx) throws Exception {

        ApplicationContext context = (ApplicationContext) ctx;
        BusinessIntent contractExplicitIntent = (BusinessIntent) context.getRuntimeContext().getServiceContract();
        ApplicationContext applicationState = context.getRuntimeContext().getConvertedResult();
        ProcessTaskDescriptor task = applicationState.getStateValue().getTaskDescriptorValue();
        String producedField = task.getOutputField();

        //is there submission material?
        CatalogEntry userOutput = applicationState.getStateValue().getEntryValue();
        //what task
        //what action
        //AQUI VA LO BUENO
        Instrospection instrospection = aceess.newSession(applicationState.getStateValue());

        //conditions from GWT desktop ( CommitEditTransaction CommitSelectTransaction )... do commit

        //FIXME this is handled on the client when an application State is updated
        if (producedField != null) {
            //TODO certain saveTo fields are reserved, like those in CatalogAction
            //GWTUtils.setAttribute(contextP, saveTo, userOutput);
            aceess.setPropertyValue(producedField,applicationState.getStateValue(),userOutput.getId(), instrospection);

        }



        CatalogEntry entry = applicationState.getStateValue().getEntryValue();
        String transactionType = applicationState.getStateValue().getTaskDescriptorValue().getTransactionType();
        final String catalog = (String) applicationState.getStateValue().getTaskDescriptorValue().getCatalog();

        boolean canceled = applicationState.getStateValue().isCanceled();
        Object id =applicationState.getStateValue().getEntry();
        if(id ==null){
            id = entry.getId();
        }
        CatalogActionRequestImpl entryCommit = null;
        if(CatalogActionRequest.READ_ACTION.equals(transactionType)){
           // onDone.setResultAndFinish(context);
        }else  if(CatalogActionRequest.WRITE_ACTION.equals(transactionType)){
			/*
			 * THIS TRANSACTION HAS SOME SORT OF USER OUTPUT THAT MUST BE COMMITED
			 */

            if(entry==null){
                //no user output, most likely canceled

            }else{

                /*boolean draft = entry.isDraft();
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

                entryCommit.setEntryValue(entry);
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

                entryCommit.setEntryValue(entry);
                entryCommit.setCatalog(catalog);
                entryCommit.setName(transactionType);

            }
        }
        //no commit required for select
        entryCommit.setFollowReferences(true);

        entry = context.getRuntimeContext().getEventBus().fireEvent(entryCommit,context.getRuntimeContext(),null);


        applicationState.getStateValue().setEntryValue(entry);
        return CONTINUE_PROCESSING;
    }


}
