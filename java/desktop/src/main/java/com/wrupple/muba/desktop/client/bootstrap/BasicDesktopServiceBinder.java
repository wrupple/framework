package com.wrupple.muba.desktop.client.bootstrap;

import javax.inject.Provider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.AbstractCommitUserTransactionImpl;
import com.wrupple.muba.bpm.client.activity.process.state.AbstractStartUserTransaction;
import com.wrupple.muba.bpm.client.activity.process.state.AbstractUserInteractionState;
import com.wrupple.muba.bpm.client.activity.process.state.CommitUserTransaction;
import com.wrupple.muba.bpm.client.activity.process.state.DesktopActivityMapper;
import com.wrupple.muba.bpm.client.activity.process.state.InitializeActivityContext;
import com.wrupple.muba.bpm.client.activity.process.state.MachineTask;
import com.wrupple.muba.bpm.client.activity.process.state.ReadNextPlace;
import com.wrupple.muba.bpm.client.activity.process.state.StartUserTransaction;
import com.wrupple.muba.bpm.client.activity.process.state.UserInteractionState;
import com.wrupple.muba.bpm.client.activity.process.state.impl.InitializeActivityContextImpl;
import com.wrupple.muba.bpm.client.activity.process.state.impl.MachineTaskImpl;
import com.wrupple.muba.bpm.client.activity.process.state.impl.ReadNextPlaceImpl;
import com.wrupple.muba.bpm.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.bpm.client.services.impl.TransactionalActivityAssemblyImpl;
import com.wrupple.muba.catalogs.client.services.ClientCatalogCacheManager;
import com.wrupple.muba.catalogs.client.services.StorageManagerImpl;
import com.wrupple.muba.catalogs.client.services.evaluation.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.client.services.evaluation.impl.WruppleCatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.client.services.impl.ClientCatalogCacheManagerImpl;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.GenericContentManager;
import com.wrupple.muba.cms.client.services.impl.GenericContentManagementSystem;
import com.wrupple.muba.cms.client.services.impl.GenericContentManagerImpl;
import com.wrupple.muba.desktop.client.activity.AdministrativeActionActivity;
import com.wrupple.muba.desktop.client.activity.CatalogEntryCreateActivity;
import com.wrupple.muba.desktop.client.activity.CatalogEntryImportActivity;
import com.wrupple.muba.desktop.client.activity.CatalogEntryReadActivity;
import com.wrupple.muba.desktop.client.activity.CatalogEntryUpdateActivity;
import com.wrupple.muba.desktop.client.activity.CatalogSelectionActivity;
import com.wrupple.muba.desktop.client.activity.DesktopTreeManagementActivity;
import com.wrupple.muba.desktop.client.activity.DomainRecoveryActivity;
import com.wrupple.muba.desktop.client.activity.NavigationActivity;
import com.wrupple.muba.desktop.client.activity.ProcessBuildingActivity;
import com.wrupple.muba.desktop.client.activity.impl.AdministrativeActionActivityImpl;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy;
import com.wrupple.muba.desktop.client.activity.impl.CatalogSelectionActivityImpl;
import com.wrupple.muba.desktop.client.activity.impl.ContentCreationActivity;
import com.wrupple.muba.desktop.client.activity.impl.ContentManagementActivity;
import com.wrupple.muba.desktop.client.activity.impl.ContentUpdateActivity;
import com.wrupple.muba.desktop.client.activity.impl.ContentViewingActivity;
import com.wrupple.muba.desktop.client.activity.impl.DesktopTreeManagementActivityImpl;
import com.wrupple.muba.desktop.client.activity.impl.DomainRecoveryActivityImpl;
import com.wrupple.muba.desktop.client.activity.impl.ProcessBuildingActivityImpl;
import com.wrupple.muba.desktop.client.activity.impl.SimpleNavigationActivity;
import com.wrupple.muba.desktop.client.activity.process.CanvasDrawingProcess;
import com.wrupple.muba.desktop.client.activity.process.CatalogTypeSelectionActivityProcess;
import com.wrupple.muba.desktop.client.activity.process.CatalogTypeSelectionProcess;
import com.wrupple.muba.desktop.client.activity.process.DesktopAuthenticationProcess;
import com.wrupple.muba.desktop.client.activity.process.impl.CanvasDrawingProcessImpl;
import com.wrupple.muba.desktop.client.activity.process.impl.CatalogTypeSelectionActivityProcessImpl;
import com.wrupple.muba.desktop.client.activity.process.impl.CatalogTypeSelectionProcessImpl;
import com.wrupple.muba.desktop.client.activity.process.impl.DesktopAuthenticationProcessImpl;
import com.wrupple.muba.desktop.client.activity.process.state.CatalogSelectionLoader;
import com.wrupple.muba.desktop.client.activity.process.state.CatalogTypeSelectionTask;
import com.wrupple.muba.desktop.client.activity.process.state.ContentLoadingState;
import com.wrupple.muba.desktop.client.activity.process.state.ImportDataHandler;
import com.wrupple.muba.desktop.client.activity.process.state.impl.CSVImportDataHandler;
import com.wrupple.muba.desktop.client.activity.process.state.impl.CatalogSelectionLoaderImpl;
import com.wrupple.muba.desktop.client.activity.process.state.impl.CatalogTypeSelectionTaskImpl;
import com.wrupple.muba.desktop.client.activity.process.state.impl.ContentLoadingStateImpl;
import com.wrupple.muba.desktop.client.activity.widgets.BreadcrumbToolbar;
import com.wrupple.muba.desktop.client.activity.widgets.CatalogExportView;
import com.wrupple.muba.desktop.client.activity.widgets.FilterOptionSelectionView;
import com.wrupple.muba.desktop.client.activity.widgets.GenericCatalogEditor;
import com.wrupple.muba.desktop.client.activity.widgets.HomeToolbarImpl;
import com.wrupple.muba.desktop.client.activity.widgets.ImportView;
import com.wrupple.muba.desktop.client.activity.widgets.RequestToolbar;
import com.wrupple.muba.desktop.client.activity.widgets.WruppleBreadcrumbToolbar;
import com.wrupple.muba.desktop.client.activity.widgets.WruppleRequestToolbar;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.WruppleTable;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.GenericCatalogEditorImpl;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates.ClickableElementTemplate;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.CAPTCHACellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.fields.providers.ReCAPTCHACellProvider;
import com.wrupple.muba.desktop.client.activity.widgets.impl.CatalogExportViewImpl;
import com.wrupple.muba.desktop.client.activity.widgets.impl.CatalogImportViewImpl;
import com.wrupple.muba.desktop.client.activity.widgets.impl.FieldFilterInteractionToken;
import com.wrupple.muba.desktop.client.activity.widgets.impl.FilterOptionSelectionViewImpl;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.FilterToolbar;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.FilterToolbarImpl;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.FilterToolbarImpl.FieldFilter;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.HomeToolbar;
import com.wrupple.muba.desktop.client.bootstrap.state.ReadDesktopMetadata;
import com.wrupple.muba.desktop.client.bootstrap.state.ReadDesktopMetadataImpl;
import com.wrupple.muba.desktop.client.factory.dictionary.ActivityPresenterMap;
import com.wrupple.muba.desktop.client.factory.dictionary.AggregateCanvasRendererMap;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEditorMap;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogFieldMap;
import com.wrupple.muba.desktop.client.factory.dictionary.ContentManagerMap;
import com.wrupple.muba.desktop.client.factory.dictionary.DictionaryRegistry;
import com.wrupple.muba.desktop.client.factory.dictionary.ExternalAPILoaderMap;
import com.wrupple.muba.desktop.client.factory.dictionary.IndexedLayoutMap;
import com.wrupple.muba.desktop.client.factory.dictionary.OutputHandlerMap;
import com.wrupple.muba.desktop.client.factory.dictionary.RelationshipFieldBrowserMap;
import com.wrupple.muba.desktop.client.factory.dictionary.SelectionModelDictionary;
import com.wrupple.muba.desktop.client.factory.dictionary.ServiceMap;
import com.wrupple.muba.desktop.client.factory.dictionary.ToolbarMap;
import com.wrupple.muba.desktop.client.factory.dictionary.TransactionAssemblerMap;
import com.wrupple.muba.desktop.client.factory.dictionary.TransactionPanelMap;
import com.wrupple.muba.desktop.client.factory.dictionary.UserAssistanceProviderMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.DictionaryMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleActivityPresenterMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleAggregateCanvasRendererMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleCatalogEditorMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleCatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleCatalogFieldMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleContentManagerMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleExternalAPILoaderMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleIndexedLayoutMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleOutputHandlerMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleRelationshipFieldBrowserMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleSelectionModelMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleServiceMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleToolbarMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleTransactionAssemblerMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleTransactionPanelMap;
import com.wrupple.muba.desktop.client.factory.dictionary.impl.WruppleUserAssistanceProviderMap;
import com.wrupple.muba.desktop.client.factory.help.ActionAidProvider;
import com.wrupple.muba.desktop.client.factory.help.TaskConfigurationAid;
import com.wrupple.muba.desktop.client.factory.help.TaskToolbarAid;
import com.wrupple.muba.desktop.client.factory.help.TriggerAidProvider;
import com.wrupple.muba.desktop.client.factory.help.impl.ActionAidProviderImpl;
import com.wrupple.muba.desktop.client.factory.help.impl.TaskConfigurationAidImpl;
import com.wrupple.muba.desktop.client.factory.help.impl.TaskToolbarAidImpl;
import com.wrupple.muba.desktop.client.factory.help.impl.TriggerAidProviderImpl;
import com.wrupple.muba.desktop.client.services.command.CatalogCommandService;
import com.wrupple.muba.desktop.client.services.command.CommitCommand;
import com.wrupple.muba.desktop.client.services.command.CurrentPlaceCommand;
import com.wrupple.muba.desktop.client.services.command.ExplicitOutputPlace;
import com.wrupple.muba.desktop.client.services.command.GoToCommand;
import com.wrupple.muba.desktop.client.services.command.HistoryBackCommand;
import com.wrupple.muba.desktop.client.services.command.InterruptActivity;
import com.wrupple.muba.desktop.client.services.command.NextPlace;
import com.wrupple.muba.desktop.client.services.command.impl.CatalogCommandServiceImpl;
import com.wrupple.muba.desktop.client.services.command.impl.CommitCommandImpl;
import com.wrupple.muba.desktop.client.services.command.impl.CurrentPlaceCommandImpl;
import com.wrupple.muba.desktop.client.services.command.impl.ExplicitOutputPlaceImpl;
import com.wrupple.muba.desktop.client.services.command.impl.GoToCommandImpl;
import com.wrupple.muba.desktop.client.services.command.impl.HistoryBackCommandImpl;
import com.wrupple.muba.desktop.client.services.command.impl.InterruptActivityImpl;
import com.wrupple.muba.desktop.client.services.command.impl.NextPlaceImpl;
import com.wrupple.muba.desktop.client.services.command.impl.ProcessSwitchCommandImpl;
import com.wrupple.muba.desktop.client.services.logic.CSVParser;
import com.wrupple.muba.desktop.client.services.logic.CatalogCache;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.ConfigurationConstants;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.desktop.client.services.logic.FilterCriteriaFieldDelegate;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.logic.GenericFieldFactory;
import com.wrupple.muba.desktop.client.services.logic.HistogramDataExtractor;
import com.wrupple.muba.desktop.client.services.logic.HistogramLayoutDelegate;
import com.wrupple.muba.desktop.client.services.logic.HistogramPositioner;
import com.wrupple.muba.desktop.client.services.logic.ModifyUserInteractionStateModelCommand;
import com.wrupple.muba.desktop.client.services.logic.MubaServiceBus;
import com.wrupple.muba.desktop.client.services.logic.MultipartFormActionUrlService;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessSwitchCommand;
import com.wrupple.muba.desktop.client.services.logic.ServiceBus;
import com.wrupple.muba.desktop.client.services.logic.TaskValueChangeListener;
import com.wrupple.muba.desktop.client.services.logic.URLFilterDataSerializationService;
import com.wrupple.muba.desktop.client.services.logic.impl.BPMValueChangeListenerImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.CSVParserImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.CSVWriterImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.CatalogEntryKeyProviderImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.CatalogPlaceInterpretImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.ConfigurationConstantsImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.DesktopActivityMapperImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.DesktopPlaceHistoryMapperImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.GWTFieldConversionStrategyImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.FilterCriteriaFieldDelegateImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.GenericFieldFactoryImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.GenericTableLayoutDelegate;
import com.wrupple.muba.desktop.client.services.logic.impl.HistogramDataExtractorImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.HistogramLayoutDelegateImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.HistogramPositionerImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.ModifyUserInteractionStateModelCommandImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.SequentialProcessManager;
import com.wrupple.muba.desktop.client.services.logic.impl.ToolbarAssemblyDelegateImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.URLFilterDataSerializationServiceImpl;
import com.wrupple.muba.desktop.client.services.logic.impl.WruppleMultipartFormActionUrlService;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.client.services.presentation.ContentStyleDelegate;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;
import com.wrupple.muba.desktop.client.services.presentation.ModifyUserInteractionStatePanelCommand;
import com.wrupple.muba.desktop.client.services.presentation.TableLayoutDelegate;
import com.wrupple.muba.desktop.client.services.presentation.ToolbarAssemblyDelegate;
import com.wrupple.muba.desktop.client.services.presentation.WruppleButtonTemplate;
import com.wrupple.muba.desktop.client.services.presentation.impl.DesktopManagerImpl;
import com.wrupple.muba.desktop.client.services.presentation.impl.GenericDataProviderImpl;
import com.wrupple.muba.desktop.client.services.presentation.impl.ModifyUserInteractionStatePanelCommandImpl;
import com.wrupple.muba.desktop.client.services.presentation.impl.WruppleValueDependableStyleDelegateImpl;
import com.wrupple.muba.desktop.client.services.presentation.layout.CellPositioner;
import com.wrupple.muba.desktop.client.services.presentation.layout.IndexedLayoutDelegate;
import com.wrupple.muba.desktop.client.services.presentation.layout.IndexedLayoutDelegateImpl;
import com.wrupple.muba.desktop.client.services.presentation.layout.grid.RegularGridCellPositionerImpl;
import com.wrupple.muba.desktop.client.services.presentation.wruppleTheme.WruppleCatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.client.services.presentation.wruppleTheme.WruppleDesktopTheme;
import com.wrupple.muba.desktop.domain.overlay.JsMemoryCache;
import com.wrupple.muba.desktop.shared.services.BucketSplittingStrategy;
import com.wrupple.muba.desktop.shared.services.CSVWriter;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.muba.desktop.shared.services.HistogramDataProcessor;
import com.wrupple.muba.desktop.shared.services.StatisticsCalculator;
import com.wrupple.muba.desktop.shared.services.impl.AverageDifferenceBetweenElements;
import com.wrupple.muba.desktop.shared.services.impl.FieldDescriptionServiceImpl;
import com.wrupple.muba.desktop.shared.services.impl.HistogramDataProcessorImpl;
import com.wrupple.muba.desktop.shared.services.impl.MubaStatisticsCalculator;
import com.wrupple.vegetate.client.services.CatalogEntryAssembler;
import com.wrupple.vegetate.client.services.CatalogServiceSerializer;
import com.wrupple.vegetate.client.services.LocalWebStorageUnit;
import com.wrupple.vegetate.client.services.RemoteStorageUnit;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.client.services.impl.CatalogEntryAssemblerImpl;
import com.wrupple.vegetate.client.services.impl.CatalogServiceSerializerImpl;
import com.wrupple.vegetate.client.services.impl.LocalWebStorageUnitImpl;
import com.wrupple.vegetate.client.services.impl.VegetateStorageUnitImpl;

