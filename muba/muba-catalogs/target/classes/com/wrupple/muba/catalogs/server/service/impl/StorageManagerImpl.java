package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;
import org.apache.commons.chain.impl.ChainBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadUrlHandlerTransaction;
import com.wrupple.muba.catalogs.server.chain.command.ValidateUserData;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.server.chain.CatalogManager;
import com.wrupple.vegetate.server.chain.command.CatalogCreateTransaction;
import com.wrupple.vegetate.server.chain.command.CatalogDeleteTransaction;
import com.wrupple.vegetate.server.chain.command.CatalogReadTransaction;
import com.wrupple.vegetate.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.vegetate.server.chain.command.impl.VegetateServiceImpl;
import com.wrupple.vegetate.server.domain.CatalogExcecutionContextImpl;
import com.wrupple.vegetate.server.domain.DomainContext;
import com.wrupple.vegetate.server.services.PrimaryKeyEncodingService;
import com.wrupple.vegetate.server.services.RequestScopedContext;

@Singleton
public class StorageManagerImpl extends CatalogBase implements CatalogManager {

	private static final Logger log = LoggerFactory.getLogger(StorageManagerImpl.class);


	private final Command create;

	private final Command read;

	private final Command write;

	private final Command delete;
	
	private final PrimaryKeyEncodingService keyEncodingService;

	private final Provider<DomainContext> domainContextProvider;
	private final Provider<RequestScopedContext> requextContextp;
	
	
	@Inject
	public StorageManagerImpl(PrimaryKeyEncodingService keyEncodingService, Provider<RequestScopedContext> requextContextp,Provider<DomainContext> domainContextProvider, CatalogCreateTransaction create, CatalogReadTransaction read, CatalogUpdateTransaction write,
			CatalogDeleteTransaction delete, CatalogFileUploadTransaction upload, CatalogFileUploadUrlHandlerTransaction url,ValidateUserData validate) {
		super();
		this.keyEncodingService=keyEncodingService;
		this.create = new ChainBase(new Command[]{validate,create});
		this.read = new ChainBase(new Command[]{validate,read});
		this.write = new ChainBase(new Command[]{validate,write});
		this.delete = new ChainBase(new Command[]{validate,delete});
		this.requextContextp=requextContextp;
		this.domainContextProvider=domainContextProvider;
		super.addCommand(CatalogActionRequest.CREATE_ACTION, this.create);
		super.addCommand(CatalogActionRequest.READ_ACTION, this.read);
		super.addCommand(CatalogActionRequest.WRITE_ACTION, this.write);
		super.addCommand(CatalogActionRequest.DELETE_ACTION, this.delete);
		super.addCommand(CatalogActionRequest.UPLOAD_ACTION, upload);
		super.addCommand("url", url);
	}

	@Override
	public CatalogExcecutionContext spawn(CatalogExcecutionContext parent) {
		// FIXME domain must initially be session stakeholder default, or
		// current choice of domain unless other domain is later specified
		CatalogExcecutionContext regreso = new CatalogExcecutionContextImpl(parent==null?domainContextProvider.get():parent.getDomainContext(), requextContextp.get(), parent);
		log.debug("[SPAWN ] {}",regreso);
		return regreso;
	}

	@Override
	public Command getRead() {
		return read;
	}
	@Override
	public Command getWrite() {
		return write;
	}
	@Override
	public Command getDelete() {
		return delete;
	}

	@Override
	public Command getNew() {
		return create;
	}

	@Override
	public PrimaryKeyEncodingService getKeyEncodingService() {
		return keyEncodingService;
	}

}
