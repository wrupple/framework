package com.wrupple.muba.bpm.domain.impl;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntryImpl;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.TaskToolbarDescriptor;
import com.wrupple.muba.bpm.domain.WruppleActivityAction;
import com.wrupple.muba.catalogs.domain.annotations.CatalogField;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldDefault;
import com.wrupple.muba.catalogs.domain.annotations.CatalogKey;

import javax.validation.constraints.NotNull;

public class ProcessTaskDescriptorImpl  implements ProcessTaskDescriptor {
	private static final long serialVersionUID = 7957074775049623796L;
	private String catalog,distinguishedName,transactionType,outputField;
	private List<String> properties,sentence;
	//@CatalogKey(foreignCatalog = WruppleActivityAction.CATALOG)
	private List<Long> userActions;
	//s@CatalogKey(foreignCatalog = ToolbarConfiguration.CATALOG)
	private List<Long> toolbars;
	@CatalogField(ignore = true)
	private  List<? extends TaskToolbarDescriptor> toolbarsValues;


    private Long id,image;
    private String  name;
    private Long domain;
    private boolean anonymouslyVisible;


    public final Long getId() {
        return id;
    }

    public final void setId(Long catalogId) {
        this.id = catalogId;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public final Long getImage() {
        return image;
    }

    public final void setImage(Long image) {
        this.image = image;
    }

    public final Long getDomain() {
        return domain;
    }

    public final void setDomain(Long domain) {
        this.domain = domain;
    }

    public final boolean isAnonymouslyVisible() {
        return anonymouslyVisible;
    }

    public final void setAnonymouslyVisible(boolean anonymouslyVisible) {
        this.anonymouslyVisible = anonymouslyVisible;
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
    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    @Override
    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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
    public List<String> getSentence() {
        return sentence;
    }

    public void setSentence(List<String> sentence) {
        this.sentence = sentence;
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
    public List<? extends TaskToolbarDescriptor> getToolbarsValues() {
        return toolbarsValues;
    }

    @Override
    public List<String> getUrlTokens() {
        return null;
    }

    public void setToolbarsValues(List<? extends TaskToolbarDescriptor> toolbarsValues) {
        this.toolbarsValues = toolbarsValues;
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    @Override
    public String getOutputField() {
        return outputField;
    }

    @Override
    public void setOutputField(String outputField) {
        this.outputField = outputField;
    }
}
