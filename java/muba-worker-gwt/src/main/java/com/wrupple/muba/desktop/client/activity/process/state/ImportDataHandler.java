package com.wrupple.muba.desktop.client.activity.process.state;

import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImportData;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

import java.util.List;

public interface ImportDataHandler extends State.ContextAware<ImportData, List<JsCatalogEntry>>{

}
