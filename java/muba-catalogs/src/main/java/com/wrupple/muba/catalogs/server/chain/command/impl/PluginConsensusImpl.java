package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.domain.CatalogEventListener;
import com.wrupple.muba.catalogs.domain.CatalogEventListenerImpl;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.domain.FilterDataOrderingImpl;
import com.wrupple.muba.catalogs.server.domain.fields.VersionFields;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.FilterDataOrdering;
import com.wrupple.muba.event.domain.reserved.*;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

public class PluginConsensusImpl implements PluginConsensus {
    protected static final Logger log = LoggerFactory.getLogger(PluginConsensusImpl.class);
    private final Provider<Object> pluginProvider;
    private final String host;

    private final CatalogTriggerInterpret triggerInterpret;
    /*
	 * versioning
	 */
    private final CatalogEventListenerImpl versionTrigger;
    /*
     * Indexing
     */
    private final CatalogEventListenerImpl treeIndex;
    private final CatalogEventListener timelineDiscriminator;

    /*
     * timelines
     */
    private CatalogEventListenerImpl timestamp;
    private ArrayList<String> defaultVersioningTriggerproperties;
    private WritePublicTimelineEventDiscriminator inheritanceHandler;

    @Inject
    public PluginConsensusImpl(@Named("catalog.plugins") Provider<Object> pluginProvider,@Named("host") String host, CatalogTriggerInterpret triggerInterpret, WritePublicTimelineEventDiscriminator inheritanceHandler) {
        this.triggerInterpret = triggerInterpret;

        this.host=host;
        this.pluginProvider=pluginProvider;
        this.inheritanceHandler = inheritanceHandler;
        treeIndex = new CatalogEventListenerImpl(0, UpdateTreeLevelIndex.class.getSimpleName(), true, null, null, null);
        treeIndex.setFailSilence(true);
        treeIndex.setStopOnFail(true);

        timelineDiscriminator = new CatalogEventListenerImpl(0,
                WritePublicTimelineEventDiscriminator.class.getSimpleName(), false, null, null, null);
        timelineDiscriminator.setFailSilence(true);
        timelineDiscriminator.setStopOnFail(true);

        timestamp = new CatalogEventListenerImpl(0, Timestamper.class.getSimpleName(), true, null, null, null);
        timestamp.setFailSilence(false);
        timestamp.setStopOnFail(true);


        versionTrigger = new CatalogEventListenerImpl(1, IncreaseVersionNumber.class.getSimpleName(), true, null, null,
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
                .add("value=" + SystemCatalogPlugin.SOURCE_OLD + "." + CatalogEventListener.SERIALIZED);

    }

    @Override
    public boolean execute(Context c) throws Exception {

        CatalogActionContext context = (CatalogActionContext) c;
        //Instrospection instrospection = context.getCatalogManager().access().newSession((CatalogEntry) context.getRequest().getEntryValue());
        CatalogDescriptor catalog = (CatalogDescriptor)context.getRequest().getEntryValue();
        String name = catalog.getDistinguishedName();


        if(log.isTraceEnabled()){
            log.trace("process descriptor with DN :"+name);
        }
        boolean versioned = catalog.getFieldDescriptor(Versioned.FIELD) != null;

        if (versioned || catalog.isVersioned()) {
            if (!versioned) {
                // MUST HAVE VERSION FIELD
                catalog.putField(new VersionFields());
            }
            triggerInterpret.addNamespaceScopeTrigger(getVersioningTrigger(), catalog,context);

        }

        if (catalog.isRevised()) {
            triggerInterpret.addNamespaceScopeTrigger(getRevisionTrigger(catalog), catalog,context);

        }
        if (catalog.getParent() != null) {
            if (catalog.getGreatAncestor() == null) {
                // find great ancestor
                CatalogDescriptor parent = context.getDescriptorForKey(catalog.getParent());

                while (parent != null) {
                    catalog.setGreatAncestor(parent.getDistinguishedName());
                    parent = parent.getParent() == null ? null : context.getDescriptorForKey(parent.getParent());
                }
            }
            if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()
                    && ContentNode.CATALOG_TIMELINE.equals(catalog.getGreatAncestor())) {

                triggerInterpret.addNamespaceScopeTrigger(timestamp, catalog,context);

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
                    triggerInterpret.addNamespaceScopeTrigger(afterCreateHandledTimeline(), catalog,context);
                }

                if (catalog.getFieldDescriptor(ContentNode.CHILDREN_TREE_LEVEL_INDEX) != null && field != null
                        && catalog.getDistinguishedName().equals(field.getCatalog())) {
                    index = new FilterDataOrderingImpl(ContentNode.CHILDREN_TREE_LEVEL_INDEX, true);
                    sorts.add(index);
                    // INDEXED TREE
                    triggerInterpret.addNamespaceScopeTrigger(beforeIndexedTreeCreate(), catalog,context);
                }
            }
        }
        CatalogPlugin[] plugins = (CatalogPlugin[]) pluginProvider.get();
        for (CatalogPlugin interpret2 : plugins) {
            log.trace("POST process {} IN {}", name, interpret2);
            interpret2.postProcessCatalogDescriptor(catalog, context);
        }
        if (catalog.getHost() == null) {
            log.trace("locally bound catalog {} @ {}", name, host);
            catalog.setHost(host);
        }
        log.trace("BUILT catalog {}={}", name, catalog);

        return CONTINUE_PROCESSING;
    }

    private CatalogEventListener getVersioningTrigger() {
        return versionTrigger;
    }

    private CatalogEventListener getRevisionTrigger(CatalogDescriptor c) {
        ArrayList<String> properties = new ArrayList<String>(5);

        properties.addAll(this.defaultVersioningTriggerproperties);

        properties.add("name=" + SystemCatalogPlugin.SOURCE_OLD + "." + c.getDescriptiveField());

        CatalogEventListenerImpl trigger = new CatalogEventListenerImpl(1, CatalogActionRequest.CREATE_ACTION, true,
                ContentRevision.CATALOG, properties, null);
        trigger.setFailSilence(true);
        trigger.setStopOnFail(true);
        return trigger;
    }


    private CatalogEventListener afterCreateHandledTimeline() {

        return timelineDiscriminator;
    }

    private CatalogEventListener beforeIndexedTreeCreate() {
        return treeIndex;
    }

}
