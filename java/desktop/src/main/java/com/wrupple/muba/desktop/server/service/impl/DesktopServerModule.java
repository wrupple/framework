package com.wrupple.muba.desktop.server.service.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.wrupple.muba.catalogs.domain.ActivityDescriptor;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.catalogs.domain.CatalogActionTriggerImpl;
import com.wrupple.muba.catalogs.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogEntryImpl;
import com.wrupple.muba.catalogs.domain.WrupleSVGDocument;
import com.wrupple.muba.catalogs.domain.WruppleAudioMetadata;
import com.wrupple.muba.catalogs.domain.WruppleFileMetadata;
import com.wrupple.muba.catalogs.domain.WruppleVideoMetadata;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileResponseWriter;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.domain.ConstraintDTO;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.SharedContextWriter;
import com.wrupple.muba.catalogs.server.service.TransactionDictionary;
import com.wrupple.muba.catalogs.server.service.WriterDictionary;
import com.wrupple.muba.catalogs.server.service.WruppleServerModule;
import com.wrupple.muba.catalogs.server.service.impl.AbstractServerModule;
import com.wrupple.muba.cms.domain.WruppleDomainHTMLPage;
import com.wrupple.muba.cms.domain.WruppleDomainJavascript;
import com.wrupple.muba.cms.domain.WruppleDomainStyleSheet;
import com.wrupple.muba.cms.server.chain.command.BlobDeleteHandler;
import com.wrupple.muba.cms.server.chain.command.SVGResponseWriter;
import com.wrupple.muba.desktop.domain.WruppleImageMetadata;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.PersistentImageMetadata;

public class DesktopServerModule extends AbstractServerModule implements WruppleServerModule {

	private final Provider<ApplicationItem> adminApp;
	private final Provider<CatalogDescriptor> itemDescp;
	private final Provider<CatalogDescriptor> imageDescp;
	private final Provider<CatalogDescriptor> htmlDescp;
	private final Provider<CatalogDescriptor> cssDescp;
	private final Provider<CatalogDescriptor> jsDescp;
	private final Provider<CatalogDescriptor> videoDescp;
	private final Provider<CatalogDescriptor> audioDescp;
	private final Provider<CatalogDescriptor> documentDescp;
	private final Provider<CatalogDescriptor> svgDescp;
	private final CatalogActionTriggerImpl blobDeletetrigger;

	@Inject
	public DesktopServerModule(WriterDictionary writerDictionary, TransactionDictionary transactions, CatalogFileResponseWriter file, SVGResponseWriter svg,
			BlobDeleteHandler imagetrigger, @Named("adminApp") Provider<ApplicationItem> adminApp,
			@Named(ApplicationItem.CATALOG) Provider<CatalogDescriptor> itemDescp, @Named(WruppleImageMetadata.CATALOG) Provider<CatalogDescriptor> imageDescp,
			@Named(WruppleDomainHTMLPage.CATALOG) Provider<CatalogDescriptor> htmlDescp,
			@Named(WruppleDomainStyleSheet.CATALOG) Provider<CatalogDescriptor> cssDescp,
			@Named(WruppleDomainJavascript.CATALOG) Provider<CatalogDescriptor> jsDescp,
			@Named(WruppleVideoMetadata.CATALOG) Provider<CatalogDescriptor> videoDescp,
			@Named(WruppleAudioMetadata.CATALOG) Provider<CatalogDescriptor> audioDescp,
			@Named(WruppleFileMetadata.CATALOG) Provider<CatalogDescriptor> documentDescp,
			@Named(WrupleSVGDocument.CATALOG) Provider<CatalogDescriptor> svgDescp) {
		super();

		this.adminApp = adminApp;
		this.itemDescp = itemDescp;
		this.imageDescp = imageDescp;
		this.htmlDescp = htmlDescp;
		this.cssDescp = cssDescp;
		this.jsDescp = jsDescp;
		this.videoDescp = videoDescp;
		this.audioDescp = audioDescp;
		this.documentDescp = documentDescp;
		this.svgDescp = svgDescp;

		blobDeletetrigger = new CatalogActionTriggerImpl(2, 3, false, null, null, null);
		blobDeletetrigger.linkToChain(BlobDeleteHandler.class.getSimpleName());
		blobDeletetrigger.setRollbackOnFail(true);
		blobDeletetrigger.setStopOnFail(true);

		transactions.addCommand(BlobDeleteHandler.class.getSimpleName(), imagetrigger);
		writerDictionary.addCommand(PersistentImageMetadata.CATALOG, file);
		writerDictionary.addCommand(WruppleVideoMetadata.CATALOG, file);
		writerDictionary.addCommand(WruppleAudioMetadata.CATALOG, file);
		writerDictionary.addCommand(WruppleFileMetadata.CATALOG, file);
		writerDictionary.addCommand(WrupleSVGDocument.CATALOG, svg);
	}

