package com.wrupple.muba.catalogs.server.service.impl;

import java.util.List;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.domain.CatalogIdentificationImpl;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import com.wrupple.muba.catalogs.server.service.UserCatalogPlugin;
@Singleton
public class UserCatalogPluginImpl implements UserCatalogPlugin {
	protected static final Logger log = LoggerFactory.getLogger(UserCatalogPluginImpl.class);

	@Override
	public void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogActionContext cctxontext)
			throws Exception {
		FilterData filter=FilterDataUtils.newFilterData();
		filter.setConstrained(false);
		
		List<CatalogDescriptor> descs = readCatalogs(cctxontext,filter,false);
		if(descs==null){
			log.warn("System appears to be unconfigured");
		}else{
			for(CatalogDescriptor desc :  descs){
				names.add(new CatalogIdentificationImpl(desc.getCatalog(), desc.getName(),(String) cctxontext.getCatalogManager().getKeyEncodingService().encodeClientPrimaryKeyFieldValue(desc.getImage(), null, null) ));
			}
		}
	}
	

	private List<CatalogDescriptor> readCatalogs(CatalogActionContext cctxontext, FilterData filter,boolean assemble) throws Exception {

		CatalogActionContext context = cctxontext.getCatalogManager().spawn(cctxontext);
		context.setCatalog(CatalogDescriptor.CATALOG_ID);
		context.setFilter(filter);
		if(assemble){
			context.put(CatalogReadTransaction.READ_GRAPH, assemble);
		}
		context.getCatalogManager().getRead().execute(context);
		return  context.getResults();
	}
	
	private CatalogDescriptor readCatalog(CatalogActionContext cctxontext, Long key,boolean assemble) throws Exception {

		CatalogActionContext context = cctxontext.getCatalogManager().spawn(cctxontext);
		context.setCatalog(CatalogDescriptor.CATALOG_ID);
		context.setEntry(key);
		if(assemble){
			context.put(CatalogReadTransaction.READ_GRAPH, assemble);
		}
		context.getCatalogManager().getRead().execute(context);
		return  context.getResult();
	}


	@Override
	public CatalogDescriptor getDescriptorForName(String catalogId, CatalogActionContext context) throws Exception {
		FilterData filter=FilterDataUtils.createSingleFieldFilter(CatalogDescriptor.CATALOG_FIELD, catalogId);
		
		List<CatalogDescriptor> descs = readCatalogs(context,filter,true);
		if(descs==null){
			log.debug("Useless search for {}",catalogId);
			return null;
		}
		CatalogDescriptor raw = descs.get(0);
		
		return raw;
	}
	
	@Override
	public CatalogDescriptor getDescriptorForKey(Long key, CatalogActionContext context) throws Exception {
		CatalogDescriptor raw = (CatalogDescriptor) readCatalog(context, key, true);
		
		return raw;
	}

	@Override
	public ValidationExpression[] getValidations() {
		return null;
	}

	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor c) {

	}


	

}