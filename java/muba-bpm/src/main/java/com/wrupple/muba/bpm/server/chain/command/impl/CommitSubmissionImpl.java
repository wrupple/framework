package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.BusinessEvent;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.server.chain.command.CommitSubmission;
import com.wrupple.muba.bpm.server.domain.BusinessContext;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
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
        BusinessContext context = (BusinessContext) ctx;
        BusinessEvent contractExplicitIntent = (BusinessEvent) context.getRuntimeContext().getServiceContract();
        ApplicationState applicationState = context.getRuntimeContext().getConvertedResult();
        ProcessTaskDescriptor task = applicationState.getTaskDescriptorValue();
        String producedField = task.getOutputField();

        //is there submission material?
        CatalogEntry userOutput = applicationState.getEntryValue();
        //what task
        //what action
        //conditions from GWT desktop (AbstractCommitUserTransactionImpl CommitEditTransaction CommitSelectTransaction)... do commit
        //AQUI VA LO BUENO
        FieldAccessStrategy.Session session = aceess.newSession(applicationState);



        if (producedField != null) {
            //TODO certain saveTo fields are reserved, like those in CatalogAction

            //GWTUtils.setAttribute(contextP, saveTo, userOutput);
            aceess.setPropertyValue(producedField,applicationState,userOutput.getId(),session);
            if (userOutput == null) {
                /*
                 *
                  *  esta llamad en realidad solo tiene sentido en el cliente (paquete runner human), pero esta logica no se debe perder, y debe realizarse aqui mismo
                 *
                 * on ApplicationState Update Catalog Event
                 *
                 * */

                context.getHumanSolver().putPlaceParameter(saveTo, null);
            }else{
                String unencodedString;
                if (GWTUtils.isArray(userOutput)) {

                    JsArray<JsCatalogEntry> out = userOutput.cast();
                    if (out.length() == 0) {
                        unencodedString = null;
                    } else {
                        StringBuilder builder = new StringBuilder(out.length()*20);
                        for (int i = 0; i < out.length(); i++) {
                            if (i > 0) {
                                builder.append(',');
                            }
                            builder.append(out.get(i).getId());

                        }
                        unencodedString = builder.toString();
                    }

                } else {
                    unencodedString = userOutput.getId();
                }
                context.getDesktopManager().putPlaceParameter(saveTo, unencodedString);
            }

        }


        return CONTINUE_PROCESSING;
    }
}
