package com.wrupple.muba.desktop.client.activity.process;
import java.util.List;

import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.desktop.domain.DesktopPlace;
public interface ImportProcess<O> extends Process<DesktopPlace, List<O>> {

}
