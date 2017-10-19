package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.ApplicationState;
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
    private Object entry;
    private Object entryValue;

    @Override
    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    private Object state;
    private ApplicationState stateValue;

    @Override
    public Object getEntry() {
        return entry;
    }

    @Override
    public void setEntry(Object entry) {
        this.entry = entry;
    }

    @Override
    public Object getEntryValue() {
        return entryValue;
    }

    public void setEntryValue(Object entryValue) {
        this.entryValue = entryValue;
    }

    @Override
    public ApplicationState getStateValue() {
        return stateValue;
    }

    @Override
    public void setStateValue(ApplicationState stateValue) {
        this.stateValue = stateValue;
    }

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
    public void setStakeHolder(Object stakeHolder) {
        this.stakeHolder= (Long) stakeHolder;
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
