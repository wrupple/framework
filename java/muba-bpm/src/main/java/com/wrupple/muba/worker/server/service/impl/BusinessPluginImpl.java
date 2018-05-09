package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.TriggerImpl;
import com.wrupple.muba.catalogs.server.chain.command.SystemPersonalitiesStorage;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyReaders;
import com.wrupple.muba.catalogs.server.service.QueryReaders;
import com.wrupple.muba.catalogs.server.service.TriggerCreationScope;
import com.wrupple.muba.catalogs.server.service.impl.StaticCatalogDescriptorProvider;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.worker.domain.Request;
import com.wrupple.muba.worker.server.chain.command.*;
import com.wrupple.muba.worker.server.domain.ValueChangeTrigger;
import com.wrupple.muba.worker.server.service.BusinessPlugin;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

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



	private final Command[] catalogActions;


    @Inject
	public BusinessPluginImpl(SystemPersonalitiesStorage peopleStorage, @Named("catalog.storage.people") String catalogPluginStorage, QueryReaders queryers,
							  PrimaryKeyReaders primaryKeyers, ValueChangeListener changeListener,
							  ValueChangeAudit validationTrigger, StakeHolderTrigger stakeHolderTrigger,
							  @Named(Person.CATALOG) CatalogDescriptor person,
							  // @Named(VegetateAuthenticationToken.CATALOG_TIMELINE) Provider<CatalogDescriptor> authTokenDescriptor,
							  @Named(Request.CATALOG) CatalogDescriptor notificationProvider,
							   @Named(ValueChangeTrigger.CATALOG) CatalogDescriptor audits
							  ) {

        super.put(person);
		super.put(notificationProvider);
		super.put(audits);
		catalogActions = new Command[]{ validationTrigger,changeListener,stakeHolderTrigger};
		queryers.addCommand(catalogPluginStorage,peopleStorage);
		primaryKeyers.addCommand(catalogPluginStorage,peopleStorage);
	}


	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor catalog, CatalogActionContext context, TriggerCreationScope scope) throws Exception {
		FieldDescriptor stakeHolderField = catalog.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
		TriggerImpl e;
		if (stakeHolderField != null && !stakeHolderField.isMultiple()
				&& stakeHolderField.getDataType() == CatalogEntry.INTEGER_DATA_TYPE
				&& Person.CATALOG.equals(stakeHolderField.getCatalog())) {
;
			if (catalog.getStorage()!=null&&catalog.getStorage().contains("secure")) {
				// FIXME writing (or reading require signed private key authentication)
				e = new TriggerImpl(-1l);
				e.setAction(0l);
				e.setAdvice(true);
				e.setName(CheckSecureConditions.class.getSimpleName());
				e.setFailSilence(false);
				e.setStopOnFail(true);
				scope.add(e, catalog,context);

				e = new TriggerImpl(-2l);
				e.setAction(1l);
				e.setAdvice(true);
				e.setName(CheckSecureConditions.class.getSimpleName());
				e.setFailSilence(false);
				e.setStopOnFail(true);
				scope.add(e, catalog,context);

				e = new TriggerImpl(-3l);
				e.setAction(2l);
				e.setAdvice(true);
				e.setName(CheckSecureConditions.class.getSimpleName());
				e.setFailSilence(false);
				e.setStopOnFail(true);
				scope.add(e, catalog,context);
			}

			e = new TriggerImpl(-4l);
			e.setAction(0l);
			e.setAdvice(true);
			e.setName(StakeHolderTrigger.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
			scope.add(e, catalog,context);

			e = new TriggerImpl(-5l);
			e.setAction(1l);
			e.setAdvice(true);
			e.setName(ValueChangeAudit.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
			scope.add(e, catalog,context);

			e = new TriggerImpl(-6l);
			e.setAction(1l);
			e.setAdvice(false);
			e.setName(ValueChangeListener.class.getSimpleName());
			e.setFailSilence(true);
			e.setStopOnFail(true);
			scope.add(e, catalog,context);

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




