package com.wrupple.muba.desktop.client.activity.impl;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.wrupple.muba.catalogs.client.services.ClientCatalogCacheManager;
import com.wrupple.muba.catalogs.domain.CatalogProcessDescriptor;
import com.wrupple.muba.desktop.client.activity.CatalogEntryImportActivity;
import com.wrupple.muba.desktop.client.activity.process.ImportProcess;
import com.wrupple.muba.desktop.client.activity.process.state.CatalogSelectionLoader;
import com.wrupple.muba.desktop.client.activity.process.state.CatalogTypeSelectionTask;
import com.wrupple.muba.desktop.client.activity.process.state.ImportDataHandler;
import com.wrupple.muba.desktop.client.activity.process.state.impl.*;
import com.wrupple.muba.desktop.client.activity.widgets.ImportView;
import com.wrupple.muba.desktop.client.activity.widgets.impl.MultipleCatalogFileUpload.Value;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.*;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.worker.client.activity.ActivityProcess;
import com.wrupple.muba.worker.client.activity.SequentialActivity;
import com.wrupple.muba.worker.client.activity.process.impl.ActivityProcessImpl;
import com.wrupple.muba.worker.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.worker.client.activity.process.state.HumanTask;
import com.wrupple.muba.worker.client.activity.process.state.State;
import com.wrupple.muba.worker.client.services.impl.DataCallback;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.List;

public class CSVImportActiviy extends AbstractActivity implements CatalogEntryImportActivity, SequentialActivity {

	public final static class FieldColumnRelation extends JavaScriptObject {
		protected FieldColumnRelation() {
        }

        public native JsArrayString getPath()/*-{
            return this.path;
		}-*/;

		public native boolean isDiscriminative()/*-{
			if(this.discriminative==null){
				return false;
			}
			return this.discriminative;
		}-*/;

		public native void setDiscriminative(boolean discriminative)/*-{
			this.discriminative = discriminative;
		}-*/;

		public native void setPath(JsArrayString path) /*-{
			this.path = path;
		}-*/;

		public native void setColumn(int col) /*-{
			this.column = col;
		}-*/;

		public native int getColumn()/*-{
			return this.column;
		}-*/;
	}

	public static class ImportData {
		private String catalog;
		private final JsArray<JsArrayString> csv;
		private JsArray<FieldColumnRelation> fieldColumnRelation;

		public ImportData(JsArray<JsArrayString> csv) {
			super();
			this.csv = csv;
		}

		public JsArray<FieldColumnRelation> getFieldColumnRelation() {
			return fieldColumnRelation;
		}

		public void setFieldColumnRelation(JsArray<FieldColumnRelation> fieldColumnRelation) {
			this.fieldColumnRelation = fieldColumnRelation;
		}

		public JsArray<JsArrayString> getCsv() {
			return csv;
		}

		public JsArrayString getColumnNames() {
			return csv.get(0);
		}

		public String getCatalog() {
			return this.catalog;
		}

		public void setCatalog(String selectedType) {
			this.catalog = selectedType;
		}

		public void addImageFieldImportData(String field, JsArray<Value> imageIdtoFileName, String fieldNameMappedtoImageFilename) {
			JsArray<ImageFieldImportData> imageFieldImportData = getImageFieldImportData();
			ImageFieldImportData newone = ImageFieldImportData.createObject().cast();
			newone.setField(field);
			newone.setfieldNameMappedtoImageFilename(fieldNameMappedtoImageFilename);
			newone.setImageIdtoFileName(imageIdtoFileName);
			imageFieldImportData.push(newone);
		}

		public native JsArray<ImageFieldImportData> getImageFieldImportData() /*-{
			if (this.imageFieldImportData == null) {
				this.imageFieldImportData = [];
			}
			return this.imageFieldImportData;
		}-*/;

		public int getContextPathIndex() {
			return getContextPath()==null? 0 : getContextPath().length();
		}

		public native void setContextPath(JsArray<JsFieldDescriptor> context) /*-{
			this.contextPath = context;
		}-*/;

		public native JsArray<JsFieldDescriptor> getContextPath() /*-{
			return this.contextPath;
		}-*/;
	}

	public final static class ImageFieldImportData extends JavaScriptObject {
		protected ImageFieldImportData() {
		}

		public native JsArray<Value> getImageIdtoFileName()/*-{
			return this.imageIdtoFileName;
		}-*/;

		public native String getfieldNameMappedtoImageFilename()/*-{
			return this.fieldNameMappedtoImageFilename;
		}-*/;

		public native String getField()/*-{
			return this.field;
		}-*/;