public class BasicDesktopServiceBinder extends AbstractGinModule {
	
	public static class TableStyleProvider implements Provider<Resources> {
		/*
		 * FIXME always use data grid, deprecate cell table, and do data grid
		 * Resources
		 */
		public interface WruppleTableResources extends Resources {
			/**
			 * The styles used in this widget.
			 */
			@Override
			@Source("WruppleTable.css")
			WruppleTable cellTableStyle();
		}

		@Override
		public Resources get() {
			Resources regreso = GWT.create(WruppleTableResources.class);
			return regreso;
		}
	}

	public static class CatalogCacheProvider implements Provider<CatalogCache>{

		@Override
		public CatalogCache get() {
			JsMemoryCache cache = JavaScriptObject.createObject().cast();
			return cache;
		}
		
	}
	

	public static class EventBusProvider implements Provider<EventBus> {

		@Override
		public EventBus get() {
			EventBus regreso = new ResettableEventBus(new SimpleEventBus());
			return regreso;
		}

	}

	public static class LegacyEventBusProvider implements Provider<com.google.gwt.event.shared.EventBus> {

		EventBus eventBus;

		@Inject
		public LegacyEventBusProvider(EventBus eventBus) {
			super();
			this.eventBus = eventBus;
		}

		@Override
		public com.google.gwt.event.shared.EventBus get() {
			return (ResettableEventBus) eventBus;
		}

	}

