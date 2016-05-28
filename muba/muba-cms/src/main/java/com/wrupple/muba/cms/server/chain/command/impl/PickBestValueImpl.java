package com.wrupple.muba.cms.server.chain.command.impl;

import java.util.List;

import org.apache.commons.chain.Context;

import com.wrupple.muba.cms.server.chain.command.PickBestValue;
import com.wrupple.muba.cms.server.domain.ContentContext;
import com.wrupple.muba.cms.server.domain.ContentContext.FoundValue;

public class PickBestValueImpl implements PickBestValue {

	@Override
	public boolean execute(Context c) throws Exception {
		ContentContext context = (ContentContext) c;
		List<FoundValue> found = context.getFoundValues();
		context.setFieldValue(null);
		if(found==null||found.isEmpty()){
			
		}else{
			FoundValue moreLikely=found.get(0);
			double leastError=moreLikely.getError();
			for(FoundValue posible: found){
				if(posible.getError()<leastError){
					 moreLikely=posible;
					leastError=posible.getError();
				}
			}
			Object value =moreLikely.getValue();
			context.setFieldValue(value);
			
		}
		return CONTINUE_PROCESSING;
	}

}
