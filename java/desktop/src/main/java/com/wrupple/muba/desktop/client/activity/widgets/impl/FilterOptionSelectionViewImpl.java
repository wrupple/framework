package com.wrupple.muba.desktop.client.activity.widgets.impl;

import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.activity.widgets.FilterOptionSelectionView;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.FieldDescriptorCell;
import com.wrupple.vegetate.domain.FieldDescriptor;

public class FilterOptionSelectionViewImpl extends SimpleSelectionTask<FieldDescriptor> implements FilterOptionSelectionView {

	@Inject
	public FilterOptionSelectionViewImpl(FieldDescriptorCell cell) {
		super(cell, new SingleSelectionModel<FieldDescriptor>());
	}


}