	public static class PlaceControllerProvider implements Provider<PlaceController> {

		EventBus eventBus;

		@Inject
		public PlaceControllerProvider(EventBus eventBus) {
			super();
			this.eventBus = eventBus;
		}

		@Override
		public PlaceController get() {
			PlaceController regreso = new PlaceController(eventBus);
			return regreso;
		}

	}
	
	/* SERVICE MAPS
	 */
	
	public static class FieldMapProvider implements Provider<CatalogFieldMap> {

		@Override
		public CatalogFieldMap get() {
			WruppleCatalogFieldMap regreso = GWT.create(WruppleCatalogFieldMap.class);
			return regreso;
		}

	}

	public static class ExternalAPILoaderMapProvider implements Provider<ExternalAPILoaderMap> {

		@Override
		public ExternalAPILoaderMap get() {
			WruppleExternalAPILoaderMap regreso = GWT.create(WruppleExternalAPILoaderMap.class);
			return regreso;
		}

	}

	public static class OutputHandlerMapProvider implements Provider<OutputHandlerMap> {

		@Override
		public OutputHandlerMap get() {
			OutputHandlerMap regreso = GWT.create(WruppleOutputHandlerMap.class);
			return regreso;
		}

	}

	public static class ServiceMapProvider implements Provider<ServiceMap> {

