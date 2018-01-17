package com.wrupple.muba.worker.client.activity;

import com.google.gwt.activity.shared.Activity;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.worker.client.services.impl.DataCallback;

public interface SequentialActivity extends Activity {
    void getActivityProcess(DesktopPlace item, JsApplicationItem applicationItem, DataCallback<ActivityProcess> callback);

}
