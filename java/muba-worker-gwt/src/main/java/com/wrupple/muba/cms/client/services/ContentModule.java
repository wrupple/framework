package com.wrupple.muba.cms.client.services;

import com.google.gwt.inject.client.Ginjector;
import com.wrupple.muba.catalogs.client.services.CatalogModule;
import com.wrupple.muba.desktop.client.activity.*;
import com.wrupple.muba.desktop.client.activity.widgets.GenericCatalogEditor;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.AggregateContentTable;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.IndexedContentTable;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.WruppleFlowLayoutDataWidget;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.WruppleIndexedLayoutDataWidget;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.LayoutCatalogEditor;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.*;
import com.wrupple.muba.desktop.client.activity.widgets.panels.DockLayoutTransactionPanel;
import com.wrupple.muba.desktop.client.activity.widgets.panels.HTMLTransactionPanel;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.*;
import com.wrupple.muba.desktop.client.factory.dictionary.*;
import com.wrupple.muba.desktop.client.factory.help.TaskConfigurationAid;
import com.wrupple.muba.desktop.client.factory.help.TaskToolbarAid;
import com.wrupple.muba.desktop.client.services.command.ExplicitOutputPlace;
import com.wrupple.muba.desktop.client.services.logic.HistogramLayoutDelegate;
import com.wrupple.muba.desktop.client.services.logic.impl.*;
import com.wrupple.muba.desktop.client.services.presentation.TableLayoutDelegate;
import com.wrupple.muba.desktop.client.services.presentation.impl.*;

public interface ContentModule extends CatalogModule, Ginjector {

	/*
	 * Activities
	 */
	CatalogSelectionActivity catalogSelectActivity();

	CatalogSelectionActivity catalogBrowsingActivity();

	CatalogEntryCreateActivity catalogCreationActivity();

	CatalogEntryReadActivity catalogViewingActivity();

	CatalogEntryUpdateActivity catalogWritingActivity();

	CatalogEntryImportActivity catalogImportActivity();

	/*
	 * Aid Providers
	 */
	TaskConfigurationAid taskAid();

	TaskToolbarAid toolbarAid();

	/*
	 * OutputHandlers
	 */

	ExplicitOutputPlace explicit();

	/*
	 * Dictionaries
	 */

	TransactionAssemblerMap transactionType();

	SelectionModelDictionary selectionModel();

	CatalogFieldMap fields();

	TransactionPanelMap transactionPanels();

	CatalogEditorMap editors();

	IndexedLayoutMap layoutDelegates();

	RelationshipFieldBrowserMap relationBrowsers();

	CatalogEntryBrowserMap browsers();

	ToolbarMap toolbars();

	ContentManagerMap cms();

	TableLayoutDelegate genericDataDisplayInitializer();

	GenericContentManager genericCMS();

	/*
	 * Transaction Handlers
	 */
	CreateTransactionAssembler newTransaction();

	ReadTransactionAssembler readTransaction();

	EditTransactionAssembler writeTransaction();

	SelectTransactionAssembler selectTransaction();

	/*
	 * TransactionPanels
	 */

	HTMLTransactionPanel HTML();

	DockLayoutTransactionPanel dock();

	/*
	 * Browsers
	 */

	IndexedContentTable tableBrowser();

	WruppleIndexedLayoutDataWidget layoutBrowser();

	WruppleFlowLayoutDataWidget flowBrowser();

	AggregateContentTable aggregateTableBrowser();

	/*
	 * Editors
	 */

	GenericCatalogEditor form();

	LayoutCatalogEditor layout();

	/*
	 * Relationship Browsers
	 */
	FlowBrowserHandle flowRelationBrowser();

	CellListHandle listRelationBrowser();
	
	ForeignSelectBoxHandle selectRelationBrowser();

	SliderRelationHandle sliderRelationBrowser();

	/*
	 * Indexed Layouts
	 */

	IndexedGridLayoutDelegate gridLayout();

	HistogramLayoutDelegate histogramLayout();

	/*
	 * CMS toolbars
	 */

	RulerToolbar rulerToolbar();

	ActionToolbar actionToolbar();

	CMSToolbar cmsToolbar();

	FilterToolbar filterToolbar();

	SelectionToolbar selectionToolbar();

	/*
	 * Selection Models
	 */
	MultipleSelectionModel multipleSelection();

	SingleSelectionModelImpl singleSelection();

	/*
	 * Data Cell providers
	 */

	MultiTextProvider fieldMultiText();

	RichTextFieldProvider fieldRichText();

	TextCellProvider fieldText();

	DateCellProvider fieldDate();

	StarRatingCellProvider rating();

	ListCellProvider fieldList();

	MapCellProvider fieldMap();

	ServicePickerCellProvider servicePicker();

	CheckBoxCellProvider fieldCheckBox();

	ColorPickerProvider fieldColor();

	// genericKey
	GenericKeyCellProvider genericKeyCell();

	// genericValue
	GenericValueCellProvider genericValueCell();

	CurrencyValueCellProvider currencyValueCell();

	ImageKeyCellProvider imageKey();

	ImageCellProvider image();

	CatalogIdAndEntryIdCellProvider catalogIdAndEntryId();

	NamedCatalogPickerProvider namedCatalogPicker();
}