		@Override
		public ServiceMap get() {
			ServiceMap regreso = GWT.create(WruppleServiceMap.class);
			return regreso;
		}

	}

	public static class EditorMapProvider implements Provider<CatalogEditorMap> {

		@Override
		public CatalogEditorMap get() {
			CatalogEditorMap regreso = GWT.create(WruppleCatalogEditorMap.class);
			return regreso;
		}

	}

	public static class TransactionPanelMapProvider implements Provider<TransactionPanelMap> {

		@Override
		public TransactionPanelMap get() {
			WruppleTransactionPanelMap regreso = GWT.create(WruppleTransactionPanelMap.class);
			return regreso;
		}

	}

	public static class IndexedLayoutMapProvider implements Provider<IndexedLayoutMap> {

		@Override
		public IndexedLayoutMap get() {
			WruppleIndexedLayoutMap regreso = GWT.create(WruppleIndexedLayoutMap.class);
			return regreso;
		}
	}

	public static class AggregateCanvasRendererMapProvider implements Provider<AggregateCanvasRendererMap> {

		@Override
		public AggregateCanvasRendererMap get() {
			AggregateCanvasRendererMap regreso = GWT.create(WruppleAggregateCanvasRendererMap.class);
			return regreso;
		}

	}

	public static class RelationshipFieldBrowserMapProvider implements Provider<RelationshipFieldBrowserMap> {

