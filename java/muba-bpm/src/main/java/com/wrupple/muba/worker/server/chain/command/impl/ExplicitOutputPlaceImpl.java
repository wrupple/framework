package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Workflow;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.worker.server.chain.command.ExplicitOutputPlace;
import org.apache.commons.chain.Context;

public class ExplicitOutputPlaceImpl implements ExplicitOutputPlace {



	@Override
	public boolean execute(Context ctx) throws Exception {
		ApplicationContext context = (ApplicationContext) ctx;
		ApplicationState state=  context.getStateValue();

		Application nextItem = findNextTreeNode(state);
		state.setApplicationValue(nextItem);
        state.setTaskDescriptorValue(null);
        state.setTaskDescriptor(null);

		return CONTINUE_PROCESSING;
	}

	private Application findNextTreeNode(ApplicationState currentState) {
		Application currentItem = (Application) currentState.getApplicationValue();



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
					return (Application) output;
				}else{
                    /* TODO IF ANY OF THE OUTPUT FIELDS CONTAIN A WORKFLOW USE IT
					CatalogDescriptor result = currentState.getCatalogValue();

                    Collection<FieldDescriptor> fields = result.getFieldsValues();
                    String applicationItemId = null;
                    for (FieldDescriptor field : fields) {
                        // TODO make field configurable
                        if (field.isKey() && !field.isMultiple() && field.getCatalog() != null
                                && Workflow.WORKFLOW_CATALOG.equals(field.getCatalog())) {

                            applicationItemId = GWTUtils.getAttribute(output, field.getDistinguishedName());
                            get valuefrom joined data field
                            break;
                        }
                    }
                    */


                }

			}
		} else {
			return (Application) currentItem.getExplicitSuccessorValue();
		}
        throw new IllegalArgumentException("unable to determine followup Activiy");
	}

    private boolean isWorkflow(CatalogEntry output) {
		return output.getCatalogType().equals(Application.CATALOG);
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
