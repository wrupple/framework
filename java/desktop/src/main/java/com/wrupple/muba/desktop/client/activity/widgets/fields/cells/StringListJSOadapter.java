package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import java.util.List;

import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.JSOAdapterCell.JSOAdapter;

public class StringListJSOadapter extends ListJSOadapter<String> implements JSOAdapter<List<String>>{

	public StringListJSOadapter() {
		super(StringJSOadapter.getInstance());
	}


}