		@Override
		public RelationshipFieldBrowserMap get() {
			RelationshipFieldBrowserMap regreso = GWT.create(WruppleRelationshipFieldBrowserMap.class);
			return regreso;
		}

	}

	public static class CatalogEntryBrowserMapProvider implements Provider<CatalogEntryBrowserMap> {

		@Override
		public CatalogEntryBrowserMap get() {
			CatalogEntryBrowserMap regreso = GWT.create(WruppleCatalogEntryBrowserMap.class);
			return regreso;
		}

	}

	public static class ToolbarMapProvider implements Provider<ToolbarMap> {

		@Override
		public ToolbarMap get() {
			ToolbarMap regreso = GWT.create(WruppleToolbarMap.class);
			return regreso;
		}

	}

	public static class ServiceRegistryProvider implements Provider<DictionaryRegistry> {

		@Override
		public DictionaryRegistry get() {
			DictionaryRegistry regreso = GWT.create(DictionaryMap.class);
			return regreso;
		}

	}

	public static class TransactionAssemblerMapProvider implements Provider<TransactionAssemblerMap> {

		@Override
		public TransactionAssemblerMap get() {
			TransactionAssemblerMap regreso = GWT.create(WruppleTransactionAssemblerMap.class);
			return regreso;
		}

	}

	public static class ContentManagerMapProvider implements Provider<ContentManagerMap> {

		@Override
		public ContentManagerMap get() {
			WruppleContentManagerMap regreso = GWT.create(WruppleContentManagerMap.class);
			return regreso;
		}

	}

	public static class SelectionModelMapProvider implements Provider<SelectionModelDictionary> {

		@Override
		public SelectionModelDictionary get() {
			WruppleSelectionModelMap regreso = GWT.create(WruppleSelectionModelMap.class);
			return regreso;
		}

	}


	public static class ActivityPResenterMapProvider implements Provider<ActivityPresenterMap> {

		@Override
		public ActivityPresenterMap get() {
			WruppleActivityPresenterMap regreso = GWT.create(WruppleActivityPresenterMap.class);
			return regreso;
		}

	}

	public static class UserAssistanceProviderMapProvider implements Provider<UserAssistanceProviderMap> {

		@Override
		public UserAssistanceProviderMap get() {
			WruppleUserAssistanceProviderMap regreso = GWT.create(WruppleUserAssistanceProviderMap.class);
			return regreso;
		}

	}
	
