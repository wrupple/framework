package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.chain.ProblemPresenter;
import com.wrupple.muba.worker.shared.factory.dictionary.TransactionPanelMap;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;

import java.util.List;

public class ProblemPresenterImpl implements ProblemPresenter {



    /*
* Services
*/
    protected final ToolbarAssemblyDelegate assembly;


    @Inject
    public ProblemPresenterChainImpl(TransactionPanelMap transactionPanelMap, ToolbarAssemblyDelegate userInterfaceAssembler,
                                     TaskValueChangeListener valueChangeListener) {
        super();
        this.transactionPanelMap = transactionPanelMap;
        this.assembly = userInterfaceAssembler;
        wrapper = new SimpleLayoutPanel();
        wrapper.setStyleName("userInteractionState");
        this.valueChangeListener = valueChangeListener;
    }
    private final TransactionPanelMap transactionPanelMap;
    private final SimpleLayoutPanel wrapper;
    /**
     * used by selection transactions that may output an array of entries
     */
    public boolean userOutputIsArray = false;
    /*
     * State
     */
    protected JsProcessTaskDescriptor taskDescriptor;
    private String layoutUnit;
    private String transactionViewClass;
    private TaskValueChangeListener valueChangeListener;
    private String saveTo;

    {
        this.saveTo = task.getProducedField();
    }

    @Override
    public void delegate(ApplicationContext context, StateTransition<ApplicationContext> callback) {


        JsApplicationItem applicationItem = context.getApplicationItem().cast();

        // build main user intereaction widget
        HumanTaskProcessor<?, ?> transactionView = buildUserInteractionInterface(catalog, properties, context, eventBus, context);
        if (transactionViewClass != null) {
            transactionView.asWidget().addStyleName(transactionViewClass);
        }
        // build task content
        HumanTaskWindow panel = getContentPanel(properties, eventBus, context);
        panel.setUnit(layoutUnit);

        // logical attach
        panel.setMainTaskProcessor(transactionView);
        context.getNestedTaskPresenter().setTaskContent(panel);
        context.getNestedTaskPresenter().setUserInteractionTaskCallback(callback);

        // physical attach
        assembly.assebleToolbars(panel, transactionView, taskDescriptor, properties, context, eventBus, context, applicationItem);
        panel.setWidget(transactionView);
        // wrapper was attached to the task presenter during task start by
        // sequential process
        wrapper.setWidget(panel);
        transactionView.addValueChangeHandler(valueChangeListener);
        valueChangeListener.setContext(catalog, context, context, properties, eventBus);

        // transmit to user
        afterUIAssembled(catalog, applicationItem, transactionView, context, eventBus, context, context.getFilterData());
    }

    protected static class EntryUpdateCallback extends DataCallback<JsCatalogEntry> {

        JsTransactionApplicationContext context;
        StateTransition<JsTransactionApplicationContext> onDone;

        public EntryUpdateCallback(JsTransactionApplicationContext context, StateTransition<JsTransactionApplicationContext> onDone) {
            super();
            this.context = context;
            this.onDone = onDone;
        }

        @Override
        public void execute() {
            context.setTargetEntryId(result.getId());
            if (context.getTargetEntryId() == null) {
                throw new IllegalArgumentException("Commited entry setRuntimeContext no Id");
            }
            context.setUserOutput(result);
            onDone.setResultAndFinish(context);
        }

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


    protected <T extends JavaScriptObject> void afterUIAssembled(String catalog, JsApplicationItem applicationItem, HumanTaskProcessor<T, ?> transactionView,
                                                                 ProcessContextServices context, EventBus eventBus, JsTransactionApplicationContext parameter, JsFilterData filterData) {
        T taskValue = (T) parameter.getUserOutput();
        if (taskValue != null) {
            transactionView.setValue(taskValue);
        } else {
            GWT.log("assuming user-interface initialized someplace else");
        }
    }

    private native JsArrayString split(String savedRawKeys) /*-{
															return savedRawKeys.split(",");
															}-*/;

    private HumanTaskWindow getContentPanel(JavaScriptObject properties, EventBus eventBus, JsTransactionApplicationContext parameter) {
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

    class AutoSelectionCallback extends DataCallback {
        StateTransition<JsTransactionApplicationContext> onDone;
        private JsTransactionApplicationContext parameter;

        public AutoSelectionCallback(JsTransactionApplicationContext parameter, StateTransition<JsTransactionApplicationContext> onDone) {
            super();
            this.parameter = parameter;
            this.onDone = onDone;
        }

        @Override
        public void execute() {

            if (userOutputIsArray) {
                JsArray<JsCatalogEntry> arr = JsArrayList.unwrap((List<JsCatalogEntry>) result);
                parameter.setUserOutput(arr);
            } else {
                parameter.setUserOutput((JavaScriptObject) result);
            }
            parameter.setRecoveredOutput(true);
            onDone.setResultAndFinish(parameter);
        }

    }
}
