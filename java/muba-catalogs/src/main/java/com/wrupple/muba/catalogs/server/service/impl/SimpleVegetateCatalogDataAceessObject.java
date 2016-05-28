package com.wrupple.muba.catalogs.server.service.impl;

import java.util.List;

import javax.inject.Provider;

import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.domain.VegetateColumnResultSet;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogActionResultImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogEntryAssembler;
import com.wrupple.muba.catalogs.server.service.CatalogVegetateChannel;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.domain.FilterDataImpl;
import com.wrupple.vegetate.server.services.ErrorAccuser;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.shared.services.SignatureGenerator;

/**
 * Target domain is set at construction time
 * 
 * Authentication is always done by signing individual requests
 * 
 * @author japi
 *
 * @param <T>
 */
public class SimpleVegetateCatalogDataAceessObject<T extends CatalogEntry> extends AbstractUndoHistoryTransactionalDAO<T> implements CatalogDataAccessObject<T> {

	private final CatalogEntryAssembler entryAssembler; 

	private CatalogDescriptor catalogdescriptor;
	protected final String domain;

	private final CatalogVegetateChannel channel;

	// sets all configuration data, except for permissions which the Data Store
	// Manager handles
	public SimpleVegetateCatalogDataAceessObject(ObjectMapper mapper, CatalogEntryAssembler entryAssembler,CatalogDescriptor catalogdescriptor,  String host,
			String vegetateUrlBase, String targetDomain, CatalogServiceManifest serviceManifest,Provider<? extends SignatureGenerator> signatureGeneratorProvider) {
		super();
		this.entryAssembler = entryAssembler;
		this.domain = targetDomain;
		this.channel = new CatalogVegetateChannel(host, vegetateUrlBase, mapper, serviceManifest,signatureGeneratorProvider);
		this.catalogdescriptor=catalogdescriptor;
	}


	public String getDomain() {
		return domain;
	}


	@Override
	public T read(Object targetEntryId) throws Exception {
		String catalog = catalogdescriptor.getCatalogId();
		CatalogActionRequestImpl request = new CatalogActionRequestImpl(domain, catalog, CatalogActionRequest.READ_ACTION, targetEntryId==null?null: getContext().getRequest().getStorageManager().getKeyEncodingService().encodeClientPrimaryKeyFieldValue(targetEntryId, this.catalogdescriptor.getFieldDescriptor(CatalogEntry.ID_FIELD), catalogdescriptor).toString(), null, null, null);

		CatalogActionResultImpl response = channel.send(request);

		return processSingleResponse(response);
	}

	@Override
	public List<T> read(FilterData filterData) throws Exception {
		String catalog = catalogdescriptor.getCatalogId();
		CatalogActionRequestImpl request = new CatalogActionRequestImpl(domain, catalog, CatalogActionRequest.READ_ACTION, null, null, null,
				(FilterDataImpl) filterData);

		CatalogActionResultImpl response = channel.send(request);

		return processMultipleResponse(response);
	}

	@Override
	public T update(T originalEntry, T updatedEntry) throws Exception {
		String catalog = catalogdescriptor.getCatalogId();
		String entry = originalEntry.getIdAsString();
		CatalogActionRequestImpl request = new CatalogActionRequestImpl(domain, catalog, CatalogActionRequest.WRITE_ACTION, entry, null, updatedEntry, null);

		CatalogActionResultImpl response = channel.send(request);
		return processSingleResponse(response);
	}

	@Override
	public T create(T o) throws Exception {
		String catalog = catalogdescriptor.getCatalogId();
		CatalogActionRequestImpl request = new CatalogActionRequestImpl(domain, catalog, CatalogActionRequest.CREATE_ACTION, null, null, o, null);

		CatalogActionResultImpl response = channel.send(request);
		return processSingleResponse(response);
	}

	@Override
	public T delete(T o) throws Exception {
		String catalog = catalogdescriptor.getCatalogId();
		String entry = o.getIdAsString();
		CatalogActionRequestImpl request = new CatalogActionRequestImpl(domain, catalog, CatalogActionRequest.DELETE_ACTION, entry, null, null, null);

		CatalogActionResultImpl response = channel.send(request);
		return processSingleResponse(response);
	}

	public <V extends CatalogEntry> CatalogDataAccessObject<V> cast(Class<V> clazz, CatalogDescriptor catalog) {
		this.catalogdescriptor = catalog;
		return (CatalogDataAccessObject<V>) this;
	}

	public CatalogDescriptor getCatalogdescriptor() {
		return catalogdescriptor;
	}

	public void setCatalogdescriptor(CatalogDescriptor catalogdescriptor) {
		this.catalogdescriptor = catalogdescriptor;
	}


	private List<T> processMultipleResponse(CatalogActionResultImpl response) throws Exception {
		List<VegetateColumnResultSet> resultSets = response.getResponse();
		if (resultSets == null || resultSets.isEmpty()) {
			return null;
		} else {
			// TODO process joined result sets
			VegetateColumnResultSet mainResultSet = resultSets.get(0);
			List<T> regreso = (List<T>) entryAssembler.processResultSet(mainResultSet, catalogdescriptor);

			return regreso;
		}
	}

	private T processSingleResponse(CatalogActionResultImpl response) throws Exception {
		List<VegetateColumnResultSet> resultSets = response.getResponse();
		if (resultSets == null || resultSets.isEmpty()) {
			return null;
		} else {
			// TODO process joined result sets
			VegetateColumnResultSet mainResultSet = resultSets.get(0);
			List<T> regreso = (List<T>) entryAssembler.processResultSet(mainResultSet, catalogdescriptor);

			if (regreso == null || regreso.isEmpty()) {
				return null;
			} else {
				return regreso.get(0);
			}
		}
	}

	public CatalogDescriptor getCatalog() {
		return catalogdescriptor;
	}


	public void setAccuser(ErrorAccuser accuser) {
		this.channel.setAccuser(accuser);
	}

	public ErrorAccuser getAccuser(){
		return this.channel.getAccuser();
	}



}
