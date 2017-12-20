package com.wrupple.muba.desktop.client.activity.process.state;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.wrupple.muba.bpm.client.activity.process.state.HumanTask;
import com.wrupple.muba.catalogs.domain.CatalogProcessDescriptor;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImportData;

import java.util.List;

public interface ImportDataInputTask extends HumanTask<CatalogProcessDescriptor, ImportData>{

    void setAction(List<? extends HasClickHandlers> a);

}
