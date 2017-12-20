package com.wrupple.muba.desktop.client.services.presentation.wruppleTheme;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;

public interface WruppleDesktopTheme extends DesktopTheme, ClientBundle {
//fail attach, browse, create, delete,empty,export,homeButton,read,,send,update

	@Source("import.png")
	ImageResource imnport();
	
	@Source("read.png")
	ImageResource ok();
	
	ImageResource read();
	
	ImageResource edit();
	
	@Source("filter-icon.png")
	ImageResource filterIcon();

	@Source("filterBusy-icon.png")
	ImageResource filterBusyIcon();

	@Source("edit-clear.png")
	ImageResource editClear();

	@Source("edit-redo.png")
	ImageResource editRedo();

	@Source("edit-select-all.png")
	ImageResource editSelectAll();

	@Source("edit-undo.png")
	ImageResource editUndo();
	
	@Source("archive.png")
	ImageResource archive();

	@Source("archive-icon.png")
	ImageResource archiveIcon();

	@Source("catalog.png")
	ImageResource catalog();

	@Source("catalog-icon.png")
	ImageResource catalogIcon();
	
	

	ImageResource homeButton();

	ImageResource browse();

	ImageResource create();

	ImageResource delete();

	ImageResource export();

	
	
	ImageResource attach();
	

	ImageResource empty();
	
	ImageResource send();
	
}
