package com.wrupple.muba.desktop.shared.services.event;

import com.google.gwt.event.shared.GwtEvent;
import com.wrupple.muba.event.domain.CatalogKey;

public class EntryCreatedEvent extends GwtEvent<EntryCreatedEventHandler> {
    private static Type<EntryCreatedEventHandler> type;
    public final CatalogKey entry;
    public final String host, domain;

    public EntryCreatedEvent(CatalogKey result, String host, String domain) {
        this.entry = result;
        this.domain = domain;
        this.host = host;
    }

    public static com.google.gwt.event.shared.GwtEvent.Type<EntryCreatedEventHandler> getType() {
        if (type == null) {
            type = new Type<EntryCreatedEventHandler>();
        }
        return type;
    }

    @Override
    protected void dispatch(EntryCreatedEventHandler handler) {
        handler.onEntryCreated(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EntryCreatedEventHandler> getAssociatedType() {
        return getType();
    }

}
