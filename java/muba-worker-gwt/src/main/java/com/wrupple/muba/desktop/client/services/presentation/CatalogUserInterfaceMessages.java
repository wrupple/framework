package com.wrupple.muba.desktop.client.services.presentation;


public interface CatalogUserInterfaceMessages {

	String delete();

	String deleteHelp();

	String create();

	String createHelp();

	String ok();

	String addNew();

	String update();

	String updateHelp();

	String createMore();

	String reset();

	String cancel();

	String chooseFile();
	
	String search();
	
	String fieldPickerHelp();

	String nullFieldIdentifier(String value);

	String fieldId();

	String fieldName();

	String browsableViewName(String catalogName);

	String detailViewName();

	String editViewName();

	String selectCatalogEntriesProcess(String foreignCatalog);

	String uploadFileProcess();

	String uploadImageProcess();

	String editingMultipleSingleLineTextProcess();

	String richTextEditingProcess();

	String importEntries();

	String exportEntries();

	String selectAll();
	
	String selectNonde();

	String selectWorkingCatalog();

	String optionDisabled();

	String filterAddStart();

	String selectParentEntry();

	String stringComparisonLike();

	String stringComparisonStartsWith();

	String stringComparisonEndsWith();

	String selectFromGallery();

	String authentication();
}