package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.domain.Trigger;
import com.wrupple.muba.catalogs.domain.TriggerImpl;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.event.domain.impl.FilterDataOrderingImpl;
import com.wrupple.muba.catalogs.server.domain.fields.VersionFields;
import com.wrupple.muba.catalogs.server.service.*;
import com.wrupple.muba.catalogs.server.service.impl.StorageTriggerScope;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.*;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class PluginConsensusImpl implements PluginConsensus {
    protected static final Logger log = LogManager.getLogger(PluginConsensusImpl.class);
    private final Provider<Object> pluginProvider;
    private final String host;

    private final EntrySynthesizer entrySynthesizer;
    /*
	 * versioning
	 */
    private final TriggerImpl versionTrigger;
    /*
     * Indexing
     */
    private final TriggerImpl treeIndex;
    private final Trigger timelineDiscriminator;

    /*
     * timelines
     */
    private TriggerImpl timestamp;
    private ArrayList<String> defaultVersioningTriggerproperties;
    private WritePublicTimelineEventDiscriminator inheritanceHandler;
    private final StorageTriggerScope defaultScope;

    @Inject
    public PluginConsensusImpl(@Named("catalog.plugins") Provider<Object> pluginProvider, @Named("host") String host, EntrySynthesizer entrySynthesizer,WritePublicTimelineEventDiscriminator inheritanceHandler) {
        this.entrySynthesizer = entrySynthesizer;

        this.host=host;
        this.pluginProvider=pluginProvider;
        this.inheritanceHandler = inheritanceHandler;
        treeIndex = new TriggerImpl(-10l,0, UpdateTreeLevelIndex.class.getSimpleName(), true, null, null, null);
        treeIndex.setFailSilence(true);
        treeIndex.setStopOnFail(true);

        timelineDiscriminator = new TriggerImpl(-11l,0,
                WritePublicTimelineEventDiscriminator.class.getSimpleName(), false, null, null, null);
        timelineDiscriminator.setFailSilence(true);
        timelineDiscriminator.setStopOnFail(true);

        timestamp = new TriggerImpl(-12l,0, Timestamper.class.getSimpleName(), true, null, null, null);
        timestamp.setFailSilence(false);
        timestamp.setStopOnFail(true);


        versionTrigger = new TriggerImpl(-13l,1, IncreaseVersionNumber.class.getSimpleName(), true, null, null,
                null);
        versionTrigger.setFailSilence(true);
        versionTrigger.setStopOnFail(true);

        this.defaultVersioningTriggerproperties = new ArrayList<String>(5);
        String putCatalogId = HasCatalogId.CATALOG_FIELD + "=" + CatalogActionRequest.CATALOG_FIELD;
        String putEntryId = HasEntryId.ENTRY_ID_FIELD + "=" + SystemCatalogPlugin.SOURCE_OLD + ".id";
        defaultVersioningTriggerproperties
                .add(Versioned.FIELD + "=" + SystemCatalogPlugin.SOURCE_OLD + "." + Versioned.FIELD);
        defaultVersioningTriggerproperties.add(putEntryId);
        defaultVersioningTriggerproperties.add(putCatalogId);
        defaultVersioningTriggerproperties
                .add("value=" + SystemCatalogPlugin.SOURCE_OLD + "." + Trigger.SERIALIZED);
        defaultScope= new StorageTriggerScope();

    }

    @Override
    public boolean execute(Context c) throws Exception {

        CatalogActionContext context = (CatalogActionContext) c;
        CatalogDescriptor catalog = (CatalogDescriptor)context.getRequest().getEntryValue();
        String name = catalog.getDistinguishedName();
        TriggerCreationScope scope = (TriggerCreationScope) context.get(OVERRIDE_SCOPE);

        if(scope==null){
            scope = defaultScope;
        }

        if(log.isTraceEnabled()){
            log.trace("process descriptor with DN :"+name);
        }
        boolean versioned = catalog.getFieldDescriptor(Versioned.FIELD) != null;

        if (versioned || catalog.getVersioned()!=null && catalog.getVersioned()) {
            if (!versioned) {
                // MUST HAVE VERSION FIELD
                catalog.putField(new VersionFields());
            }
            scope.add(getVersioningTrigger(), catalog,context);

        }

        if (catalog.getRevised()!=null&&catalog.getRevised()) {
            scope.add(getRevisionTrigger(catalog), catalog,context);

        }
        if (catalog.getParent() != null) {
            String greatAncestor = entrySynthesizer.evaluateGreatAncestor(context,catalog,null);
            if (greatAncestor != null && (catalog.getConsolidated()==null||!catalog.getConsolidated())
                    && ContentNode.CATALOG_TIMELINE.equals(greatAncestor)) {

                scope.add(timestamp, catalog,context);

                List<FilterDataOrdering> sorts = catalog.getAppliedSorts();
                FilterDataOrderingImpl index;
                if (sorts == null) {
                    sorts = new ArrayList<FilterDataOrdering>(5);
                    catalog.setAppliedSorts(sorts);
                }
                if (catalog.getFieldDescriptor(IsPinnable.FIELD) != null) {
                    index = new FilterDataOrderingImpl(IsPinnable.FIELD, false);
                    sorts.add(index);
                }
                if (catalog.getFieldDescriptor(HasTimestamp.FIELD) != null) {
                    index = new FilterDataOrderingImpl(HasTimestamp.FIELD, false);
                    sorts.add(index);
                }

                FieldDescriptor field = catalog.getFieldDescriptor(HasChildren.FIELD);

                if (catalog.getFieldDescriptor(inheritanceHandler.getDiscriminatorField()) != null
                        && catalog.getFieldDescriptor(inheritanceHandler.getDiscriminatorField()) != null) {
                    log.debug("catalog is public timeline");
                    scope.add(afterCreateHandledTimeline(), catalog,context);
                }

                if (catalog.getFieldDescriptor(ContentNode.CHILDREN_TREE_LEVEL_INDEX) != null && field != null
                        && catalog.getDistinguishedName().equals(field.getCatalog())) {
                    index = new FilterDataOrderingImpl(ContentNode.CHILDREN_TREE_LEVEL_INDEX, true);
                    sorts.add(index);
                    // INDEXED TREE
                    scope.add(beforeIndexedTreeCreate(), catalog,context);
                }
            }
        }
        CatalogPlugin[] plugins = (CatalogPlugin[]) pluginProvider.get();
        for (CatalogPlugin interpret2 : plugins) {
            log.trace("POST process {} IN {}", name, interpret2);
            interpret2.postProcessCatalogDescriptor(catalog, context, scope);
        }
        if (catalog.getHost() == null) {
            log.trace("locally bound catalog {} @ {}", name, host);
            catalog.setHost(host);
        }
        log.trace("BUILT catalog {}={}", name, catalog);

        return CONTINUE_PROCESSING;
    }

    private Trigger getVersioningTrigger() {
        return versionTrigger;
    }

    private Trigger getRevisionTrigger(CatalogDescriptor c) {
        ArrayList<String> properties = new ArrayList<String>(5);

        properties.addAll(this.defaultVersioningTriggerproperties);

        properties.add("name=" + SystemCatalogPlugin.SOURCE_OLD + "." + c.getDescriptiveField());

        TriggerImpl trigger = new TriggerImpl(-14l,1, CatalogActionRequest.CREATE_ACTION, true,
                ContentRevision.CATALOG, properties, null);
        trigger.setFailSilence(true);
        trigger.setStopOnFail(true);
        return trigger;
    }


    private Trigger afterCreateHandledTimeline() {

        return timelineDiscriminator;
    }

    private Trigger beforeIndexedTreeCreate() {
        return treeIndex;
    }


}
