package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.Container;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.domain.Person;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.ForeignKey;

public class ContainerImpl extends ManagedObjectImpl implements Container {
    @ForeignKey(foreignCatalog = Host.CATALOG)
    private Long peer;
    @CatalogField(ignore = true)
    private Host peerValue;
    @ForeignKey(foreignCatalog = Person.CATALOG)
    private Long stakeHolder;
    @CatalogField(ignore = true)
    private Person stakeHolderValue;

    public Long getPeer() {
        return peer;
    }

    public void setPeer(Long peer) {
        this.peer = peer;
    }

    @Override
    public Host getPeerValue() {
        return peerValue;
    }

    public void setPeerValue(Host peerValue) {
        this.peerValue = peerValue;
    }

    @Override
    public Long getStakeHolder() {
        return stakeHolder;
    }

    @Override
    public void setStakeHolder(Long stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    @Override
    public Person getStakeHolderValue() {
        return stakeHolderValue;
    }

    public void setStakeHolderValue(Person stakeHolderValue) {
        this.stakeHolderValue = stakeHolderValue;
    }
}
