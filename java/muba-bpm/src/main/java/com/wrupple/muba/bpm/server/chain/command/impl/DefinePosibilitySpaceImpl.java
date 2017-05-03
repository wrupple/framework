package com.wrupple.muba.bpm.server.chain.command.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bpm.server.domain.ContentContext;
import com.wrupple.muba.bpm.server.domain.ContentContext.PossibleValue;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

@Singleton
public class DefinePosibilitySpaceImpl  implements Command {


	@Override
	public boolean execute(Context c) throws Exception {
		ContentContext context= (ContentContext) c;
		FilterData filter = context.getFilter();
		FieldDescriptor field = context.getField();
		String catalog = field.getCatalog();
		CatalogActionContext read = context.getCatalogContext();
		read.setFilter(filter);
		read.setCatalog(catalog);
		read.setEntry(null);
		
		read.getCatalogManager().getRead().execute(read);
		List<CatalogEntry> results = read.getResults();
		
		if(results==null || results.isEmpty()){
			
		}else{
			
			List<PossibleValue> foundValues = new ArrayList<ContentContext.PossibleValue>(results.size());
			for(CatalogEntry e: results){
				foundValues.add(new PossibleValue(e, 0));
			}
			context.setFoundValues(foundValues);
		}
		
		return CONTINUE_PROCESSING;
	}


}
