package com.wrupple.muba.desktop.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.DataStoreManager;
import com.wrupple.muba.catalogs.shared.services.CatalogTokenInterpret;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.cms.server.services.VanityIdReader;
import com.wrupple.muba.cms.shared.services.ContentManagerRegistry;
import com.wrupple.muba.desktop.server.chain.command.DetailWriter;
import com.wrupple.muba.desktop.server.domain.impl.DesktopRequestContextImpl;
import com.wrupple.muba.desktop.shared.services.UrlParser;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.server.services.impl.VegetateUrlServiceBuilder;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.PrintWriter;
import java.util.List;

@Singleton
public class DetailWriterImpl extends AbstractSingleCatalogEntryWriter implements DetailWriter {

    private ContentManagerRegistry cms;
    private UrlParser url;

    @Inject
    public DetailWriterImpl(VanityIdReader reader, Provider<CatalogTokenInterpret> catalogDescriptionServiceP, CatalogPropertyAccesor cpa,
                            Provider<DataStoreManager> dsmp, ContentManagerRegistry cms, UrlParser url) {
        super(reader, catalogDescriptionServiceP, cpa, dsmp);
        this.cms = cms;
        this.url = url;
    }

    @Override
    protected void writeFile(DesktopRequestContextImpl context, VegetateUrlServiceBuilder catalogUrlBuilder, PrintWriter writer, CatalogEntry entry,
                             FieldDescriptor field, Object imageKey) {
        if (imageKey != null) {
            SearchEngineOptimizedDesktopWriterCommandImpl.writeFileLink(catalogUrlBuilder, writer, field.getForeignCatalogName(), String.valueOf(imageKey),
                    String.valueOf(entry.getDomain()), entry.getName());
        }
    }

    @Override
    protected void writeFieldValue(PrintWriter writer, FieldDescriptor field, Object fieldValue) {
        if (fieldValue != null) {
            List<String> options = field.getDefaultValueOptions();
            if (options != null && CatalogEntry.INTEGER_DATA_TYPE == field.getDataType()) {
                int index = ((Integer) fieldValue).intValue();
                writer.println(options.get(index));
            } else {

                writer.println(fieldValue);

            }
        }
    }

    @Override
    protected void writeForeignValue(PrintWriter writer, DesktopRequestContextImpl context, CatalogDescriptor catalog, CatalogEntry parentEntry,
                                     FieldDescriptor field, CatalogEntry foreignValue, Object fieldValue) {
        if (foreignValue != null) {
            writer.print("<a href=\"");
            ApplicationItem manager = cms.getManager(context.getDesktopPlaceHierarchy(), catalog, CatalogActionRequest.READ_ACTION);
            url.printItemActivity(context.getPathTokens(), context.getFirstTokenIndex(), manager, context.getDesktopPlaceHierarchy(), writer);
            writer.print("\">");
            writer.println(foreignValue.getName());
            writer.print("</a>");
        }
    }

    @Override
    protected void writeFooter(DesktopRequestContextImpl context, ProcessTaskDescriptor task, CatalogDescriptor catalog, PrintWriter writer, CatalogEntry entry) {

    }

    @Override
    protected void writeHeader(DesktopRequestContextImpl context, ProcessTaskDescriptor task, CatalogDescriptor catalog, PrintWriter writer, CatalogEntry entry) {
        if (entry != null) {
            writer.println("<h1>");
            writer.print(catalog.getName());
            writer.print(" - ");
            writer.print(entry.getName());
            writer.println("</h1>");
        }
    }

}
