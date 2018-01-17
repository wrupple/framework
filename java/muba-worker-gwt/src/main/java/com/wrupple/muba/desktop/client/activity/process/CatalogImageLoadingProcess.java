package com.wrupple.muba.desktop.client.activity.process;

import com.wrupple.muba.desktop.client.activity.widgets.DataInputView;
import com.wrupple.muba.desktop.domain.HasUserActions;
import com.wrupple.muba.worker.client.activity.process.state.HumanTask;
import com.wrupple.vegetate.domain.PersistentImageMetadata;

public interface CatalogImageLoadingProcess<V extends PersistentImageMetadata> extends HumanTask<V,V>,HasUserActions ,
Process<V, V> {
	DataInputView<V> getEditor();
}
