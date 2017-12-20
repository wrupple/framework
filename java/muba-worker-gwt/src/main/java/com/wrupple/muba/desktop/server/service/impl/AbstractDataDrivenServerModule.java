package com.wrupple.muba.desktop.server.service.impl;

import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.bpm.server.service.BPMPeerErrorAccuser;
import com.wrupple.muba.catalogs.domain.ActivityDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.*;
import com.wrupple.muba.catalogs.server.service.impl.CatalogDaoFactoryImpl;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.catalogs.server.service.impl.SimpleVegetateCatalogDataAceessObject;
import com.wrupple.muba.cms.server.services.DataDrivenServerModule;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.domain.AnonymouslyVisibleField;
import com.wrupple.vegetate.server.domain.CatalogDescriptorImpl;
import com.wrupple.vegetate.server.domain.NameField;
import com.wrupple.vegetate.server.domain.PrimaryKeyField;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.shared.services.SignatureGenerator;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

public abstract class AbstractDataDrivenServerModule extends CatalogDaoFactoryImpl implements DataDrivenServerModule {

	protected final Provider<JSRAnnotationsDictionary> validationDictionary;
	private PrimaryKeyField primaryKeyField;
	private NameField nameField;
	private AnonymouslyVisibleField publicField;
	protected final ObjectMapper mapper;
	protected final CatalogEntryAssembler entryAssembler;
	protected final Provider<DataStoreManager> dsmp;
	protected final CatalogServiceManifest catalogManifest;
	protected final Provider<BPMPeerErrorAccuser> errorAccuser;
	public static final String CATALOG_CHANNEL_ID = "vegetate/catalog";

	@Inject
	public AbstractDataDrivenServerModule(CatalogServiceManifest catalogManifest, PrimaryKeyField id, NameField name, AnonymouslyVisibleField publicField,
			Provider<JSRAnnotationsDictionary> validationDictionary, Provider<CatalogEntryBeanDAO> localDatasourceProvider,
			Provider<PersistentCatalogEntityDAO> multitenantDataSourceProvider, Provider<DataStoreManager> dsmp, ObjectMapper mapper,
			CatalogEntryAssembler entryAssembler, Provider<BPMPeerErrorAccuser> errorAccuser) {
		super(multitenantDataSourceProvider, localDatasourceProvider);
		this.catalogManifest = catalogManifest;
		this.primaryKeyField = id;
		this.nameField = name;
		this.publicField = publicField;
		this.validationDictionary = validationDictionary;
		this.dsmp = dsmp;
		this.mapper = mapper;
		this.entryAssembler = entryAssembler;
		this.errorAccuser = errorAccuser;
	}

	@Override
	public void writeItems(ActivityDescriptor domainRoot) {

	}

	@Override
	public void invalidateCache(String o) {

	}

	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor c) {
		CatalogDescriptorImpl cc = (CatalogDescriptorImpl) c;
		if (c.getFieldDescriptor(CatalogEntry.ID_FIELD) == null) {
			cc.putField(this.primaryKeyField);
		}
		if (c.getDescriptiveField() == null) {
			c.setDescriptiveField(CatalogEntry.NAME_FIELD);
		}
		if (c.getFieldDescriptor(CatalogEntry.NAME_FIELD) == null) {
			cc.putField(this.nameField);
		}
		if (c.getFieldDescriptor(CatalogEntry.PUBLIC) == null) {
			cc.putField(publicField);
		}
		if (c.getKeyField() == null) {
			c.setKeyField(CatalogEntry.ID_FIELD);
		}
	}

	@Override
	public void writeClientContext(SharedContextWriter contextWriter) throws Exception {
		validationDictionary.get().writeClientContext(contextWriter);
	}

	@Override
	public CatalogDescriptor getDescriptorForName(String name, Long domain) throws Exception {

		CatalogDescriptor regreso = getFirstResultDescriptor(name, domain);
		return regreso;
	}

	@Override
	public CatalogDataAccessObject<? extends CatalogEntry> getOrAssembleDataSource(CatalogDescriptor catalog, Class<? extends CatalogEntry> clazz,
			CatalogExcecutionContext context) throws Exception {
		CatalogDataAccessObject<? extends CatalogEntry> regreso;
		if (catalog.getHost() != null) {
			CatalogDataAccessObject<BPMPeer> peerDao = dsmp.get().getOrAssembleDataSource(BPMPeer.CATALOG, context, BPMPeer.class);
			FilterData hostFilter = FilterDataUtils.createSingleFieldFilter("host", catalog.getHost());
			List<BPMPeer> catalogPeers = peerDao.read(hostFilter);
			BPMPeer catalogPeer = catalogPeers.get(0);

			String signerDomain = catalogPeer.getCatalogDomain();

			if (signerDomain == null) {
				throw new IllegalArgumentException("Catalog host does not provide acces to a domain");
			}
			Provider<SignatureGenerator> signatureProvider = getSignatureGeneratorProvider(catalogPeer.getPrivateKey(), catalogPeer.getPrivateKey());
			BPMPeerErrorAccuser accuser = errorAccuser.get();
			accuser.setPeer(catalogPeer);
			SimpleVegetateCatalogDataAceessObject<? extends CatalogEntry> dataSource = new SimpleVegetateCatalogDataAceessObject(mapper, entryAssembler,
					catalog, catalogPeer.getHost(), catalogPeer.getUrlBase(), signerDomain, catalogManifest, signatureProvider);
			dataSource.setAccuser(accuser);
			return dataSource;
		} else {
			regreso = super.getOrAssembleDataSource(catalog, clazz, context);
		}
		return regreso;
	}

	protected abstract CatalogDescriptor getFirstResultDescriptor(String name, Long domain) throws Exception;

	protected abstract Provider<SignatureGenerator> getSignatureGeneratorProvider(String publicKey, String privateKey);

}
