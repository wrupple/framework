package com.wrupple.muba.bpm.server.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.wrupple.muba.catalogs.domain.CatalogEventListenerImpl;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.impl.StaticCatalogDescriptorProvider;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.server.chain.command.StakeHolderTrigger;
import com.wrupple.muba.bpm.server.chain.command.ValueChangeAudit;
import com.wrupple.muba.bpm.server.chain.command.ValueChangeListener;
import com.wrupple.muba.bpm.server.chain.command.CheckSecureConditions;
import com.wrupple.muba.bpm.server.service.BusinessPlugin;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
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
public class BusinessPluginImpl extends StaticCatalogDescriptorProvider implements BusinessPlugin {


    private final Provider<CatalogTriggerInterpret> triggerInterpret;

	private final Command[] catalogActions;


    @Inject
	public BusinessPluginImpl(ValueChangeListener changeListener,
                              ValueChangeAudit validationTrigger, StakeHolderTrigger stakeHolderTrigger,
							  @Named(ApplicationState.CATALOG) CatalogDescriptor state,
                              @Named(Workflow.CATALOG) CatalogDescriptor appItem,
                              // @Named(VegetateAuthenticationToken.CATALOG_TIMELINE) Provider<CatalogDescriptor> authTokenDescriptor,
                              @Named(WorkRequest.CATALOG) CatalogDescriptor notificationProvider,
                              // @Named(Host.CATALOG_TIMELINE) Provider<CatalogDescriptor> clientProvider,
                              @Named(ServiceManifest.CATALOG) CatalogDescriptor serviceManifestProvider, Provider<CatalogTriggerInterpret> triggerInterpret) {

        this.triggerInterpret = triggerInterpret;
		super.put(serviceManifestProvider);
		super.put(state);
		super.put(appItem);
		super.put(notificationProvider);
		catalogActions = new Command[]{ validationTrigger,changeListener,stakeHolderTrigger};
	}


	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor catalog, CatalogActionContext context) throws Exception {
		FieldDescriptor stakeHolderField = catalog.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
		CatalogEventListenerImpl e;
		if (stakeHolderField != null && !stakeHolderField.isMultiple()
				&& stakeHolderField.getDataType() == CatalogEntry.INTEGER_DATA_TYPE
				&& Person.CATALOG.equals(stakeHolderField.getCatalog())) {

			if (catalog.getStorage().contains("secure")) {
				e = new CatalogEventListenerImpl();
				e.setAction(0l);
				e.setAdvice(true);
				e.setName(CheckSecureConditions.class.getSimpleName());
				e.setFailSilence(false);
				e.setStopOnFail(true);
                triggerInterpret.get().addNamespaceScopeTrigger(e, catalog,context);

				e = new CatalogEventListenerImpl();
				e.setAction(1l);
				e.setAdvice(true);
				e.setName(CheckSecureConditions.class.getSimpleName());
				e.setFailSilence(false);
				e.setStopOnFail(true);
                triggerInterpret.get().addNamespaceScopeTrigger(e, catalog,context);

				e = new CatalogEventListenerImpl();
				e.setAction(2l);
				e.setAdvice(true);
				e.setName(CheckSecureConditions.class.getSimpleName());
				e.setFailSilence(false);
				e.setStopOnFail(true);
                triggerInterpret.get().addNamespaceScopeTrigger(e, catalog,context);
			}

			stakeHolderField.setWriteable(false);

			e = new CatalogEventListenerImpl();
			e.setAction(0l);
			e.setAdvice(true);
			e.setName(StakeHolderTrigger.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
            triggerInterpret.get().addNamespaceScopeTrigger(e, catalog,context);
			// asi como los triggers se agregan, modificadores de permisos
			// especificos, alteran los filtros o la visibilidad de una entrada
			// ((CatalogDescriptor) catalog).setStakeHolderProtected(true);

			e = new CatalogEventListenerImpl();
			e.setAction(1l);
			e.setAdvice(true);
			e.setName(ValueChangeAudit.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
            triggerInterpret.get().addNamespaceScopeTrigger(e, catalog,context);

			e = new CatalogEventListenerImpl();
			e.setAction(1l);
			e.setAdvice(false);
			e.setName(ValueChangeListener.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
            triggerInterpret.get().addNamespaceScopeTrigger(e, catalog,context);

			// FIXME writing (or reading require signed private key authentication)


		}

	}

	@Override
	public ValidationExpression[] getValidations() {
		return null;
	}

	@Override
	public Command[] getCatalogActions() {


		return catalogActions;
	}

}




