package com.wrupple.muba.bootstrap.server.chain.command.impl;

import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.ContractDescriptor;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;

public abstract class PathParsingCommand implements Command {

	@Override
	public boolean execute(Context ctx) throws Exception {
		ExcecutionContext requestContext = (ExcecutionContext) ctx;
		Context context = createBlankContext(requestContext);
		Object contract = requestContext.getServiceContract();
		if (contract != null) {
			ContractDescriptor descriptor = requestContext.getServiceManifest().getContractDescriptor();
			if (descriptor != null) {
				Collection<String> fields = descriptor.getFields();
				Object value;
				for (String field : fields) {
					value = PropertyUtils.getProperty(contract, field);
					if (value != null) {
						context.put(field, value);
					}
				}
			}

		}
		
		if (requestContext.hasNext()) {
			String[] tokens =  requestContext.getServiceManifest().getGrammar();
			String key, value;
			for (int i = 0; i < tokens.length; i++) {
				if (requestContext.hasNext()) {
					key = tokens[i];
					value = requestContext.next();
					context.put(key, value);
				} else {
					break;
				}

			}
		}
		requestContext.setServiceContext(context);
		return CONTINUE_PROCESSING;
	}

	protected abstract Context createBlankContext(ExcecutionContext requestContext) ;

}
