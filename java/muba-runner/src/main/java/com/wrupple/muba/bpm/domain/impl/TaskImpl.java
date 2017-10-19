package com.wrupple.muba.bpm.domain.impl;

import java.util.List;

import com.wrupple.muba.bpm.domain.Task;
import com.wrupple.muba.bpm.domain.TaskToolbarDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogEventListener;
import com.wrupple.muba.catalogs.domain.CatalogJobImpl;
import com.wrupple.muba.event.domain.CatalogEntryImpl;
import com.wrupple.muba.event.domain.annotations.CatalogField;

public class TaskImpl extends CatalogJobImpl implements Task {


    private List<Long> userActions;
    private List<Long> toolbars;
    private List<TaskToolbarDescriptor> toolbarsValues;
    private List<String> grammar;
    private String distinguishedName;
    private String outputField;
    private Object stakeHolder;

    @Override
    public Object getStakeHolder() {
        return stakeHolder;
    }

    public void setStakeHolder(Object stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    @Override
    public List<Long> getUserActions() {
        return userActions;
    }

    public void setUserActions(List<Long> userActions) {
        this.userActions = userActions;
    }

    @Override
    public List<Long> getToolbars() {
        return toolbars;
    }

    public void setToolbars(List<Long> toolbars) {
        this.toolbars = toolbars;
    }

    @Override
    public List<TaskToolbarDescriptor> getToolbarsValues() {
        return toolbarsValues;
    }

    public void setToolbarsValues(List<TaskToolbarDescriptor> toolbarsValues) {
        this.toolbarsValues = toolbarsValues;
    }

    @Override
    public List<String> getGrammar() {
        return grammar;
    }

    public void setGrammar(List<String> grammar) {
        this.grammar = grammar;
    }

    @Override
    public String getDistinguishedName() {
        return distinguishedName;
    }

    @Override
    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    @Override
    public String getOutputField() {
        return outputField;
    }

    @Override
    public void setOutputField(String outputField) {
        this.outputField = outputField;
    }


    @Override
    public String getCatalogType() {
        return Task.CATALOG;
    }
}
