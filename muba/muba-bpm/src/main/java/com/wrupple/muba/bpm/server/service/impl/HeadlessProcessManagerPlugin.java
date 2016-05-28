package com.wrupple.muba.bpm.server.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.bpm.domain.BusinessEvent;
import com.wrupple.muba.bpm.domain.ExplicitEventSuscription;
import com.wrupple.muba.bpm.domain.Notification;
import com.wrupple.muba.bpm.domain.ProcessDescriptor;
import com.wrupple.muba.bpm.server.chain.command.BPMStakeHolderTrigger;
import com.wrupple.muba.bpm.server.chain.command.BPMValidationTrigger;
import com.wrupple.muba.bpm.server.chain.command.BPMValueChangeListener;
import com.wrupple.muba.bpm.server.service.ProcessManagerPlugin;
import com.wrupple.muba.catalogs.domain.CatalogActionTriggerImpl;
import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.domain.CatalogIdentificationImpl;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.HasStakeHolder;
import com.wrupple.vegetate.domain.Person;
import com.wrupple.vegetate.domain.VegetateAuthenticationToken;
import com.wrupple.vegetate.server.domain.ValidationExpression;
import com.wrupple.vegetate.server.domain.VegetateAuthenticationTokenDescriptor;
import com.wrupple.vegetate.server.services.CatalogDescriptorBuilder;

@Singleton
public class HeadlessProcessManagerPlugin  implements ProcessManagerPlugin {

	private final Provider<CatalogDescriptor> processDescriptorProvider;
	private Provider<CatalogDescriptor> notificationProvider;
	private Provider<CatalogDescriptor> clientProvider;
	
	private Provider<VegetateAuthenticationTokenDescriptor> authTokenDescriptor;
	private final CatalogDescriptorBuilder builder;
	private Provider<BusinessEvent> bep;

	@Inject
	public HeadlessProcessManagerPlugin(Provider<BusinessEvent> bep, CatalogDescriptorBuilder builder, BPMValueChangeListener changeListener,
			BPMValidationTrigger validationTrigger, BPMStakeHolderTrigger stakeHolderTrigger, CatalogManager transactions,
			Provider<VegetateAuthenticationTokenDescriptor> authTokenDescriptor, 
			@Named(ProcessDescriptor.CATALOG) Provider<CatalogDescriptor> processDescriptorProvider,
			@Named(Notification.CATALOG) Provider<CatalogDescriptor> notificationProvider,
			@Named(BPMPeer.CATALOG) Provider<CatalogDescriptor> clientProvider ) {
		this.processDescriptorProvider = processDescriptorProvider;
		this.notificationProvider = notificationProvider;
		this.clientProvider = clientProvider;
		this.authTokenDescriptor = authTokenDescriptor;
		this.builder = builder;
		this.bep = bep;
		transactions.addCommand(BPMValidationTrigger.class.getSimpleName(), validationTrigger);
		transactions.addCommand(BPMValueChangeListener.class.getSimpleName(), changeListener);
		transactions.addCommand(BPMStakeHolderTrigger.class.getSimpleName(), stakeHolderTrigger);
	}

	@Override
	public void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogExcecutionContext context) {

		names.add(new CatalogIdentificationImpl(ExplicitEventSuscription.CATALOG, ExplicitEventSuscription.CATALOG, "/static/img/notification.png"));
		names.add(new CatalogIdentificationImpl(BPMPeer.CATALOG, "Open Sessions", "/static/img/session.png"));
		names.add(new CatalogIdentificationImpl(ProcessDescriptor.CATALOG, "Process", "/static/img/process.png"));
		names.add(new CatalogIdentificationImpl(Notification.CATALOG, Notification.CATALOG, "/static/img/notification.png"));
		//organization catalog as visible?
	}

	@Override
	public CatalogDescriptor getDescriptorForName(String catalogId, Long domain) throws Exception {
		if (ProcessDescriptor.CATALOG.equals(catalogId)) {
			return processDescriptorProvider.get();
		} else if (Notification.CATALOG.equals(catalogId)) {
			return notificationProvider.get();
		} else if (BPMPeer.CATALOG.equals(catalogId)) {
			return clientProvider.get();
		} else if (VegetateAuthenticationToken.CATALOG.equals(catalogId)) {
			return authTokenDescriptor.get();
		} else if (BusinessEvent.CATALOG.equals(catalogId)) {
			return builder.fromClass(bep.get().getClass(), BusinessEvent.CATALOG, "Business Event", -2958309327l);
		}
		return null;
	}


	@Override
	public CatalogDataAccessObject<? extends CatalogEntry> getOrAssembleDataSource(CatalogDescriptor catalog, Class<? extends CatalogEntry> clazz,
			CatalogExcecutionContext context) throws Exception {
		return null;
	}

	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor catalog) {
		FieldDescriptor stakeHolderField = catalog.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
		CatalogActionTriggerImpl e;
		if (stakeHolderField != null && !stakeHolderField.isMultiple() && stakeHolderField.getDataType() == CatalogEntry.INTEGER_DATA_TYPE
				&& Person.CATALOG.equals(stakeHolderField.getForeignCatalogName())) {

			stakeHolderField.setWriteable(false);

			e = new CatalogActionTriggerImpl();
			e.setAction(0);
			e.setBefore(true);
			e.setHandler(3);
			e.setRollbackOnFail(true);
			e.setStopOnFail(true);
			e.linkToChain(BPMStakeHolderTrigger.class.getSimpleName());
			catalog.getTriggersValues().add(e);
			//asi como los triggers se agregan, modificadores de permisos especificos, alteran los filtros o la visibilidad de una entrada
			//((CatalogDescriptor) catalog).setStakeHolderProtected(true);

			e = new CatalogActionTriggerImpl();
			e.setAction(1);
			e.setBefore(true);
			e.setHandler(3);
			e.setRollbackOnFail(true);
			e.setStopOnFail(true);
			e.linkToChain(BPMValidationTrigger.class.getSimpleName());
			catalog.getTriggersValues().add(e);

			e = new CatalogActionTriggerImpl();
			e.setAction(1);
			e.setBefore(false);
			e.setHandler(3);
			e.setRollbackOnFail(true);
			e.setStopOnFail(true);
			e.linkToChain(BPMValueChangeListener.class.getSimpleName());
			catalog.getTriggersValues().add(e);

		}

	}

	@Override
	public void invalidateCache(String o) {

	}


	
	@Override
	public CatalogDescriptor loadFromCache(String host, String domain, String catalog) {
		throw new UnsupportedOperationException("this is only supported client side");
	}

	@Override
	public ValidationExpression[] getValidations() {
		return null;
	}


}
