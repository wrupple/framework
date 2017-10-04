package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public class TabbedActivityPresenter extends ResizeComposite implements ProcessPresenter {

	private final TabLayoutPanel main;
	private final TaskPresenter output;

	@Inject
	public TabbedActivityPresenter() {
		main = new TabLayoutPanel(36, Unit.PX);
		this.output=createNewOutputFeature();
		initWidget(main);
	}

	class DetachModalPanel extends DataCallback<Void> {

		private Feature f;

		public DetachModalPanel(Feature f) {
			super();
			this.f = f;
		}

		@Override
		public void execute() {
			main.remove(f.asWidget());
		}

	}

	class RemoveHandler implements ClickHandler {
		StateTransition<Void> callback;
		private Feature f;

		public RemoveHandler(StateTransition<Void> callback, Feature f) {
			super();
			this.callback = callback;
			this.f = f;
		}

		@Override
		public void onClick(ClickEvent event) {
			main.remove(f.asWidget());
			callback.execute();
		}

	}
	
	
	@Override
	public TaskPresenter getRootTaskPresenter() {
		return output;
	}

	

	public class Feature implements IsWidget, TaskPresenter {
		Label label;
		SimpleLayoutPanel outputFeature;

		private ContentPanel userContent;
		private StateTransition<JsTransactionApplicationContext> serInteractionTaskCallback;

		public Feature() {
			label = new Label();
			outputFeature = new SimpleLayoutPanel();
		}

		@Override
		public void setWidget(IsWidget w) {
			outputFeature.setWidget(w);
		}

		@Override
		public Widget asWidget() {
			return outputFeature;
		}

		@Override
		public TaskPresenter spawnChild(StateTransition<Void> callback) {
			Feature f = createNewOutputFeature();
			Feature last = (Feature) this;
			last.label.addClickHandler(new RemoveHandler(callback, f));
			callback.hook(new DetachModalPanel(f));
			return f;
		}

		@Override
		public void setProcessName(String processName, String oldReplacedProcessName) {
			label.setText(processName);
		}

		@Override
		public ContentPanel getTaskContent() {
			return userContent;
		}

		@Override
		public void setTaskContent(ContentPanel panel) {
			this.userContent=panel;
		}

		@Override
		public void setUserInteractionTaskCallback(StateTransition<JsTransactionApplicationContext> onDone) {
			this.serInteractionTaskCallback=onDone;
		}

		@Override
		public StateTransition<JsTransactionApplicationContext> getUserInteractionTaskCallback() {
			return serInteractionTaskCallback;
		}

		@Override
		public void setUserContentClass(String processUserAreaClass) {
			outputFeature.setStyleName(processUserAreaClass);
		}


	}


	public Feature createNewOutputFeature() {
		Feature f = new Feature();
		main.add(f, f.label);
		return f;
	}



}
