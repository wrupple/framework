package com.wrupple.muba.desktop.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.DataStoreManager;
import com.wrupple.muba.catalogs.shared.services.CatalogTokenInterpret;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.cms.server.services.VanityIdReader;
import com.wrupple.muba.desktop.client.chain.command.ContainterRequestInterpret;
import com.wrupple.muba.desktop.server.chain.command.FormWriter;
import com.wrupple.muba.desktop.server.domain.impl.DesktopRequestContextImpl;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.server.services.impl.VegetateUrlServiceBuilder;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

@Singleton
public class FormWriterImpl extends AbstractSingleCatalogEntryWriter implements FormWriter {

    @Inject
    public FormWriterImpl(VanityIdReader reader, Provider<CatalogTokenInterpret> catalogDescriptionServiceP, CatalogPropertyAccesor cpa,
                          Provider<DataStoreManager> dsmp) {
        super(reader, catalogDescriptionServiceP, cpa, dsmp);
    }

    @Override
    protected void writeForeignValue(PrintWriter writer, DesktopRequestContextImpl context, CatalogDescriptor catalog, CatalogEntry parentEntry,
                                     FieldDescriptor field, CatalogEntry foreignEntry, Object fieldValue) {
        if (foreignEntry != null) {
            writer.print(foreignEntry.getName());
        }
        writer.print("<input type=\"text\"  name=\"");
        writer.print(field.getFieldId());
        writer.print("\" value=\"");
        if (foreignEntry != null) {
            writer.print(foreignEntry.getIdAsString());
        }
        writer.print("\" />");
    }

    @Override
    protected void writeFieldValue(PrintWriter writer, FieldDescriptor field, Object fieldValue) {
        List<String> options = field.getDefaultValueOptions();
        if (options != null) {
            writer.print("<select name =\"");
            writer.print(field.getFieldId());
            writer.print("\">");

            if (CatalogEntry.INTEGER_DATA_TYPE == field.getDataType()) {
                int index = ((Integer) fieldValue).intValue();
                for (int i = 0; i < options.size(); i++) {
                    if (index == i) {
                        writer.println("<option selected");
                    } else {
                        writer.println("<option ");
                    }
                    writer.print(" value=\"");
                    writer.print(i);
                    writer.print("\" >");
                    writer.print(options.get(i));
                    writer.println("</option >");
                }

            } else {
                for (int i = 0; i < options.size(); i++) {
                    if (fieldValue.equals(options.get(i))) {
                        writer.println("<option selected>");
                    } else {
                        writer.println("<option >");
                    }
                    writer.print(options.get(i));
                    writer.println("</option >");
                }
            }

            writer.print("</select>");
        } else {
            writer.print("<input type=\"text\"  name=\"");
            writer.print(field.getFieldId());
            writer.print("\" value=\"");
            if (fieldValue != null) {
                writer.print(fieldValue);
            }
            writer.print("\" />");
        }

    }

    @Override
    protected void writeFile(DesktopRequestContextImpl context, VegetateUrlServiceBuilder catalogUrlBuilder, PrintWriter writer, CatalogEntry parentEntry,
                             FieldDescriptor field, Object fieldValue) {
        if (fieldValue == null) {
            writer.print("<input type=\"file\" name=\"");
            writer.print(field.getFieldId());
            writer.print("\" />");
        } else {
            SearchEngineOptimizedDesktopWriterCommandImpl.writeFileLink(catalogUrlBuilder, writer, field.getForeignCatalogName(), String.valueOf(fieldValue),
                    String.valueOf(parentEntry.getDomain()), parentEntry.getName());
        }
    }

    @Override
    protected void writeFooter(DesktopRequestContextImpl context, ProcessTaskDescriptor task, CatalogDescriptor catalog, PrintWriter writer, CatalogEntry entry) {
        writer.println("<input type=\"submit\" value=\"Ok\">");
        writer.print("<input type=\"hidden\" name=\"");
        writer.print(ContainterRequestInterpret.SUBMITTING_TASK);
        writer.print(" value=\"");
        writer.print(task.getIdAsString());
        writer.print("\"  >");
        if (entry != null) {
            writer.print("<input type=\"hidden\" name=\"__");
            writer.print(CatalogEntry.ID_FIELD);
            writer.print(" value=\"");
            writer.print(entry.getIdAsString());
            writer.print("\"  >");
        }
        writer.println("</form>");
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
        writer.print("<form action=\"");
        writer.print(context.getSubmitUrl());
        writer.print("\"  method=\"post\" ");

        Collection<FieldDescriptor> fields = catalog.getOwnedFieldsValues();
        boolean multipart = false;
        for (FieldDescriptor field : fields) {
            if (ImplicitJoinUtils.isFileField(field)) {
                multipart = true;
                break;
            }
        }
        if (multipart) {
            writer.print(" enctype=\"multipart/form-data\" ");
        } else {
            writer.print(" enctype=\"application/x-www-form-urlencoded\" ");
        }
        writer.print(">");

    }

}
