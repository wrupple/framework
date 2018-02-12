package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.worker.server.chain.command.AssembleInteractionPanel;
import com.wrupple.muba.worker.shared.domain.HumanApplicationContext;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;

public class AssembleInteractionPanelImpl implements AssembleInteractionPanel {
    @Override
    public boolean execute(HumanApplicationContext context) throws Exception {

        // build main user intereaction widget
        HumanTaskProcessor<Object> transactionView = context.getUserInteractionInterface();


        // build task content -> task window -> task container -> processWindow -> ContainerContext (CreateWorkerStructure:LaunchWorkerEngine)
        HumanTaskWindow panel = getContentPanel(properties, eventBus, context);
        panel.setUnit(layoutUnit);

        // logical attach
        panel.setMainTaskProcessor(transactionView);
        /* wrapper is the main panel square
          // wrapper was attached to the task presenter during task start by
        // sequential process
        wrapper.setWidget(panel);
         */
        context.getNestedTaskPresenter().setTaskContent(panel);
        context.getNestedTaskPresenter().setUserInteractionTaskCallback(callback);




        return CONTINUE_PROCESSING;
    }

    @Override
    public void setTaskDescriptor(JsProcessTaskDescriptor traskDescriptor) {
        if (traskDescriptor == null) {
            throw new IllegalArgumentException();
        }
        this.taskDescriptor = traskDescriptor;
        wrapper.addStyleName("userInteractionState-" + traskDescriptor.getTransactionType());
        wrapper.addStyleName("userInteractionState-" + traskDescriptor.getId());
    }




    private HumanTaskWindow getContentPanel(JavaScriptObject properties, EventBus eventBus, JsTransactionHumanApplicationContext parameter) {
        HumanTaskWindow panel;
        if (this.taskDescriptor.isKeepOutputFeature()) {
            panel = context.getActivityOutputFeature().getRootTaskPresenter().getTaskContent();
            if (panel == null) {
                panel = transactionPanelMap.getConfigured(properties, context, eventBus, parameter);
            }
        } else {
            panel = transactionPanelMap.getConfigured(properties, context, eventBus, parameter);
        }

        return panel;
    }
}
