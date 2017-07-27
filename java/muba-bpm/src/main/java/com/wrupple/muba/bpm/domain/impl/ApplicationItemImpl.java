package com.wrupple.muba.bpm.domain.impl;

import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.CatalogEntryImpl;
import com.wrupple.muba.bpm.domain.ApplicationItem;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;

import java.util.List;

/**
 * Created by japi on 25/07/17.
 */
public class ApplicationItemImpl extends CatalogEntryImpl implements ApplicationItem {

    private String distinguishedName,outputHandler,description,outputCatalog,catalog;
    private List<String> properties;
    private List<Long> process,requiredElements,children;
    private List<ProcessTaskDescriptor> processValue;
    private List<ApplicationItem> childrenValues;
    private Long stakeHolder,peer;


    @Override
    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName)  {
        if(getName()==null){
            setName(distinguishedName);
        }
        this.distinguishedName = distinguishedName;
    }

    @Override
    public String getOutputHandler() {
        return outputHandler;
    }

    @Override
    public void setOutputHandler(String outputHandler) {
        this.outputHandler = outputHandler;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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
    public List<String> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    @Override
    public List<Long> getProcess() {
        return process;
    }

    public void setProcess(List<Long> process) {
        this.process = process;
    }

    @Override
    public List<Long> getRequiredElements() {
        return requiredElements;
    }

    public void setRequiredElements(List<Long> requiredElements) {
        this.requiredElements = requiredElements;
    }

    @Override
    public List<Long> getChildren() {
        return children;
    }

    public void setChildren(List<Long> children) {
        this.children = children;
    }

    public List<ProcessTaskDescriptor> getProcessValue() {
        return processValue;
    }

    public void setProcessValue(List<ProcessTaskDescriptor> processValue) {
        this.processValue = processValue;
    }

    @Override
    public List<ApplicationItem> getChildrenValues() {
        return childrenValues;
    }

    @Override
    public void setChildrenValues(List<ApplicationItem> childrenValues) {
        this.childrenValues = childrenValues;
    }

    @Override
    public Long getStakeHolder() {
        return stakeHolder;
    }

    @Override
    public void setStakeHolder(long stakeHolder) {
        this.stakeHolder=stakeHolder;
    }

    public void setStakeHolder(Long stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    @Override
    public Long getPeer() {
        return peer;
    }

    public void setPeer(Long peer) {
        this.peer = peer;
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }
}
