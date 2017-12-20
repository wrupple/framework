package com.wrupple.muba.desktop.client.activity.widgets.toolbar;

import com.wrupple.muba.bpm.client.activity.widget.Toolbar;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;

/**
 * A filter view is a GUI object that portrais a manner for the System to get
 * input on the User's deepest and darkest searching urges.
 * 
 * There is usually one {@link FieldFilterViewProvider} contained in the
 * {@link FilterToolbar} assosiated with each filterable field of a catalog with
 * the FilterView providing a model on how to constrain the possible outcomes of
 * one or more fields of the query results.
 * 
 * 
 * @author japi
 * 
 */
public interface FilterToolbar extends Toolbar {
	String TOOLBAR_NAME = "filterToolbar";

    void addCriteria(FilterCriteria criteria, FieldDescriptor field, boolean fireEvents);

    void setModelAlterationTarget(String attribute);

    void setHideNewCriteriaButton(String attribute);

    void forceRefreshFromUserData();

	void setHideOperatorSymbol(String s);

	void setLabel(String s);

	void setVisibleFields(String s);

}
