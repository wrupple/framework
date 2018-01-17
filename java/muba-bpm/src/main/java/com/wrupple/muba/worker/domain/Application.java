package com.wrupple.muba.worker.domain;

import java.util.List;

public interface Application extends Job, Workflow {

    String CATALOG = "Application";

    List<Long> getDependencies();

    List<ApplicationDependency> getDependenciesValues();

    Long getPeer();

    String getRequiredRole();
}
