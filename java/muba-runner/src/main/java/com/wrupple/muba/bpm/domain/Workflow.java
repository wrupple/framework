package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.reserved.HasOutput;

import java.util.List;

/**
 *
 */
public interface Workflow extends ServiceManifest,HasOutput{

    String WORKFLOW_CATALOG = "Workflow";

    //TODO input is deermined by service contract and first task's type, output by last task's type, maybe have an ephemeral field with these catalog id's


    List<Long> getProcess();

    <T extends Task> List<T> getProcessValues();

    boolean isClearOutput();
}