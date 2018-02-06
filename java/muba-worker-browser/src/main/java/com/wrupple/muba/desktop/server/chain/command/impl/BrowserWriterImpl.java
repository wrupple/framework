package com.wrupple.muba.desktop.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.DataStoreManager;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyEncodingService;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.catalogs.shared.services.CatalogTokenInterpret;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.cms.server.ContentServerModule;
import com.wrupple.muba.desktop.client.chain.command.ContainterRequestInterpret;
import com.wrupple.muba.desktop.server.chain.command.BrowserWriter;
import com.wrupple.muba.desktop.server.domain.impl.DesktopRequestContextImpl;
import com.wrupple.muba.desktop.shared.services.UrlParser;
import com.wrupple.muba.desktop.shared.services.UserInteractionStateModifier;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.services.impl.VegetateUrlServiceBuilder;
import org.apache.commons.chain.Context;

import javax.inject.Provider;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

public class BrowserWriterImpl implements BrowserWriter {

    private final Provider<CatalogTokenInterpret> catalogDescriptionServiceP;
    private final Provider<DataStoreManager> dsmp;
    private final Provider<ContentServerModule> cmsp;
    private final CatalogPropertyAccesor cpa;
    private final PrimaryKeyEncodingService pkes;
    private CatalogTokenInterpret descriptionService;
    private DataStoreManager dsm;
    private ContentServerModule cms;

    public BrowserWriterImpl(Provider<CatalogTokenInterpret> catalogDescriptionServiceP, Provider<ContentServerModule> cmsp, CatalogPropertyAccesor cpa,
                             Provider<DataStoreManager> dsmp, PrimaryKeyEncodingService pkes) {
        super();
        this.pkes = pkes;
        this.cmsp = cmsp;
        this.dsmp = dsmp;
        this.catalogDescriptionServiceP = catalogDescriptionServiceP;
        this.cpa = cpa;
    }

    @Override
    public boolean execute(Context c) throws Exception {
        DesktopRequestContextImpl context = (DesktopRequestContextImpl) c;
        ProcessTaskDescriptor task = context.getTask();
        PrintWriter writer = context.getResponse().getWriter();

        String catalogId = task.getCatalogId();
        CatalogDescriptor catalog = getDescriptionService().getDescriptorForName(catalogId, task.getDomain());
        int index = context.getNextPathToken();
        int pageToken = index;
        String[] pathTokens = context.getPathTokens();
        int page;
        if (pathTokens.length < index) {
            context.setNextPathToken(index + 1);
            page = Integer.parseInt(pathTokens[index]);
        } else {
            page = 1;
        }

        FilterData filter = FilterDataUtils.newFilterData();
        filter.setConstrained(true);
        filter.setStart((FilterData.DEFAULT_INCREMENT * 3) * (page - 1));
        filter.setLength(FilterData.DEFAULT_INCREMENT * 3);

        CatalogDataAccessObject<CatalogEntry> dao = getDSM().getOrAssembleDataSource(catalog, context.getCatalogContext(), CatalogEntry.class);

        List<CatalogEntry> results = dao.read(filter);

        if (results != null && !results.isEmpty()) {
            boolean multipleSelection = isMultipleSelection(task);
            Session session = cpa.newSession(results.get(0));
            if (multipleSelection) {
                writer.print("<form  action=\"");
                writer.print(context.getSubmitUrl());
                writer.print("\"  >");
                writer.print("<input type=\"hidden\" name=\"");
                writer.print(ContainterRequestInterpret.SUBMITTING_TASK);
                writer.print(" value=\"");
                writer.print(task.getIdAsString());
                writer.print("\"  >");
            }
            writer.println("<table>");
            Collection<FieldDescriptor> fields = catalog.getOwnedFieldsValues();
            FieldDescriptor primaryFeyField = catalog.getFieldDescriptor(catalog.getKeyField());
            if (primaryFeyField == null) {
                primaryFeyField = catalog.getFieldDescriptor(CatalogEntry.ID_FIELD);
            }

            writer.println("<tr>");

            if (multipleSelection) {
                writer.println("<td>");
                writer.print("-");
                writer.println("</td>");
            }

            for (FieldDescriptor field : fields) {
                if (field.isSummary() || field == primaryFeyField) {
                    writer.println("<td>");
                    writer.print(field.getName());
                    writer.println("</td>");
                }
            }
            writer.println("</tr>");

            Object fieldValue;
            Collection<Object> fieldValues;
            VegetateUrlServiceBuilder catalogUrlBuilder = (VegetateUrlServiceBuilder) context.get(VegetateUrlServiceBuilder.class.getSimpleName());
            // none of these are supposed to be null, so Nullpointerexception
            // will get thrown if they are.
            ProcessTaskDescriptor submitTask = context.getSubmitTask();
            String submitUrl = context.getSubmitUrl();
            List<String> taskTokens = submitTask.getUrlTokens();

            if (taskTokens == null) {
                taskTokens = UrlParser.DEFAULT_FORM_TASK_TOKENS;
            }


            for (CatalogEntry entry : results) {
                writer.println("<tr>");
                if (multipleSelection) {
                    writer.println("<td>");
                    writer.print("<input type=\"checkbox\" name=\"");
                    writer.print(CatalogActionRequest.CATALOG_ENTRY_PARAMETER);
                    writer.print("value=\"");
                    writer.print(pkes.encodeClientPrimaryKeyFieldValue(entry.getId(), primaryFeyField, catalog));
                    writer.print("\" />");
                    writer.println("<td>");
                }

                for (FieldDescriptor field : fields) {
                    if (field.isSummary() || field == primaryFeyField) {
                        fieldValue = cpa.getPropertyValue(catalog, field, entry, null, session);
                        writer.println("<td>");
                        if (field == primaryFeyField) {

                            writeSingleEntrySubmission(context, writer, field, entry, submitUrl, taskTokens);

                        } else if (field.isMultiple() && fieldValue != null) {
                            fieldValues = (Collection<Object>) fieldValue;
                            if (ImplicitJoinUtils.isFileField(field)) {
                                for (Object o : fieldValues) {
                                    writer.println("<p>");
                                    writeFile(context, catalogUrlBuilder, writer, entry, field, o);
                                    writer.println("</p>");
                                }
                            } else {
                                if (ImplicitJoinUtils.isJoinableValueField(field)) {
                                    // ignore in browser
                                } else {
                                    for (Object o : fieldValues) {
                                        writer.println("<p>");
                                        writeFieldValue(writer, field, o);
                                        writer.println("</p>");
                                    }
                                }
                            }

                        } else {
                            if (ImplicitJoinUtils.isFileField(field)) {
                                writeFile(context, catalogUrlBuilder, writer, entry, field, fieldValue);
                            } else {
                                if (ImplicitJoinUtils.isJoinableValueField(field)) {
                                    // ignore in browser
                                } else {
                                    writeFieldValue(writer, field, fieldValue);
                                }
                            }
                        }
                        writer.println("</td>");
                    }
                }

                writer.println("</tr>");
            }

            writer.println("</table>");
            if (multipleSelection) {
                writer.println("<input type=\"submit\" value=\"Ok\">");
                writer.println("</form>");
            }

            writer.println("<nav><ul>");
            // TODO use paging cursors... because... come on!
            if (page > 1) {
                writer.println("<li>");
                writer.print("<a herf=\"");
                buildPageUrl(pathTokens, pageToken, writer, (page - 1));
                writer.print("\">");
                writer.print((page - 1));
                writer.println("</a>");
                writer.println("</li>");
            }
            if (results != null && results.size() == filter.getLength()) {
                writer.println("<li>");
                writer.print("<a herf=\"");
                buildPageUrl(pathTokens, pageToken, writer, (page + 1));
                writer.print("\">");
                writer.print((page + 1));
                writer.println("</a>");
                writer.println("</li>");
            }

            writer.println("</ul></nav>");

        }

        return CONTINUE_PROCESSING;
    }