	@Override
	public void writeItems(ActivityDescriptor domainRoot) {
		((ApplicationItem) domainRoot).appendChild(adminApp.get());
	}

	@Override
	public CatalogDescriptor getDescriptorForName(String catalogId, Long domain) throws Exception {
		CatalogDescriptor img;
		if (ApplicationItem.CATALOG.equals(catalogId)) {
			return itemDescp.get();
		} else if (WruppleImageMetadata.CATALOG.equals(catalogId)) {
			img = imageDescp.get();
			img.getTriggersValues().add(blobDeletetrigger);
			return img;
		} else if (WruppleDomainHTMLPage.CATALOG.equals(catalogId)) {
			return htmlDescp.get();
		} else if (WruppleDomainStyleSheet.CATALOG.equals(catalogId)) {
			return cssDescp.get();
		} else if (WruppleDomainJavascript.CATALOG.equals(catalogId)) {
			return jsDescp.get();
		} else if (WruppleVideoMetadata.CATALOG.equals(catalogId)) {
			img = videoDescp.get();
			img.getTriggersValues().add(blobDeletetrigger);
			return img;
		} else if (WruppleAudioMetadata.CATALOG.equals(catalogId)) {
			img = audioDescp.get();
			img.getTriggersValues().add(blobDeletetrigger);
			return img;
		} else if (WruppleFileMetadata.CATALOG.equals(catalogId)) {
			img = documentDescp.get();
			img.getTriggersValues().add(blobDeletetrigger);
			img.setVersioned(true);
			img.setRevised(true);
			return img;
		} else if (WrupleSVGDocument.CATALOG.equals(catalogId)) {
			img = svgDescp.get();
			return img;
		}
		// com.wrupple.base.domain.persistent.PersistentImageContentTag value
		// field 327016
		return null;
	}

	@Override
	public void modifyAvailableCatalogList(List<? super CatalogEntry> names, CatalogExcecutionContext context) {
		names.add(new CatalogEntryImpl(ApplicationItem.CATALOG, "Desktop Place", "/static/img/application.png"));
		names.add(new CatalogEntryImpl(WruppleDomainHTMLPage.CATALOG, "HTML", "/static/img/document.png"));
		names.add(new CatalogEntryImpl(WruppleDomainStyleSheet.CATALOG, "CSS", "/static/img/css.png"));
		names.add(new CatalogEntryImpl(WruppleDomainJavascript.CATALOG, "JavaScript", "/static/img/javascript.png"));
		names.add(new CatalogEntryImpl(WruppleImageMetadata.CATALOG, PersistentImageMetadata.IMAGE_FIELD, "/static/img/image.png"));
		names.add(new CatalogEntryImpl(WruppleVideoMetadata.CATALOG, "Video", "/static/img/video.png"));
		names.add(new CatalogEntryImpl(WruppleAudioMetadata.CATALOG, "Audio", "/static/img/audio.png"));
		names.add(new CatalogEntryImpl(WruppleFileMetadata.CATALOG, "Document", "/static/img/misc-files.png"));
		names.add(new CatalogEntryImpl(WrupleSVGDocument.CATALOG, "Drawing", "/static/img/vector.png"));
	}

	@Override
	public CatalogDataAccessObject<? extends CatalogEntry> getOrAssembleDataSource(CatalogDescriptor catalog, Class<? extends CatalogEntry> clazz,
			CatalogExcecutionContext context) throws Exception {
		return null;
	}

	@Override
	public void invalidateCache(String o) {

	}

	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor catalog) {
	}

	@Override
	public void writeClientContext(SharedContextWriter contextWriter) throws Exception {

	}

	@Override
	public void registerConstraints(Map<String, ? extends ConstraintDTO> map) {
		// TODO Auto-generated method stub

	}

	@Override
	public CatalogDescriptor loadFromCache(String host, String domain, String catalog) {
		throw new UnsupportedOperationException("this is only supported client side");
	}

}
