package com.wrupple.muba.desktop.server.domain.impl;

import com.wrupple.muba.bpm.domain.BusinessEventDTO;

public class BusinessEventDTOImpl implements BusinessEventDTO {

    private String timestamp;
    private String name;
    private String catalog;
    private String entryId;
    private Object entry;
    private String domain;

    public BusinessEventDTOImpl(long timestamp, String sourceAction, String catalog, String entryId, Object entry, String domain) {
        super();
        this.setName(sourceAction);
        this.timestamp = String.valueOf(timestamp);
        this.catalog = catalog;
        this.setDomain(domain);
        this.setEntry(entry);
    }

    @Override
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = String.valueOf(timestamp);
    }

    @Override
    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String sourceAction) {
        this.name = sourceAction;
    }

    public Object getEntry() {
        return entry;
    }

    public void setEntry(Object entry) {
        this.entry = entry;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

}
