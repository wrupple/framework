package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.Task;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.bpm.domain.WorkCompleteEvent;
import com.wrupple.muba.event.domain.ExplicitIntentImpl;

import java.util.Date;

public class WorkCompleteEventImpl extends ExplicitIntentImpl implements WorkCompleteEvent {
    private Workflow handleValue;
    private Task taskDescriptorValue;
    private Long stakeHolder;
    private Date timestamp;

    @Override
    public Workflow getHandleValue() {
        return handleValue;
    }

    public void setHandleValue(Workflow handleValue) {
        this.handleValue = handleValue;
    }

    @Override
    public Task getTaskDescriptorValue() {
        return taskDescriptorValue;
    }

    public void setTaskDescriptorValue(Task taskDescriptorValue) {
        this.taskDescriptorValue = taskDescriptorValue;
    }

    @Override
    public Long getStakeHolder() {
        return stakeHolder;
    }

    @Override
    public void setStakeHolder(long stakeHolder) {
        this.stakeHolder=stakeHolder;
    }

    public void setStakeHolder(Long stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
