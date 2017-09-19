package com.wrupple.muba.bpm.server.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.domain.Person;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.server.chain.command.StakeHolderTrigger;
import com.wrupple.muba.bpm.server.chain.command.ValueChangeAudit;
import com.wrupple.muba.bpm.server.chain.command.ValueChangeListener;
import com.wrupple.muba.bpm.server.chain.command.CheckSecureConditions;
import com.wrupple.muba.bpm.server.service.BusinessPlugin;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogActionTriggerImpl;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.domain.CatalogIdentificationImpl;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import org.apache.commons.chain.Command;

/**
 * Deals with
 * 
 * <ol>
 * <li>Tree Level indexing</li>
 * <li>Content Revisions: Using a trigger, old entry is json-serialized, and
 * stored in a timestamped entry en Revision Catalog</li>
 * <li>Indexing by timestamp :
 * <ul>
 * <li>Content node catalog descriptor defines ordering by timestamp, otherwise
 * ordering is not artificially added</li>
 * <li>Base content node entries ( Actual class implementing PublicContentNode
 * (eg: PersistentTimelineEvent) are managed by their own server module, not
 * this one</li>
 * <li>content node catalog descriptor must be a database-stored catalog
 * descriptor with a database key-id</li>
 * <li>Content node child catalogs that merge ancestors behave normally and are
 * not handled by this module</li>
 * </ul>
 * </li>
 * </ol>
 * 
 * @author japi
 *
 */
@Singleton
public class BusinessPluginImpl implements BusinessPlugin {
    private final Provider<CatalogDescriptor> appItem;


    private final Provider<CatalogDescriptor> notificationProvider;

	private final int SECURE_STORAGE;
	private final Command[] actions;

	@Inject
	public BusinessPluginImpl(ValueChangeListener changeListener,
							  ValueChangeAudit validationTrigger, StakeHolderTrigger stakeHolderTrigger,
							  @Named(Workflow.CATALOG) Provider<CatalogDescriptor> appItem,
							  // @Named(VegetateAuthenticationToken.CATALOG) Provider<CatalogDescriptor> authTokenDescriptor,
							  @Named(Notification.CATALOG) Provider<CatalogDescriptor> notificationProvider,
							  // @Named(Host.CATALOG) Provider<CatalogDescriptor> clientProvider,
							  @Named("catalog.storage." + CatalogDescriptor.SECURE) Integer secureStorageIndex) {
		this.SECURE_STORAGE = secureStorageIndex;
		this.notificationProvider = notificationProvider;

		this.appItem=appItem;

		actions = new Command[]{ validationTrigger,changeListener,stakeHolderTrigger};
	}

	@Override
	public CatalogDescriptor getDescriptorForKey(Long key, CatalogActionContext context)  {
		return null;
	}

	@Override
	public CatalogDescriptor getDescriptorForName(String catalogId, CatalogActionContext context) {
		if (Workflow.CATALOG.equals(catalogId)) {
			return appItem.get();
		} else if (Notification.CATALOG.equals(catalogId)) {
			return notificationProvider.get();
		}/* else if (BPMPeer.NUMERIC_ID.equals(catalogId)) {
			return clientProvider.get();
		} else if (VegetateAuthenticationToken.CATALOG.equals(catalogId)) {
			return authTokenDescriptor.get();
		}*/
		return null;
	}

	@Override
	public void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogActionContext context) {
		names.add(new CatalogIdentificationImpl(ExplicitEventSuscription.CATALOG, ExplicitEventSuscription.CATALOG,
				"/static/img/notification.png"));
		names.add(new CatalogIdentificationImpl(Host.CATALOG, "Open Sessions", "/static/img/session.png"));
		names.add(new CatalogIdentificationImpl(Workflow.CATALOG, "Process", "/static/img/process.png"));
		names.add(new CatalogIdentificationImpl(Notification.CATALOG, Notification.CATALOG,
				"/static/img/notification.png"));
		// organization catalog as visible?
	}

	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor catalog) {
		FieldDescriptor stakeHolderField = catalog.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
		CatalogActionTriggerImpl e;
		if (stakeHolderField != null && !stakeHolderField.isMultiple()
				&& stakeHolderField.getDataType() == CatalogEntry.INTEGER_DATA_TYPE
				&& Person.CATALOG.equals(stakeHolderField.getCatalog())) {

			if (catalog.getStorage() == SECURE_STORAGE) {
				e = new CatalogActionTriggerImpl();
				e.setAction(0);
				e.setAdvice(true);
				e.setHandler(CheckSecureConditions.class.getSimpleName());
				e.setFailSilence(false);
				e.setStopOnFail(true);
				catalog.addTrigger(e);
				e = new CatalogActionTriggerImpl();
				e.setAction(1);
				e.setAdvice(true);
				e.setHandler(CheckSecureConditions.class.getSimpleName());
				e.setFailSilence(false);
				e.setStopOnFail(true);
				catalog.addTrigger(e);
				e = new CatalogActionTriggerImpl();
				e.setAction(2);
				e.setAdvice(true);
				e.setHandler(CheckSecureConditions.class.getSimpleName());
				e.setFailSilence(false);
				e.setStopOnFail(true);
				catalog.addTrigger(e);
			}

			stakeHolderField.setWriteable(false);

			e = new CatalogActionTriggerImpl();
			e.setAction(0);
			e.setAdvice(true);
			e.setHandler(StakeHolderTrigger.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
			catalog.addTrigger(e);
			// asi como los triggers se agregan, modificadores de permisos
			// especificos, alteran los filtros o la visibilidad de una entrada
			// ((CatalogDescriptor) catalog).setStakeHolderProtected(true);

			e = new CatalogActionTriggerImpl();
			e.setAction(1);
			e.setAdvice(true);
			e.setHandler(ValueChangeAudit.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
			catalog.addTrigger(e);

			e = new CatalogActionTriggerImpl();
			e.setAction(1);
			e.setAdvice(false);
			e.setHandler(ValueChangeListener.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
			catalog.addTrigger(e);

			// FIXME writing (or reading require signed authentication)


		}

	}

	@Override
	public ValidationExpression[] getValidations() {
		return null;
	}

	@Override
	public Command[] getActions() {


		return actions;
	}

}




