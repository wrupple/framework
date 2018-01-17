package com.wrupple.muba.worker.shared.event;

import com.google.gwt.event.shared.GwtEvent;

import java.util.Collection;

public class EntriesDeletedEvent extends GwtEvent<EntriesDeletedEventHandler> {

    private static Type<EntriesDeletedEventHandler> type;
    public final Collection<String> entries;
    public final String catalog, domain, host;

    public EntriesDeletedEvent(Collection<String> values, String catalog, String domain, String host) {
        this.catalog = catalog;
        this.entries = values;
        this.domain = domain;
        this.host = host;
    }

    public static com.google.gwt.event.shared.GwtEvent.Type<EntriesDeletedEventHandler> getType() {
        if (type == null) {
            type = new Type<EntriesDeletedEventHandler>();
        }
        return type;
    }

    @Override
    protected void dispatch(EntriesDeletedEventHandler handler) {
        handler.onEntriesDeleted(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EntriesDeletedEventHandler> getAssociatedType() {
        return getType();
    }


}
