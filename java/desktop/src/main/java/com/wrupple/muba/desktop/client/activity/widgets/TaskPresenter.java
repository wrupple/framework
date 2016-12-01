package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface TaskPresenter extends AcceptsOneWidget{
	/**
	 *@param parent preceding output feature
	 * @param callback a transition to call when (if) the user exits modal nested state
	 * @return output feature where to present user interaction. If parent is not null, an output feature  modal to the parent will be returned.
	 */
	TaskPresenter spawnChild(StateTransition<Void> callback );
	
	void setProcessName(String processName, String oldReplacedProcessName);
	
	/**
	 * @return the object where all User interaction widgets ( main, and auxiliary, toolbars, etc.) are registered
	 */
	ContentPanel getTaskContent();
	/**
	 * @param panel this must be a child of whatever widget was attached by setWidget
	 */
	void setTaskContent(ContentPanel panel);
	

	void setUserInteractionTaskCallback(StateTransition<JsTransactionActivityContext> onDone);

	StateTransition<JsTransactionActivityContext> getUserInteractionTaskCallback();

	void setUserContentClass(String processUserAreaClass);

}