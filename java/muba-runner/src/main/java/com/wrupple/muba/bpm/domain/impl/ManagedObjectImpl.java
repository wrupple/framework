package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.ManagedObject;
import com.wrupple.muba.catalogs.domain.ContentNodeImpl;

/**
 * Created by japi on 29/07/17.
 */
public class ManagedObjectImpl extends ContentNodeImpl implements ManagedObject {
    private Long stakeHolder;

    @Override
    public Long getStakeHolder() {
        return stakeHolder;
    }

    public void setStakeHolder(Long stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    @Override
    public void setStakeHolder(long stakeHolder) {

        this.stakeHolder=stakeHolder;
    }
}
