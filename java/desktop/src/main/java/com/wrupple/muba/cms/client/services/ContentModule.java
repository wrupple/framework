package com.wrupple.muba.cms.client.services;

import com.google.gwt.inject.client.Ginjector;
import com.wrupple.muba.catalogs.client.services.CatalogModule;
import com.wrupple.muba.desktop.client.activity.CatalogEntryCreateActivity;
import com.wrupple.muba.desktop.client.activity.CatalogEntryImportActivity;
import com.wrupple.muba.desktop.client.activity.CatalogEntryReadActivity;
import com.wrupple.muba.desktop.client.activity.CatalogEntryUpdateActivity;
import com.wrupple.muba.desktop.client.activity.CatalogSelectionActivity;
import com.wrupple.muba.desktop.client.activity.widgets.GenericCatalogEditor;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.AggregateContentTable;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.IndexedContentTable;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.WruppleFlowLayoutDataWidget;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.WruppleIndexedLayoutDataWidget;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.LayoutCatalogEditor;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.CatalogIdAndEntryIdCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.CheckBoxCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.ColorPickerProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.CurrencyValueCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.DateCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.GenericKeyCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.GenericValueCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.ImageCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.ImageKeyCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.ListCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.MapCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.MultiTextProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.NamedCatalogPickerProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.RichTextFieldProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.ServicePickerCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.StarRatingCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.TextCellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.panels.DockLayoutTransactionPanel;
import com.wrupple.muba.desktop.client.activity.widgets.panels.HTMLTransactionPanel;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.ActionToolbar;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.CMSToolbar;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.FilterToolbar;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.RulerToolbar;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.SelectionToolbar;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEditorMap;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogFieldMap;
import com.wrupple.muba.desktop.client.factory.dictionary.ContentManagerMap;
import com.wrupple.muba.desktop.client.factory.dictionary.IndexedLayoutMap;
import com.wrupple.muba.desktop.client.factory.dictionary.RelationshipFieldBrowserMap;
import com.wrupple.muba.desktop.client.factory.dictionary.SelectionModelDictionary;
import com.wrupple.muba.desktop.client.factory.dictionary.ToolbarMap;
import com.wrupple.muba.desktop.client.factory.dictionary.TransactionAssemblerMap;
import com.wrupple.muba.desktop.client.factory.dictionary.TransactionPanelMap;
import com.wrupple.muba.desktop.client.factory.help.TaskConfigurationAid;
import com.wrupple.muba.desktop.client.factory.help.TaskToolbarAid;
import com.wrupple.muba.desktop.client.services.command.ExplicitOutputPlace;
import com.wrupple.muba.desktop.client.services.logic.HistogramLayoutDelegate;
import com.wrupple.muba.desktop.client.services.logic.impl.CreateTransactionAssembler;
import com.wrupple.muba.desktop.client.services.logic.impl.EditTransactionAssembler;
import com.wrupple.muba.desktop.client.services.logic.impl.IndexedGridLayoutDelegate;
import com.wrupple.muba.desktop.client.services.logic.impl.ReadTransactionAssembler;
import com.wrupple.muba.desktop.client.services.logic.impl.SelectTransactionAssembler;
import com.wrupple.muba.desktop.client.services.presentation.TableLayoutDelegate;
import com.wrupple.muba.desktop.client.services.presentation.impl.CellListHandle;
import com.wrupple.muba.desktop.client.services.presentation.impl.FlowBrowserHandle;
import com.wrupple.muba.desktop.client.services.presentation.impl.ForeignSelectBoxHandle;
import com.wrupple.muba.desktop.client.services.presentation.impl.MultipleSelectionModel;
import com.wrupple.muba.desktop.client.services.presentation.impl.SingleSelectionModelImpl;
import com.wrupple.muba.desktop.client.services.presentation.impl.SliderRelationHandle;

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
