package com.wrupple.muba.desktop.client.activity.widgets;

import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.worker.client.activity.process.state.HumanTask;

import java.util.List;

public interface CatalogExportView extends HumanTask<List<JsCatalogEntry>, Void> {
	
}