	@Override
	protected void configure() {
		
		/*
		 * Basic Shared Desktop Services
		 * 
		 */
		bind(ReadDesktopMetadata.class).to(ReadDesktopMetadataImpl.class);
		bind(ProcessManager.class).to(
				SequentialProcessManager.class);
		/*
		 * Desktop Structure
		 */
		bind(DesktopActivityMapper.class).to(DesktopActivityMapperImpl.class).in(Singleton.class);
		bind(PlaceHistoryMapper.class).to(DesktopPlaceHistoryMapperImpl.class).in(Singleton.class);
		
		/*
		 * SERVICE MAPS
		 */
		bind(UserAssistanceProviderMap.class).toProvider(UserAssistanceProviderMapProvider.class).in(Singleton.class);
		bind(ExternalAPILoaderMap.class).toProvider(ExternalAPILoaderMapProvider.class).in(Singleton.class);
		bind(ActivityPresenterMap.class).toProvider(ActivityPResenterMapProvider.class).in(Singleton.class);
		bind(TransactionAssemblerMap.class).toProvider(TransactionAssemblerMapProvider.class).in(Singleton.class);
		bind(SelectionModelDictionary.class).toProvider(SelectionModelMapProvider.class).in(Singleton.class);
		bind(ContentManagerMap.class).toProvider(ContentManagerMapProvider.class).in(Singleton.class);
		bind(CatalogFieldMap.class).toProvider(FieldMapProvider.class).in(Singleton.class);
		bind(TransactionPanelMap.class).toProvider(TransactionPanelMapProvider.class).in(Singleton.class);
		bind(CatalogEditorMap.class).toProvider(EditorMapProvider.class).in(Singleton.class);
		bind(IndexedLayoutMap.class).toProvider(IndexedLayoutMapProvider.class).in(Singleton.class);
		bind(OutputHandlerMap.class).toProvider(OutputHandlerMapProvider.class).in(Singleton.class);
		bind(ServiceMap.class).toProvider(ServiceMapProvider.class).in(Singleton.class);
		bind(AggregateCanvasRendererMap.class).toProvider(AggregateCanvasRendererMapProvider.class).in(Singleton.class);
		bind(RelationshipFieldBrowserMap.class).toProvider(RelationshipFieldBrowserMapProvider.class).in(Singleton.class);
		bind(CatalogEntryBrowserMap.class).toProvider(CatalogEntryBrowserMapProvider.class).in(Singleton.class);
		bind(ToolbarMap.class).toProvider(ToolbarMapProvider.class).in(Singleton.class);
		bind(DictionaryRegistry.class).toProvider(ServiceRegistryProvider.class).in(Singleton.class);
		
		
		/*
		 * Services
		 */
		bind(TaskValueChangeListener.class).to(BPMValueChangeListenerImpl.class);
		bind(CAPTCHACellProvider.class).to(ReCAPTCHACellProvider.class).in(Singleton.class);
		bind(ClientCatalogCacheManager.class).to(ClientCatalogCacheManagerImpl.class).in(Singleton.class);
		bind(CatalogCache.class).toProvider(CatalogCacheProvider.class);

		// bind(ActivityPresenter.class).to(SingleTaskActivityPresenter.class).in(Singleton.class);
		bind(ServiceBus.class).to(MubaServiceBus.class).in(Singleton.class);
		bind(DesktopManager.class).to(DesktopManagerImpl.class).in(Singleton.class);
		bind(EventBus.class).toProvider(EventBusProvider.class).in(Singleton.class);
		bind(com.google.gwt.event.shared.EventBus.class).toProvider(LegacyEventBusProvider.class).in(Singleton.class);
		bind(PlaceController.class).toProvider(PlaceControllerProvider.class).in(Singleton.class);
		bind(ContentManagementSystem.class).to(GenericContentManagementSystem.class);
		bind(HomeToolbar.class).to(HomeToolbarImpl.class).in(Singleton.class);
		bind(CatalogEvaluationDelegate.class).to(WruppleCatalogEvaluationDelegate.class);
		bind(LocalWebStorageUnit.class).to(LocalWebStorageUnitImpl.class);
		bind(RemoteStorageUnit.class).to(VegetateStorageUnitImpl.class);
		/*
		 * TEMPLATING
		 */
		bind(ImageTemplate.class).to(WruppleImageTemplate.class);
		bind(ClickableElementTemplate.class).to(WruppleButtonTemplate.class);
		bind(CatalogUserInterfaceMessages.class).to(WruppleCatalogUserInterfaceMessages.class);
		bind(DesktopTheme.class).to(WruppleDesktopTheme.class);
		
		/*
		 * Activities
		 */
		bind(CatalogSelectionActivity.class).to(ContentManagementActivity.class);
		bind(CatalogSelectionActivity.class).to(CatalogSelectionActivityImpl.class);
		bind(CatalogEntryCreateActivity.class).to(ContentCreationActivity.class);
		bind(CatalogEntryReadActivity.class).to(ContentViewingActivity.class);
		bind(CatalogEntryUpdateActivity.class).to(ContentUpdateActivity.class);
		bind(CatalogEntryImportActivity.class).to(CSVImportActiviy.class);
		bind(NavigationActivity.class).to(SimpleNavigationActivity.class);
		bind(AdministrativeActionActivity.class).to(AdministrativeActionActivityImpl.class);
		bind(ProcessBuildingActivity.class).to(ProcessBuildingActivityImpl.class);
		bind(DesktopTreeManagementActivity.class).to(DesktopTreeManagementActivityImpl.class);
		bind(DomainRecoveryActivity.class).to(DomainRecoveryActivityImpl.class);
		// States
		bind(ContentLoadingState.class).to(ContentLoadingStateImpl.class);
		bind(ReadNextPlace.class).to(ReadNextPlaceImpl.class);
		bind(InitializeActivityContext.class).to(InitializeActivityContextImpl.class);
		bind(MachineTask.class).to(MachineTaskImpl.class);
		bind(StartUserTransaction.class).to(AbstractStartUserTransaction.class);
		bind(CommitUserTransaction.class).to(AbstractCommitUserTransactionImpl.class);
		bind(UserInteractionState.class).to(AbstractUserInteractionState.class);
		bind(CatalogTypeSelectionProcess.class).to(CatalogTypeSelectionProcessImpl.class);
		bind(CatalogTypeSelectionActivityProcess.class).to(CatalogTypeSelectionActivityProcessImpl.class);


		// commands
		bind(CatalogCommandService.class).to(CatalogCommandServiceImpl.class).in(Singleton.class);
		bind(GoToCommand.class).to(GoToCommandImpl.class);
		bind(NextPlace.class).to(NextPlaceImpl.class);
		bind(ExplicitOutputPlace.class).to(ExplicitOutputPlaceImpl.class);
		bind(CurrentPlaceCommand.class).to(CurrentPlaceCommandImpl.class);
		bind(HistoryBackCommand.class).to(HistoryBackCommandImpl.class);
		bind(InterruptActivity.class).to(InterruptActivityImpl.class);
		bind(ModifyUserInteractionStateModelCommand.class).to(ModifyUserInteractionStateModelCommandImpl.class);
		bind(ModifyUserInteractionStatePanelCommand.class).to(ModifyUserInteractionStatePanelCommandImpl.class);
		bind(ProcessSwitchCommand.class).to(ProcessSwitchCommandImpl.class);
		bind(CommitCommand.class).to(CommitCommandImpl.class);

		/*
		 * SERVICES
		 */

		// and other module servicesextends
		bind(StorageManager.class).to(StorageManagerImpl.class);
		bind(CatalogEntryAssembler.class).to(CatalogEntryAssemblerImpl.class);
		// Data Type Metadata service
		// Data Type fields description service
		bind(CatalogSelectionLoader.class).to(CatalogSelectionLoaderImpl.class);
		bind(FieldDescriptionService.class).to(FieldDescriptionServiceImpl.class);
		bind(ContentStyleDelegate.class).to(WruppleValueDependableStyleDelegateImpl.class);
		// bind(GenericDataProvider.class).to(GenericDataProviderImpl.class);
		// <-- provides data without foreign joins
		bind(GenericDataProvider.class).to(GenericDataProviderImpl.class);

		// knows how to initialize the cells of a generic catalog table
		bind(TableLayoutDelegate.class).to(GenericTableLayoutDelegate.class);

		// know how to parse CSV (the default export format)
		bind(CSVWriter.class).to(CSVWriterImpl.class);
		bind(CSVParser.class).to(CSVParserImpl.class);
		bind(FieldConversionStrategy.class).to(GWTFieldConversionStrategyImpl.class);
		bind(URLFilterDataSerializationService.class).to(URLFilterDataSerializationServiceImpl.class);
		bind(CatalogServiceSerializer.class).to(CatalogServiceSerializerImpl.class);
		bind(CatalogEntryKeyProvider.class).to(CatalogEntryKeyProviderImpl.class);
		bind(GenericContentManager.class).to(GenericContentManagerImpl.class);
		bind(GenericFieldFactory.class).to(GenericFieldFactoryImpl.class).in(Singleton.class);
		bind(IndexedLayoutDelegate.class).to(IndexedLayoutDelegateImpl.class);
		bind(CellPositioner.class).to(RegularGridCellPositionerImpl.class);
		bind(StatisticsCalculator.class).to(MubaStatisticsCalculator.class);
		bind(HistogramLayoutDelegate.class).to(HistogramLayoutDelegateImpl.class);
		bind(HistogramPositioner.class).to(HistogramPositionerImpl.class);
		bind(HistogramDataProcessor.class).to(HistogramDataProcessorImpl.class);
		bind(HistogramDataExtractor.class).to(HistogramDataExtractorImpl.class);
		bind(BucketSplittingStrategy.class).to(AverageDifferenceBetweenElements.class);
		bind(MultipartFormActionUrlService.class).to(WruppleMultipartFormActionUrlService.class);
		bind(CatalogPlaceInterpret.class).to(CatalogPlaceInterpretImpl.class);
		bind(ImportDataHandler.class).to(CSVImportDataHandler.class);

		/*
		 * PRESENTATION SERVICES
		 */
		// user aid providers
		bind(TaskConfigurationAid.class).to(TaskConfigurationAidImpl.class);// not
																			// singletons
																			// please
		bind(TaskToolbarAid.class).to(TaskToolbarAidImpl.class);
		bind(ActionAidProvider.class).to(ActionAidProviderImpl.class);
		bind(TriggerAidProvider.class).to(TriggerAidProviderImpl.class);

		// assemblers
		bind(FilterCriteriaFieldDelegate.class).to(FilterCriteriaFieldDelegateImpl.class).in(Singleton.class);
		bind(TransactionalActivityAssembly.class).to(TransactionalActivityAssemblyImpl.class);
		bind(ToolbarAssemblyDelegate.class).to(ToolbarAssemblyDelegateImpl.class).in(Singleton.class);
		bind(ConfigurationConstants.class).to(ConfigurationConstantsImpl.class).in(Singleton.class);
		bind(FilterToolbar.class).to(FilterToolbarImpl.class);
		bind(GenericCatalogEditor.class).to(GenericCatalogEditorImpl.class);
		bind(BreadcrumbToolbar.class).to(WruppleBreadcrumbToolbar.class);
		bind(RequestToolbar.class).to(WruppleRequestToolbar.class);
		bind(FieldFilter.class).to(FieldFilterInteractionToken.class);

		bind(CatalogExportView.class).to(CatalogExportViewImpl.class);
		bind(ImportView.class).to(CatalogImportViewImpl.class);
		bind(CatalogTypeSelectionTask.class).to(CatalogTypeSelectionTaskImpl.class);
		bind(FilterOptionSelectionView.class).to(FilterOptionSelectionViewImpl.class);
		bind(Resources.class).toProvider(TableStyleProvider.class).in(Singleton.class);
		/*
		 * Activities
		 */
		bind(DesktopAuthenticationProcess.class).to(DesktopAuthenticationProcessImpl.class);
		bind(CanvasDrawingProcess.class).to(CanvasDrawingProcessImpl.class);
	}
	 interface WruppleImageTemplate extends ImageTemplate, SafeHtmlTemplates {
			
