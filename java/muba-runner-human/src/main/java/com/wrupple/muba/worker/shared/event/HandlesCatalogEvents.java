package com.wrupple.muba.worker.shared.event;

public interface HandlesCatalogEvents {


    void onEntryCreated(EntryCreatedEvent entryCreatedEvent);

    void onEntriesRetrived(EntriesRetrivedEvent e);

    void onEntryUpdated(EntryUpdatedEvent entryUpdatedEvent);

    void onEntriesDeleted(EntriesDeletedEvent entriesDeletedEvent);

}