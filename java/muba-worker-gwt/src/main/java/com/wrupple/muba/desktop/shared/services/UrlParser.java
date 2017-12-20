package com.wrupple.muba.desktop.shared.services;

import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.domain.HasEntryId;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public interface UrlParser {

    String PRIVATE_CATALOG_ID_PARAMETER = "$" + HasCatalogId.FIELD;
    String PRIVATE_CATALOG_ENTRY_PARAMETER = "$" + HasEntryId.FIELD;
    String TOKEN_SEPARATOR = "/";
    // by default task define a catalogId as a static persistent property, if a
	// task has a variable catalog, then it must define custom set of tokens
    List<String> DEFAULT_FORM_TASK_TOKENS = Arrays.asList(CatalogActionRequest.CATALOG_ENTRY_PARAMETER);

    ApplicationItem getActivityItem(String activity, ApplicationItem root);

	ApplicationItem findNextTreeNode(ApplicationItem currentItem, ApplicationItem root);

    void getItemActivity(String[] tokens, int firstActivityIndex, ApplicationItem item, ApplicationItem root, StringBuilder builder);

    void printItemActivity(String[] tokens, int firstActivityIndex, ApplicationItem item, ApplicationItem root, PrintWriter printer);
}
