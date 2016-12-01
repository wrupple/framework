package com.wrupple.muba.desktop.client.activity.process.state;

import java.util.List;

import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImportData;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public interface ImportDataHandler extends State.ContextAware<ImportData, List<JsCatalogEntry>>{

}
