package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.WorkCompleteEvent;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.bpm.server.chain.command.ExplicitOutputPlace;
import com.wrupple.muba.event.domain.*;
import org.apache.commons.chain.Context;

public class ExplicitOutputPlaceImpl implements ExplicitOutputPlace {



	@Override
	public boolean execute(Context ctx) throws Exception {
		RuntimeContext context = (RuntimeContext) ctx;
		WorkCompleteEvent event = (WorkCompleteEvent) context.getServiceContract();
		ApplicationState state= (ApplicationState) event.

        Workflow nextItem = findNextTreeNode(event, state);
		state.setHandleValue(nextItem);
        state.setTaskDescriptorValue(null);
        state.setTaskDescriptor(null);

		return CONTINUE_PROCESSING;
	}

	private Workflow findNextTreeNode(WorkCompleteEvent event, ApplicationState currentState ) {
        Workflow currentItem = (Workflow) currentState.getHandleValue();



        /*
        excecution
         */

		if (currentItem.getExplicitSuccessorValue() == null) {
			/*
			 * if activity has not been defined either by properties or command argument:
			 * 
			 * read the value from the output of whateve field that has a foreignKey pointing to Workflow
			 * 
			 */
            CatalogEntry output = getApplicationOutput(currentState);
			if (output != null) {
			    if(isWorkflow(output)){
			        return (Workflow) output;
                }else{
                    /* TODO IF ANY OF THE OUTPUT FIELDS CONTAIN A WORKFLOW USE IT
                    CatalogDescriptor result = currentState.getSolutionDescriptor();

                    Collection<FieldDescriptor> fields = result.getFieldsValues();
                    String applicationItemId = null;
                    for (FieldDescriptor field : fields) {
                        // TODO make field configurable
                        if (field.isKey() && !field.isMultiple() && field.getCatalog() != null
                                && Workflow.CATALOG.equals(field.getCatalog())) {

                            applicationItemId = GWTUtils.getAttribute(output, field.getFieldId());
                            get valuefrom joined data field
                            break;
                        }
                    }
                    */


                }

			}
		} else {
			return currentItem.getExplicitSuccessorValue();
		}
        throw new IllegalArgumentException("unable to determine followup Activiy");
	}

    private boolean isWorkflow(CatalogEntry output) {
	    return output.getCatalogType().equals(Workflow.CATALOG);
    }

    private CatalogEntry getApplicationOutput(ApplicationState currentState) {
	    if(currentState.getEntryValue()==null){
	        if(currentState.getUserSelectionValues()==null||currentState.getUserSelectionValues().isEmpty()){
	            throw new IllegalStateException("unable to determine followup Activiy");
            }else{
                return currentState.getUserSelectionValues().get(0);
            }
        }else{
	        return currentState.getEntryValue();
        }
    }


}
