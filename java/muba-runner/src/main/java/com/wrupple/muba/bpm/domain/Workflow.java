package com.wrupple.muba.bpm.domain;

import java.util.List;

import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.reserved.*;

/**
 *
 */
public interface Workflow extends ServiceManifest,HasOutput{
	
	public  final String CATALOG = "Workflow";

	public Long getPeer();

    public String getDescription();

    public List<Long> getDependencies();

    /**
     * usually sets a reference to the output of the application item on the output field and picks the next application item to invoke
     *
     * @return
     */
    String getExit/*Handler*/();

    public String getCancel();

    public String getError();

    //TODO input is deermined by service contract and first task's type, output by last task's type, maybe have an ephemeral field with these catalog id's

    List<Long> getProcess();

    <T extends ProcessTaskDescriptor> List<T> getProcessValues();

    /**
     *
     * @return explicitly links next activity to start when this finishes
     */
    Workflow getExplicitSuccessorValue();

    boolean isClearOutput();
}