package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.ContainerRequest;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

import java.util.List;

public class ContainerRequestImpl extends CatalogEntryImpl implements ContainerRequest {

    private List<String> sentence;

    public ContainerRequestImpl() {
    }

    public ContainerRequestImpl(List<String> sentence) {
        this();
        this.sentence = sentence;
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    @Override
    public Object getCatalog() {
        return getCatalogType();
    }

    @Override
    public void setCatalog(String catalog) {

    }

    @Override
    public List<String> getSentence() {
        return sentence;
    }

    public void setSentence(List<String> sentence) {
        this.sentence = sentence;
    }
}
