package com.wrupple.muba.bpm.server.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.Host;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.reserved.HasStakeHolder;
import com.wrupple.muba.bootstrap.server.service.CatalogManager;
import com.wrupple.muba.bootstrap.server.service.ContentManagementSystem;
import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.bpm.domain.BusinessEvent;
import com.wrupple.muba.bpm.domain.ExplicitEventSuscription;
import com.wrupple.muba.bpm.domain.Notification;
import com.wrupple.muba.bpm.domain.ProcessDescriptor;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.TaskToolbarDescriptor;
import com.wrupple.muba.bpm.domain.VegetateAuthenticationToken;
import com.wrupple.muba.bpm.domain.WruppleActivityAction;
import com.wrupple.muba.bpm.domain.WruppleDomainHTMLPage;
import com.wrupple.muba.bpm.domain.WruppleDomainJavascript;
import com.wrupple.muba.bpm.domain.WruppleDomainStyleSheet;
import com.wrupple.muba.bpm.server.chain.command.BPMStakeHolderTrigger;
import com.wrupple.muba.bpm.server.chain.command.BPMValidationTrigger;
import com.wrupple.muba.bpm.server.chain.command.BPMValueChangeListener;
import com.wrupple.muba.bpm.server.chain.command.CheckSecureConditions;
import com.wrupple.muba.bpm.server.service.BusinessPlugin;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogActionTriggerImpl;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.domain.CatalogIdentificationImpl;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.WrupleSVGDocument;
import com.wrupple.muba.catalogs.server.chain.command.WriteFormatedDocument;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;

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

	private final int SECURE_STORAGE;
	private final Provider<CatalogDescriptor> taskDescP;
	private final Provider<CatalogDescriptor> actionDescP;
	private final Provider<CatalogDescriptor> toolbarDescP;

	private final Provider<CatalogDescriptor> processDescriptorProvider;
	private Provider<CatalogDescriptor> notificationProvider;
	private Provider<CatalogDescriptor> clientProvider;

	private Provider<CatalogDescriptor> authTokenDescriptor;
	private final CatalogDescriptorBuilder builder;
	private final Provider<CatalogDescriptor> event;

	@Inject
	public BusinessPluginImpl(CatalogManager databasePlugin, WriteFormatedDocument documentWriter, ContentManagementSystem cms, CatalogEvaluationDelegate synthesizer,
			@Named(BusinessEvent.CATALOG) Provider<CatalogDescriptor> event,
			@Named(ProcessTaskDescriptor.CATALOG) Provider<CatalogDescriptor> taskDescP,
			@Named(TaskToolbarDescriptor.CATALOG) Provider<CatalogDescriptor> toolbarDescP,
			@Named(WruppleActivityAction.CATALOG) Provider<CatalogDescriptor> actionDescP,
			CatalogDescriptorBuilder builder, BPMValueChangeListener changeListener,
			BPMValidationTrigger validationTrigger, BPMStakeHolderTrigger stakeHolderTrigger,
			CatalogManager transactions,
			@Named(VegetateAuthenticationToken.CATALOG) Provider<CatalogDescriptor> authTokenDescriptor,
			@Named(ProcessDescriptor.CATALOG) Provider<CatalogDescriptor> processDescriptorProvider,
			@Named(Notification.CATALOG) Provider<CatalogDescriptor> notificationProvider,
			@Named(Host.CATALOG) Provider<CatalogDescriptor> clientProvider,
			@Named("catalog.storage." + CatalogDescriptor.SECURE) Integer secureStorageIndex, CheckSecureConditions secureTrigger) {
		this.taskDescP = taskDescP;
		this.actionDescP = actionDescP;
		this.toolbarDescP = toolbarDescP;
		this.event = event;
		this.SECURE_STORAGE = secureStorageIndex;

		cms.addCommand(WruppleDomainHTMLPage.CATALOG, documentWriter);
		cms.addCommand(WruppleDomainJavascript.CATALOG, documentWriter);
		cms.addCommand(WruppleDomainStyleSheet.CATALOG, documentWriter);
		cms.addCommand(WrupleSVGDocument.CATALOG, documentWriter);
		this.processDescriptorProvider = processDescriptorProvider;
		this.notificationProvider = notificationProvider;
		this.clientProvider = clientProvider;
		this.authTokenDescriptor = authTokenDescriptor;
		this.builder = builder;
		transactions.addCommand(BPMValidationTrigger.class.getSimpleName(), validationTrigger);
		transactions.addCommand(BPMValueChangeListener.class.getSimpleName(), changeListener);
		transactions.addCommand(BPMStakeHolderTrigger.class.getSimpleName(), stakeHolderTrigger);
		transactions.addCommand(CheckSecureConditions.class.getSimpleName(), secureTrigger);
	}

	@Override
	public CatalogDescriptor getDescriptorForKey(Long key, CatalogActionContext context) throws Exception {
		return null;
	}

	@Override
	public CatalogDescriptor getDescriptorForName(String catalogId, CatalogActionContext context) {
		if (ProcessTaskDescriptor.CATALOG.equals(catalogId)) {
			return taskDescP.get();
		} else if (TaskToolbarDescriptor.CATALOG.equals(catalogId)) {
			return toolbarDescP.get();
		} else if (WruppleActivityAction.CATALOG.equals(catalogId)) {
			return actionDescP.get();
		} else if (ProcessDescriptor.CATALOG.equals(catalogId)) {
			return processDescriptorProvider.get();
		} else if (Notification.CATALOG.equals(catalogId)) {
			return notificationProvider.get();
		} else if (BPMPeer.NUMERIC_ID.equals(catalogId)) {
			return clientProvider.get();
		} else if (VegetateAuthenticationToken.CATALOG.equals(catalogId)) {
			return authTokenDescriptor.get();
		} else if (BusinessEvent.CATALOG.equals(catalogId)) {
			return event.get();
		}
		return null;
	}

	@Override
	public void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogActionContext context) {
		names.add(new CatalogIdentificationImpl(ProcessTaskDescriptor.CATALOG, "Task Descriptor",
				"/static/img/task.png"));
		names.add(
				new CatalogIdentificationImpl(WruppleActivityAction.CATALOG, "Task Action", "/static/img/action.png"));
		names.add(new CatalogIdentificationImpl(TaskToolbarDescriptor.CATALOG, "Task Toolbar",
				"/static/img/task-piece.png"));
		names.add(new CatalogIdentificationImpl(ExplicitEventSuscription.CATALOG, ExplicitEventSuscription.CATALOG,
				"/static/img/notification.png"));
		names.add(new CatalogIdentificationImpl(Host.CATALOG, "Open Sessions", "/static/img/session.png"));
		names.add(new CatalogIdentificationImpl(ProcessDescriptor.CATALOG, "Process", "/static/img/process.png"));
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
			e.setHandler(BPMStakeHolderTrigger.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
			catalog.addTrigger(e);
			// asi como los triggers se agregan, modificadores de permisos
			// especificos, alteran los filtros o la visibilidad de una entrada
			// ((CatalogDescriptor) catalog).setStakeHolderProtected(true);

			e = new CatalogActionTriggerImpl();
			e.setAction(1);
			e.setAdvice(true);
			e.setHandler(BPMValidationTrigger.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
			catalog.addTrigger(e);

			e = new CatalogActionTriggerImpl();
			e.setAction(1);
			e.setAdvice(false);
			e.setHandler(BPMValueChangeListener.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
			catalog.addTrigger(e);

			// FIXME writing (or reading require signed authentication)

			// see that triggers are linked to the a.i.

		}

	}

	@Override
	public ValidationExpression[] getValidations() {
		return null;
	}


}




