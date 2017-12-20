package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.JSOAdapterCell.JSOAdapter;

import java.util.List;

public class StringListJSOadapter extends ListJSOadapter<String> implements JSOAdapter<List<String>>{

	public StringListJSOadapter() {
		super(StringJSOadapter.getInstance());
	}


}