			//TODO replace this with a hand-writtend implementation so CatalogReadingChannel can ve injected and url building functionality used
			
			@Template("<img onclick=\"\" src=\"/vegetate/catalog/user/PersistentImageMetadata/read/{0}/"+TINY+"\" />")
			SafeHtml tinyImageOutput(String fileId);
			
			@Template("<img onclick=\"\" src=\"/vegetate/catalog/user/PersistentImageMetadata/read/{0}/"+SMALL+"\" />")
			SafeHtml smallImageOutput(String fileId);
			
			@Template("<img onclick=\"\" src=\"/vegetate/catalog/user/PersistentImageMetadata/read/{0}/"+THUMBNAIL+"\" />")
			SafeHtml thumbnailImageOutput(String fileId);
			
			@Template("<img onclick=\"\" src=\"/vegetate/catalog/user/PersistentImageMetadata/read/{0}/"+BIG+"\" />")
			SafeHtml bigImageOutput(String fileId);
			
			@Template("<img onclick=\"\" src=\"/vegetate/catalog/user/PersistentImageMetadata/read/{0}/image\" />")
			SafeHtml fullsizeImageOutput(String fileId);

			@Template("<img onclick=\"\" src=\"/static/img/no-image.png\" />")
			SafeHtml noImageOutput();
			
			@Template("<img onclick=\"\" src=\"{0}\" />")
			SafeHtml urlImageOutput(String staticImage);
			
			@Template("<img onclick=\"\" src=\"{0}\" />")
			SafeHtml urlImageOutput(SafeUri staticImageUri);
		}

}
