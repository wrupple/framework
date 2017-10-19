package com.wrupple.muba.event.domain.impl;


import com.wrupple.muba.event.domain.ManagedObject;

/**
 * Created by japi on 29/07/17.
 */
public class ManagedObjectImpl extends ContentNodeImpl implements ManagedObject {
    private Object stakeHolder;


    @Override
    public Object getStakeHolder() {
        return stakeHolder;
    }

    public void setStakeHolder(Object stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    public void setStakeHolder(Long stakeHolder) {

        this.stakeHolder=stakeHolder;
    }


}
