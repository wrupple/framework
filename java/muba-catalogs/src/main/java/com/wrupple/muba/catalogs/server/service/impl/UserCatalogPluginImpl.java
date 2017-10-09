package com.wrupple.muba.catalogs.server.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.inject.Singleton;

import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.domain.CatalogIdentificationImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogException;
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
				names.add(new CatalogIdentificationImpl(desc.getDistinguishedName(), desc.getName(),(String) cctxontext.getCatalogManager().encodeClientPrimaryKeyFieldValue(desc.getImage(), null, null) ));
			}
		}
	}
	

	private List<CatalogDescriptor> readCatalogs(CatalogActionContext context, FilterData filter,boolean assemble) throws CatalogException{

        List<CatalogDescriptor> r= null;
        try {
            r = context.triggerRead(CatalogDescriptor.CATALOG_ID,filter,assemble);
        } catch (Exception e) {
            throw new CatalogException("failed to read catalogs",e);
        }
        return r;

	}
	
	private CatalogDescriptor readCatalog(CatalogActionContext context, Long key,boolean assemble) throws CatalogException{
		if(key.longValue()<0){
			throw new IllegalArgumentException("No negative keys allowed for user defined catalogs: "+key);
		}

		try {
			return context.triggerGet(CatalogDescriptor.CATALOG_ID,key,assemble);
		} catch (Exception e) {
			throw new CatalogException("Unable to read catalog:"+key,e);

		}

	}


	@Override
	public CatalogDescriptor getDescriptorForName(String catalogId, CatalogActionContext context) throws RuntimeException {
		FilterData filter=FilterDataUtils.createSingleFieldFilter(HasDistinguishedName.FIELD, catalogId);
		
		List<CatalogDescriptor> descs = readCatalogs(context,filter,true);
		if(descs==null){
			log.debug("Useless search for {}",catalogId);
			return null;
		}
		CatalogDescriptor raw = descs.get(0);
		
		return raw;
	}
	
	@Override
	public CatalogDescriptor getDescriptorForKey(Long key, CatalogActionContext context) throws RuntimeException {
		CatalogDescriptor raw = (CatalogDescriptor) readCatalog(context, key, true);
		
		return raw;
	}

	@Override
	public ValidationExpression[] getValidations() {
		return null;
	}

	@Override
	public Command[] getCatalogActions() {
		//
		return null;
	}

	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor c, CatalogActionContext context) {

	}


	

}
