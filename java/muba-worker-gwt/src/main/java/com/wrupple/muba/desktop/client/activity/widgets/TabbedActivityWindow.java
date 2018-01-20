package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.client.widgets.TaskContainer;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;

public class TabbedActivityWindow extends ResizeComposite implements ProcessWindow {

    private final TabLayoutPanel main;
    private final TaskContainer output;

    @Inject
    public TabbedActivityWindow() {
        main = new TabLayoutPanel(36, Unit.PX);
        this.output = createNewOutputFeature();
        initWidget(main);
    }

    @Override
    public TaskContainer getRootTaskPresenter() {
        return output;
    }

    public Feature createNewOutputFeature() {
        Feature f = new Feature();
        main.add(f, f.label);
        return f;
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

    public class Feature implements IsWidget, TaskContainer {
        Label label;
        SimpleLayoutPanel outputFeature;

        private HumanTaskWindow userContent;
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
        public TaskContainer spawnChild(StateTransition<Void> callback) {
            Feature f = createNewOutputFeature();
            Feature last = this;
            last.label.addClickHandler(new RemoveHandler(callback, f));
            callback.hook(new DetachModalPanel(f));
            return f;
        }

        @Override
        public void setProcessName(String processName, String oldReplacedProcessName) {
            label.setText(processName);
        }

        @Override
        public HumanTaskWindow getTaskContent() {
            return userContent;
        }

        @Override
        public void setTaskContent(HumanTaskWindow panel) {
            this.userContent = panel;
        }

        @Override
        public StateTransition<JsTransactionApplicationContext> getUserInteractionTaskCallback() {
            return serInteractionTaskCallback;
        }

        @Override
        public void setUserInteractionTaskCallback(StateTransition<JsTransactionApplicationContext> onDone) {
            this.serInteractionTaskCallback = onDone;
        }

        @Override
        public void setUserContentClass(String processUserAreaClass) {
            outputFeature.setStyleName(processUserAreaClass);
        }


    }


}
