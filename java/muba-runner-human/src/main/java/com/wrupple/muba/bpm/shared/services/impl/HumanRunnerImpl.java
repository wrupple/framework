package com.wrupple.muba.bpm.shared.services.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.service.VariableEligibility;
import com.wrupple.muba.bpm.shared.services.HumanRunner;
import com.wrupple.muba.bpm.shared.services.HumanVariableEligibility;
import com.wrupple.muba.event.domain.FieldDescriptor;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class HumanRunnerImpl implements HumanRunner {

    private final Provider<HumanVariableEligibility> variableProvider;

    @Inject
    public HumanRunnerImpl(Provider<HumanVariableEligibility> variableProvider) {
        this.variableProvider = variableProvider;
    }

    @Override
    public boolean canHandle(FieldDescriptor field, ApplicationContext context) {
        return field.isWriteable();
    }

    @Override
    public VariableEligibility handleAsVariable(FieldDescriptor field, ApplicationContext context) {
        return variableProvider.get().of(field,context);
    }

    @Override
    public boolean solve(ApplicationContext context) {

        AcceptsOneWidget panel = context.getPanel()

        HelloView helloView = clientFactory.getHelloView();
        helloView.setName(name);
        helloView.setPresenter(this);
        containerWidget.setWidget(helloView.asWidget());


        DesktopPlace place = (DesktopPlace) pc.getWhere();
        if (!dm.isDesktopyConfigured()) {
            if (recoverFromMissconfiguredDesktop(place)) {
                return;
            }

        }

        JavaScriptObject o = dm.getApplicationItem(place);

        JsApplicationItem applicationItem;
        if (o == null) {
            applicationItem = null;
        } else {
            applicationItem = o.cast();
        }
        getActivityProcess(place, applicationItem, new SetApplicationStateAndContext(pm, panel, eventBus, applicationItem));


        return Command.CONTINUE_PROCESSING;
    }
}