		public native void setField(String field)/*-{
			this.field = field;
		}-*/;

		public native void setImageIdtoFileName(JsArray<Value> imageIdtoFileName)/*-{
			this.imageIdtoFileName = imageIdtoFileName;
		}-*/;

		public native void setfieldNameMappedtoImageFilename(String fieldNameMappedtoImageFilename)/*-{
			this.fieldNameMappedtoImageFilename = fieldNameMappedtoImageFilename;
		}-*/;
	}

	public static class SimpleImportProcess<O> extends SequentialProcess<DesktopPlace, List<O>> implements ImportProcess<O> {

		public SimpleImportProcess(CatalogPlaceInterpret interpret, HumanTask<CatalogProcessDescriptor, ImportData> readDataToImportFromSomewehre,
				HumanTask<ImportData, ImportData> columnFieldAssignation, HumanTask<ImportData, ImportData> imageFields, State<ImportData, ImportData> imageupdater,
				ImportDataHandler strategyToPersistAllData, Provider<CatalogTypeSelectionTask> typeSelection,
				Provider<CatalogSelectionLoader> selectionInterpretState) {
			super();
			add(new BrowsePlaceInterpretImpl(interpret, typeSelection, selectionInterpretState));
			add(readDataToImportFromSomewehre);
			add(columnFieldAssignation);
			add(imageFields);
			add(imageupdater);
			add(strategyToPersistAllData);
		}
	}

	StorageManager descriptionService;
	private CatalogPlaceInterpret interpret;
	private CSVParser parser;
	private ImportView view;
	private DesktopTheme theme;
	private Provider<CatalogTypeSelectionTask> typeSelection;
	private Provider<CatalogSelectionLoader> selectionInterpret;
	private Provider<ImportColumnFieldAssignation> fieldAssignationProvider;
	private ConfigurationConstants ef;
	private CatalogEntryBrowserMap ad;
	private MultipartFormActionUrlService uploadUrlService;
	private CatalogUserInterfaceMessages messages;
	private Provider<CsvRecordImportState> sateProvider;
	private ClientCatalogCacheManager ccm;

	@Inject
	public CSVImportActiviy( ClientCatalogCacheManager ccm,Provider<CsvRecordImportState> sateProvider, CatalogUserInterfaceMessages messages,
			MultipartFormActionUrlService uploadUrlService, Provider<ImportColumnFieldAssignation> fieldAssignationProvider, CatalogEntryBrowserMap ad,
			ConfigurationConstants ef, DesktopManager dm, StorageManager descriptionService,
			Provider<CatalogSelectionLoader> selectionInterpret, Provider<CatalogTypeSelectionTask> typeSelection, DesktopTheme theme,
			ProcessManager pm, PlaceController pc, CatalogPlaceInterpret interpret, CSVParser p, ImportView v) {
		super(dm, pm, pc);
		this.sateProvider = sateProvider;
		this.messages = messages;
		this.ccm=ccm;
		this.uploadUrlService = uploadUrlService;
		this.fieldAssignationProvider = fieldAssignationProvider;
		this.descriptionService = descriptionService;
		this.typeSelection = typeSelection;
		this.selectionInterpret = selectionInterpret;
		this.theme = theme;
		this.interpret = interpret;
		this.parser = p;
		this.view = v;
		this.ad = ad;
		this.ef = ef;
	}

	@Override
	public void getActivityProcess(DesktopPlace item, JsApplicationItem app, DataCallback<ActivityProcess> callback) {
		ActivityProcess result = new ActivityProcessImpl();
		String catalog = interpret.getPlaceCatalog(item);
		ExitToBrowseActivity exit = new ExitToBrowseActivity(catalog);

		ImportCSVDataInputTask readData = new ImportCSVDataInputTask(view, parser, dm, descriptionService);
		ImportDataHandler strategyToPersistAllData = new CSVImportDataHandler(sateProvider, ccm);
		HumanTask<ImportData, ImportData> imageUploader = new ImageImportTask(dm, descriptionService, uploadUrlService, messages, theme);
		State<ImportData, ImportData> imageupdater = new ImageWriteTask();

		SimpleImportProcess<JsCatalogEntry> process = new SimpleImportProcess<JsCatalogEntry>(interpret, readData, fieldAssignationProvider.get(),
				imageUploader, imageupdater, strategyToPersistAllData, typeSelection, selectionInterpret);
		Image ok = new Image(theme.ok());
		readData.setAction(Arrays.asList(ok));
		result.addAll(process);
		result.addState(exit);
		callback.setResultAndFinish(result);
	}

}
