package com.wrupple.muba.desktop.client.services.logic.impl;

import com.wrupple.muba.desktop.client.services.presentation.layout.IndexedLayoutDelegateImpl;
import com.wrupple.muba.desktop.client.services.presentation.layout.grid.RegularGridCellPositionerImpl;

public class IndexedGridLayoutDelegate extends IndexedLayoutDelegateImpl {

	public IndexedGridLayoutDelegate() {
		super(new RegularGridCellPositionerImpl(4, 175, 175));
	}

}
