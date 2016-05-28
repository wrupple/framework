package com.wrupple.muba.cms.server.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.wrupple.muba.catalogs.domain.CatalogActionTriggerImpl;
import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.domain.CatalogIdentificationImpl;
import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.ContentManagementSystem;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.PersistentCatalogEntityDAO;
import com.wrupple.muba.catalogs.server.service.PropertyMapDAO;
import com.wrupple.muba.cms.domain.ContentRevision;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.cms.domain.TaskToolbarDescriptor;
import com.wrupple.muba.cms.domain.WruppleActivityAction;
import com.wrupple.muba.cms.domain.WruppleDomainHTMLPage;
import com.wrupple.muba.cms.domain.WruppleDomainJavascript;
import com.wrupple.muba.cms.domain.WruppleDomainStyleSheet;
import com.wrupple.muba.cms.server.chain.command.IncreaseVersionNumber;
import com.wrupple.muba.cms.server.chain.command.UpdateTreeLevelIndex;
import com.wrupple.muba.cms.server.chain.command.WritePublicTimelineEventDiscriminator;
import com.wrupple.muba.cms.server.chain.command.impl.CssContentManager;
import com.wrupple.muba.cms.server.chain.command.impl.HtmlContentManager;
import com.wrupple.muba.cms.server.chain.command.impl.JavaScriptContentManager;
import com.wrupple.muba.cms.server.services.ContentPlugin;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterDataOrdering;
import com.wrupple.vegetate.domain.HasAccesablePropertyValues;
import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.domain.HasEntryId;
import com.wrupple.vegetate.domain.Versioned;
import com.wrupple.vegetate.domain.structure.HasChildren;
import com.wrupple.vegetate.domain.structure.HasTimestamp;
import com.wrupple.vegetate.server.chain.CatalogManager;
import com.wrupple.vegetate.server.domain.FilterDataOrderingImpl;
import com.wrupple.vegetate.server.domain.ValidationExpression;
import com.wrupple.vegetate.server.domain.VersionFields;

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
public class ContentPluginImpl implements ContentPlugin {

	private final Provider<PersistentCatalogEntityDAO> pced;
	private final String ancestorIdField;
	private final Provider<CatalogDescriptor> taskDescP;
	private final Provider<CatalogDescriptor> actionDescP;
	private final Provider<CatalogDescriptor> toolbarDescP;
	private final Provider<CatalogDescriptor> revisionP;
	private final List<String> defaultVersioningTriggerproperties;
	private final CatalogPropertyAccesor access;
	private final DatabasePlugin databasePlugin;

	@Inject
	public ContentPluginImpl(DatabasePlugin databasePlugin, CssContentManager css, HtmlContentManager html, JavaScriptContentManager js,
			ContentManagementSystem cms, UpdateTreeLevelIndex treeIndexHandler, WritePublicTimelineEventDiscriminator inheritanceHandler,
			IncreaseVersionNumber increaseVersionNumber, CatalogManager transactionDictionary, Provider<PersistentCatalogEntityDAO> pced,
			CatalogPropertyAccesor synthesizer, @Named("ancestorIdField") String ancestorIdField,
			@Named(ProcessTaskDescriptor.CATALOG) Provider<CatalogDescriptor> taskDescP,
			@Named(TaskToolbarDescriptor.CATALOG) Provider<CatalogDescriptor> toolbarDescP,
			@Named(WruppleActivityAction.CATALOG) Provider<CatalogDescriptor> actionDescP,
			@Named(ContentRevision.CATALOG) Provider<CatalogDescriptor> revisionP) {
		super();
		this.revisionP = revisionP;
		this.taskDescP = taskDescP;
		this.actionDescP = actionDescP;
		this.toolbarDescP = toolbarDescP;
		this.pced = pced;
		this.access = synthesizer;
		this.ancestorIdField = ancestorIdField;
		this.databasePlugin = databasePlugin;
		String putCatalogId = HasCatalogId.FIELD + "=" + CatalogActionRequest.CATALOG_ID_PARAMETER;
		String putEntryId = HasEntryId.FIELD + "=" + CatalogActionTrigger.SOURCE_OLD + ".id";

		this.defaultVersioningTriggerproperties = new ArrayList<String>(5);

		defaultVersioningTriggerproperties.add(Versioned.FIELD + "=" + CatalogActionTrigger.SOURCE_OLD + "." + Versioned.FIELD);
		defaultVersioningTriggerproperties.add(putEntryId);
		defaultVersioningTriggerproperties.add(putCatalogId);
		defaultVersioningTriggerproperties.add("value=" + CatalogActionTrigger.SOURCE_OLD + "." + CatalogActionTrigger.SERIALIZED);

		cms.addCommand(WruppleDomainHTMLPage.CATALOG, html);
		cms.addCommand(WruppleDomainJavascript.CATALOG, js);
		cms.addCommand(WruppleDomainStyleSheet.CATALOG, css);
		transactionDictionary.addCommand(IncreaseVersionNumber.class.getSimpleName(), increaseVersionNumber);
		transactionDictionary.addCommand(UpdateTreeLevelIndex.class.getSimpleName(), treeIndexHandler);
		transactionDictionary.addCommand(WritePublicTimelineEventDiscriminator.class.getSimpleName(), inheritanceHandler);
	}