    private void buildPageUrl(String[] pathTokens, int pageToken, PrintWriter writer, int page) {
        int i;
        for (i = 0; i < pathTokens.length; i++) {
            writer.print('/');
            if (i == pageToken) {
                break;
            } else {
                writer.print(pathTokens[i]);
            }
        }
        if (page > 0) {
            writer.print('/');
            writer.print(page);
        }

    }

    private void writeSingleEntrySubmission(DesktopRequestContextImpl context, PrintWriter writer, FieldDescriptor field, CatalogEntry submitted, String submitUrl,
                                            List<String> taskTokens) {

        writer.println("<a href=\"");
        writer.print(submitUrl);

        //TODO for each entry we are repeating this comparation
        if (taskTokens.get(0).equals(CatalogActionRequest.CATALOG_ENTRY_PARAMETER)) {
            writer.print('/');
        } else {
            writer.print('?');
            writer.print(CatalogActionRequest.CATALOG_ENTRY_PARAMETER);
            writer.print('=');
        }
        writer.print(submitted.getIdAsString());
        writer.print("\" >");
        writer.println(submitted.getName() == null ? submitted.getIdAsString() : submitted.getName());
        writer.println("</a>");
    }

    private void writeFile(DesktopRequestContextImpl context, VegetateUrlServiceBuilder catalogUrlBuilder, PrintWriter writer, CatalogEntry entry,
                           FieldDescriptor field, Object imageKey) {
        if (imageKey != null) {
            SearchEngineOptimizedDesktopWriterCommandImpl.writeFileLink(catalogUrlBuilder, writer, field.getForeignCatalogName(), String.valueOf(imageKey),
                    String.valueOf(entry.getDomain()), entry.getName());
        }
    }

    private void writeFieldValue(PrintWriter writer, FieldDescriptor field, Object fieldValue) {
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

    private boolean isMultipleSelection(ProcessTaskDescriptor task) {
        List<String> properties = task.getProperties();
        if (properties == null) {
            return false;
        } else {
            for (String s : properties) {
                if (s.startsWith("selectionModel")) {
                    return s.endsWith(UserInteractionStateModifier.MULTIPLE_SELECTION);
                }
            }
            return false;
        }

    }

    private DataStoreManager getDSM() {
        if (dsm == null) {
            dsm = dsmp.get();
        }
        return dsm;
    }

    private CatalogTokenInterpret getDescriptionService() {
        if (descriptionService == null) {
            descriptionService = catalogDescriptionServiceP.get();
        }
        return descriptionService;
    }

    private ContentServerModule getCMS() {
        if (cms == null) {
            cms = this.cmsp.get();
        }
        return cms;
    }
}
