package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.user.client.ui.FormPanel;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;

public interface MultipartFormActionUrlService {

	void setUploadUrl(FormPanel form) throws Exception;

	void getUrl(StateTransition<String> callback) throws Exception;

}
