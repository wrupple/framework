package com.wrupple.muba.bpm.client.activity;

import com.google.gwt.activity.shared.Activity;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;

public interface SequentialActivity extends Activity {
	public void getActivityProcess(DesktopPlace item,JsApplicationItem applicationItem, DataCallback<ActivityProcess> callback);
	
}
