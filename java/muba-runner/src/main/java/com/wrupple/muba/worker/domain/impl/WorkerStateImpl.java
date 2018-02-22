package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;

import javax.validation.constraints.NotNull;
import java.util.List;

public class WorkerStateImpl extends CatalogEntryImpl implements WorkerState {

    private List<String> sentence;
    private Long wordIndex;
    @NotNull //FIXME static storage unit
    private Long runner;
    private String characterEncoding, homeActivity;

    @CatalogField(ignore = true)
    private Application applicationTree;



    @ForeignKey(foreignCatalog =  ApplicationState.CATALOG)
    @CatalogField(filterable = true)
    private Long state;

    @NotNull
    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = ApplicationState.CATALOG)
    private ApplicationState stateValue;



    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Host.CATALOG)
    private Host hostValue;
    @ForeignKey(foreignCatalog = Host.CATALOG)
    private Long host;


    @ForeignKey(foreignCatalog =  Application.CATALOG)
    private Long homeApplication;
    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Application.CATALOG)
    private Application homeApplicationValue;
    @CatalogField(ignore = true)
    private HasAccesablePropertyValues parametersValue;



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
    public Long getRunner() {
        return runner;
    }

    public void setRunner(Long runner) {
        this.runner = runner;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    @Override
    public String getHomeActivity() {
        return homeActivity;
    }

    @Override
    public void setHomeActivity(String homeActivity) {
        this.homeActivity = homeActivity;
    }

    public Application getApplicationTree() {
        return applicationTree;
    }

    @Override
    public void setApplicationTree(Application applicationTree) {
        this.applicationTree = applicationTree;
    }


    public ApplicationState getStateValue() {
        return stateValue;
    }

    public void setStateValue(ApplicationState stateValue) {
        this.stateValue = stateValue;
    }

    @Override
    public Application getHomeApplicationValue() {
        return homeApplicationValue;
    }



    public Long getState() {
        return this.state ;
    }


    public void setState(Long state) {
        this.state = state;
    }


    public void setHomeApplicationValue(Application homeApplicationValue) {
        this.homeApplicationValue = homeApplicationValue;
    }

    public Long getHomeApplication() {
        return homeApplication;
    }

    public void setHomeApplication(Long homeApplication) {
        this.homeApplication = homeApplication;
    }

    @Override
    public List<String> getSentence() {
        return sentence;
    }

    @Override
    public void setSentence(List<String> sentence) {
        this.sentence = sentence;
    }

    @Override
    public Long getWordIndex() {
        return wordIndex;
    }

    @Override
    public void setWordIndex(Long wordIndex) {
        this.wordIndex = wordIndex;
    }

    @Override
    public HasAccesablePropertyValues getParametersValue() {
        return parametersValue;
    }

    public void setParametersValue(HasAccesablePropertyValues parametersValue) {
        this.parametersValue = parametersValue;
    }

    @Override
    public Host getHostValue() {
        return hostValue;
    }

    @Override
    public void setHostValue(Host hostValue) {
        this.hostValue = hostValue;
    }

    @Override
    public Long getHost() {
        return host;
    }

    public void setHost(Long host) {
        this.host = host;
    }
}
