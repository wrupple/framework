package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.gwt.dom.client.StyleInjector;
import com.wrupple.muba.desktop.client.chain.command.StyleSheetImportCommand;
import com.wrupple.muba.desktop.domain.DependencyImportContext;

import java.util.List;

public class StyleSheetImportCommandImpl implements StyleSheetImportCommand {
    @Override
    public boolean execute(DependencyImportContext context) throws Exception {
        sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), WruppleDomainStyleSheet.CATALOG, GWTUtils.asStringList(sheets),
                new DataCallback<List<JsCatalogEntry>>() {

                    @Override
                    public void execute() {
                        loadedstyles = true;
                        testAndFinish();
                        for (JsCatalogEntry js : result) {
                            StyleInjector.inject(js.getStringValue());
                        }
                    }
                });

        return CONTINUE_PROCESSING;
    }
}
