package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.bpm.domain.Application;
import com.wrupple.muba.event.domain.Event;

import java.util.ListIterator;

public interface LaunchWorker extends Event {

    String CATALOG = "LaunchWorker";

    Application getHomeApplicationValue();
}
