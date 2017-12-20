package com.wrupple.muba.bpm.domain;

public interface Application extends Job, Workflow {

    String CATALOG = "Application";


    Long getPeer();
}
