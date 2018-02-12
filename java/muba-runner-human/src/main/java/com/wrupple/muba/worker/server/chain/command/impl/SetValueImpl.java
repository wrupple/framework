package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.worker.server.chain.command.SetValue;
import com.wrupple.muba.worker.shared.domain.HumanApplicationContext;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;

public class SetValueImpl implements SetValue{
    @Override
    public boolean execute(HumanApplicationContext context) throws Exception {
        return CONTINUE_PROCESSING;
    }


    protected <T extends JavaScriptObject> void afterUIAssembled(String catalog, JsApplicationItem applicationItem, HumanTaskProcessor<T, ?> transactionView,
                                                                 ProcessContextServices context, EventBus eventBus, JsTransactionApplicationContext parameter, JsFilterData filterData) {
        if (!disableBrowserInit) {
            T initialValue = null;
            boolean navigationflag = taskDescriptor.getCurrentPlaceNavigationFlag();
            if (navigationflag && applicationItem != null && catalog.equals(ApplicationItem.CATALOG)) {
                JsArray<JsApplicationItem> array = applicationItem.getChildItemsValuesArray();
                if (array != null) {
                    initialValue = array.cast();
                }
            } else {
                if (filterData == null) {
                    // TODO read filtervalue from configuration
                    initialValue = JsFilterData.newFilterData().cast();
                } else {
                    initialValue = filterData.cast();
                }
            }
            transactionView.setValue(initialValue);
        }
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
}
