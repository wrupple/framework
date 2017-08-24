package com.wrupple.muba.bpm.server.domain;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.bpm.domain.IntentResolverContext;
import org.apache.commons.chain.impl.ContextBase;

/**
 * Created by japi on 12/08/17.
 */
public class IntentResolverContextImpl extends ContextBase implements IntentResolverContext {


    public final Long getId() {
        return null;
    }

    public final void setId(Long catalogId) {

    }

    public final String getName() {
        return null;
    }

    public final void setName(String name) {

    }

    @Override
    public final Long getImage() {
        return null;
    }

    public final void setImage(Long image) {

    }

    public final Long getDomain() {
        return null;
    }

    public final void setDomain(Long domain) {

    }

    public final boolean isAnonymouslyVisible() {
        return false;
    }

    public final void setAnonymouslyVisible(boolean anonymouslyVisible) {
    }



    private RuntimeContext runtimeContext;

    private String outputCatalog,catalog;

    @Override
    public String getOutputCatalog() {
        return outputCatalog;
    }

    public void setOutputCatalog(String outputCatalog) {
        this.outputCatalog = outputCatalog;
    }

    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    @Override
    public void setExcecutionContext(RuntimeContext requestContext) {
        this.runtimeContext=requestContext;
    }
}
