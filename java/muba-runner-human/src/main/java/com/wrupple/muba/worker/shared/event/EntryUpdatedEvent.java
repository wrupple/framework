package com.wrupple.muba.worker.shared.event;

import com.google.gwt.event.shared.GwtEvent;
import com.wrupple.muba.event.domain.CatalogEntry;

public class EntryUpdatedEvent extends GwtEvent<EntryUpdatedEventHandler> {
    private static Type<EntryUpdatedEventHandler> type;
    public final CatalogEntry entry;
    public final String host, domain;

    public EntryUpdatedEvent(CatalogEntry entry, String host, String domain) {
        this.entry = entry;
        this.domain = domain;
        this.host = host;
    }

    public static com.google.gwt.event.shared.GwtEvent.Type<EntryUpdatedEventHandler> getType() {
        if (type == null) {
            type = new Type<EntryUpdatedEventHandler>();
        }
        return type;
    }

    @Override
    protected void dispatch(EntryUpdatedEventHandler handler) {
        handler.onEntryUpdated(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EntryUpdatedEventHandler> getAssociatedType() {
        return getType();
    }

}
