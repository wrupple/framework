package com.wrupple.muba.event.domain.impl;


import com.wrupple.muba.event.domain.ManagedObject;
import com.wrupple.muba.event.domain.Person;
import com.wrupple.muba.event.domain.annotations.ForeignKey;

/**
 * Created by japi on 29/07/17.
 */
public class ManagedObjectImpl extends ContentNodeImpl implements ManagedObject {

    @ForeignKey(foreignCatalog = Person.CATALOG)
    private Long stakeHolder;


    @Override
    public Long getStakeHolder() {
        return stakeHolder;
    }



    public void setStakeHolder(Long stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    @Override
    public void setStakeHolder(Object stakeHolder) {
        setStakeHolder((Long)stakeHolder);
    }


}
