package com.wrupple.muba.desktop.client.activity.process;

import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.desktop.domain.DesktopPlace;

import java.util.List;
public interface ImportProcess<O> extends Process<DesktopPlace, List<O>> {

}
