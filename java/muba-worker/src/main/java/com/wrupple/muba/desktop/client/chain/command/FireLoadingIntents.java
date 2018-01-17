package com.wrupple.muba.desktop.client.chain.command;

import org.apache.commons.chain.Command;

public interface FireLoadingIntents extends Command {

       /*  TODO form / file POST submission?
             * all forms that use a context.getSubmitUrl() should append a hidden field setRuntimeContext the id of the submitting task.  if this field is present we know we have a form submission in our hands
			 *
			 * in that case the use output context variable is constituted by the resulting entrie(s) of committing the submitting task
			 *
			 * see how to handle different submissions cause redirecting is a posibility, se how submitUrl is determined to also know how to handle sumissions
			 *

			also userOutputVariable should be used, since client uses that variable to deduce what item to show in read transactions
			El cliente debería de poder determinar el estado en el que se encuentra una petición únicamente por la URL

			reader should check for submitts before wasting any more resources and perform
			redirects filling out apropiate url tokens (at least entry and ?catalog? according to task configuration if necesary
			*/

}
