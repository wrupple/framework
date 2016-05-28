package com.wrupple.muba.bpm.server.service.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.service.CatalogEntryAssembler;
import com.wrupple.muba.catalogs.server.service.impl.SimpleVegetateCatalogDataAceessObject;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.Person;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.server.services.SessionContext;
import com.wrupple.vegetate.shared.services.SignatureGenerator;

public class PersonDao  extends
SimpleVegetateCatalogDataAceessObject<Person>  {

	protected SessionContext session;

	@Inject
	public PersonDao(@Named(Person.CATALOG)CatalogDescriptor personCatalogProvider,ObjectMapper mapper,
			CatalogEntryAssembler entryAssembler,
			@Named("authenticator-host") String host,
			@Named("vegetateCatalogBase") String vegetateUrlBase,
			CatalogServiceManifest serviceManifest,
			
			@Named("authenticator-host") Provider<SignatureGenerator> signatureProvider,SessionContext session) {
		super(mapper, entryAssembler, personCatalogProvider, host, vegetateUrlBase,
				String.valueOf(CatalogEntry.WRUPPLE_ID), serviceManifest,
				signatureProvider);
		this.session=session;
	}
	
	
	
	@Override
	public Person update(Person originalEntry, Person updatedEntry) throws Exception {
		if(session.getStakeHolder()==originalEntry.getId().longValue()){
			return super.update(originalEntry, updatedEntry);
		}else{
			throw new IllegalArgumentException("Cannot change personalities");
		}
	
	}
	
	@Override
	public Person delete(Person o) throws Exception {
		throw new IllegalArgumentException("Cannot delete personalities");
	}
}