	@Override
	public void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogExcecutionContext context) throws Exception {
		names.add(new CatalogIdentificationImpl(ProcessTaskDescriptor.CATALOG, "Task Descriptor", "/static/img/task.png"));
		names.add(new CatalogIdentificationImpl(WruppleActivityAction.CATALOG, "Task Action", "/static/img/action.png"));
		names.add(new CatalogIdentificationImpl(TaskToolbarDescriptor.CATALOG, "Task Toolbar", "/static/img/task-piece.png"));
		names.add(new CatalogIdentificationImpl(ContentRevision.CATALOG, "Revision", "/static/img/revision.png"));
		if (context.getDomainContext().isRecycleBinEnabled()) {
			names.add(new CatalogIdentificationImpl(Trash.CATALOG, "Trash", "/static/img/trash.png"));
		}
	}

	@Override
	public CatalogDescriptor getDescriptorForName(String catalogId, CatalogExcecutionContext context) {
		if (ProcessTaskDescriptor.CATALOG.equals(catalogId)) {
			return taskDescP.get();
		} else if (TaskToolbarDescriptor.CATALOG.equals(catalogId)) {
			return toolbarDescP.get();
		} else if (WruppleActivityAction.CATALOG.equals(catalogId)) {
			return actionDescP.get();
		} else if (ContentRevision.CATALOG.equals(catalogId)) {
			return revisionP.get();
		}
		return null;
	}

	@Override
	public void postProcessCatalogDescriptor(final CatalogDescriptor catalog) {
		List<CatalogActionTrigger> triggerValues = catalog.getTriggersValues();

		boolean versioned = catalog.getFieldDescriptor(Versioned.FIELD) != null;

		if (versioned || catalog.isVersioned()) {
			if (!versioned) {
				// MUST HAVE VERSION FIELD
				catalog.putField(new VersionFields());
			}
			triggerValues.add(getVersioningTrigger());
		}

		if (catalog.isRevised()) {
			triggerValues.add(getRevisionTrigger(catalog));
		}

		if (catalog.getParent() != null) {

			if (catalog.getGreatAncestor() != null && !catalog.isConsolidated() && ContentNode.CATALOG.equals(catalog.getGreatAncestor())) {
				List<FilterDataOrdering> sorts = catalog.getAppliedSorts();
				FilterDataOrderingImpl index;
				if (sorts == null) {
					sorts = new ArrayList<>(5);
					catalog.setAppliedSorts(sorts);
				}
				if (catalog.getFieldDescriptor(HasTimestamp.FIELD) != null) {
					index = new FilterDataOrderingImpl(HasTimestamp.FIELD, false);
					sorts.add(index);
				}

				FieldDescriptor field = catalog.getFieldDescriptor(HasChildren.FIELD);
				if (catalog.getFieldDescriptor(ContentNode.CHILDREN_TREE_LEVEL_INDEX) != null && field != null
						&& catalog.getCatalogId().equals(field.getForeignCatalogName())) {
					index = new FilterDataOrderingImpl(ContentNode.CHILDREN_TREE_LEVEL_INDEX, true);
					sorts.add(index);
					// INDEXED TREE
					triggerValues.add(beforeIndexedTreeCreate());
					triggerValues.add(afterCreateHandledTimeline());
				}
			}
		}

	}

	private CatalogActionTrigger afterCreateHandledTimeline() {
		CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(0, WritePublicTimelineEventDiscriminator.class.getSimpleName(), false, null, null,
				null);
		trigger.setRollbackOnFail(true);
		trigger.setStopOnFail(true);
		return trigger;
	}

	private CatalogActionTrigger beforeIndexedTreeCreate() {
		CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(0, UpdateTreeLevelIndex.class.getSimpleName(), true, null, null, null);
		trigger.setRollbackOnFail(true);
		trigger.setStopOnFail(true);
		return trigger;
	}

	private CatalogActionTrigger getVersioningTrigger() {
		CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1, IncreaseVersionNumber.class.getSimpleName(), true, null, null, null);
		trigger.setRollbackOnFail(true);
		trigger.setStopOnFail(true);
		return trigger;
	}

	private CatalogActionTrigger getRevisionTrigger(CatalogDescriptor c) {
		ArrayList<String> properties = new ArrayList<String>(5);

		properties.addAll(this.defaultVersioningTriggerproperties);

		properties.add("name=" + CatalogActionTrigger.SOURCE_OLD + "." + c.getDescriptiveField());

		CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1, CatalogActionRequest.CREATE_ACTION, true, ContentRevision.CATALOG, properties, null);
		trigger.setRollbackOnFail(true);
		trigger.setStopOnFail(true);
		return trigger;
	}

	@Override
	public CatalogDataAccessObject<? extends CatalogEntry> getOrAssembleDataSource(CatalogDescriptor catalog, Class<? extends CatalogEntry> clazz,
			CatalogExcecutionContext context) throws Exception {
		if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {
			PropertyMapDAO<HasAccesablePropertyValues> regreso = (PropertyMapDAO) pced.get();
			regreso.setContext(context);
			regreso.setCatalogDescriptor(catalog);
			return new InheritanceAwareDataAccessObjectImpl(databasePlugin, regreso, catalog, this.ancestorIdField, context, access);
		}
		return null;
	}

	@Override
	public ValidationExpression[] getValidations() {
		return null;
	}

}
