package com.wrupple.muba.desktop.server.chain.command.impl;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import javax.inject.Provider;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.DataStoreManager;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.catalogs.shared.services.CatalogTokenInterpret;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.cms.server.services.VanityIdReader;
import com.wrupple.muba.desktop.server.domain.DesktopBuilderContext;
import com.wrupple.muba.desktop.shared.services.UrlParser;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.services.impl.VegetateUrlServiceBuilder;

public abstract class AbstractSingleCatalogEntryWriter implements Command {

	private final VanityIdReader reader;
	private final Provider<CatalogTokenInterpret> catalogDescriptionServiceP;
	private final CatalogPropertyAccesor cpa;
	private final Provider<DataStoreManager> dsmp;
	private DataStoreManager dsm;
	private CatalogTokenInterpret descriptionService;

	public AbstractSingleCatalogEntryWriter(VanityIdReader reader, Provider<CatalogTokenInterpret> catalogDescriptionServiceP, CatalogPropertyAccesor cpa,
			Provider<DataStoreManager> dsmp) {
		super();
		this.dsmp = dsmp;
		this.catalogDescriptionServiceP = catalogDescriptionServiceP;
		this.reader = reader;
		this.cpa = cpa;

	}

	@Override
	public boolean execute(Context c) throws Exception {
		DesktopBuilderContext context = (DesktopBuilderContext) c;
		ProcessTaskDescriptor task = context.getTask();
		PrintWriter writer = context.getResponse().getWriter();

		List<String> taskTokens = task.getUrlTokens();

		if (taskTokens == null) {
			taskTokens = UrlParser.DEFAULT_FORM_TASK_TOKENS;
		}

		String[] pathTokens = context.getPathTokens();
		int index = context.getNextPathToken();

		String rawEntryName = null, catalogId = null;
		CatalogEntry entry;

		String taskToken;
		for (int i = 0; i < taskTokens.size(); i++) {
			index += i;
			if (pathTokens.length < index) {
				taskToken = taskTokens.get(i);

				if (CatalogActionRequest.CATALOG_ID_PARAMETER.equals(taskToken)) {
					catalogId = pathTokens[index];

				} else if (CatalogActionRequest.CATALOG_ENTRY_PARAMETER.equals(taskToken)) {
					rawEntryName = pathTokens[index];

				}
			}
		}

		context.setNextPathToken(index);

		if (task.getCatalogId() != null) {
			catalogId = task.getCatalogId();
		}

		CatalogDescriptor catalog = getDescriptionService().getDescriptorForName(catalogId, task.getDomain());

		if (rawEntryName == null) {
			entry = null;
		} else {
			entry = reader.read(rawEntryName, catalog, context.getCatalogContext());
			assert entry != null : rawEntryName + " is an invalid identifier";
		}

		writeHeader(context, task, catalog, writer, entry);

		/*
		 * ACTIVITY CONTEXT PARAMETERS
		 */
		writer.print("<input type=\"hidden\" name=\"");
		writer.print(UrlParser.PRIVATE_CATALOG_ID_PARAMETER);
		writer.print("\" value=\"");
		writer.print(catalogId);
		writer.print("\" />");

		if (entry != null) {
			writer.print("<input type=\"hidden\" name=\"");
			writer.print(UrlParser.PRIVATE_CATALOG_ENTRY_PARAMETER);
			writer.print("\" value=\"");
			writer.print(entry.getIdAsString());
			writer.print("\" />");
		}

		/*
		 * Catalog FIELDS
		 */

		Collection<FieldDescriptor> fields = catalog.getOwnedFieldsValues();

		writer.println("<table>");
		Object fieldValue;
		Collection<Object> fieldValues;
		Session session = entry == null ? null : cpa.newSession(entry);
		VegetateUrlServiceBuilder catalogUrlBuilder = (VegetateUrlServiceBuilder) context.get(VegetateUrlServiceBuilder.class.getSimpleName());
		CatalogDataAccessObject<CatalogEntry> dao;
		CatalogEntry foreignValue;
		List<CatalogEntry> foreignValues;
		FilterData filter;
		for (FieldDescriptor field : fields) {
			if (field.isDetailable() && !field.isEphemeral()) {
				fieldValue = entry == null ? null : cpa.getPropertyValue(catalog, field, entry, null, session);
				writer.println("<tr>");
				writer.println("<td>");
				writer.println(field.getName());
				writer.println("</td>");
				writer.println("<td>");
				if (field.isMultiple() && fieldValue != null) {
					fieldValues = (Collection<Object>) fieldValue;
					if (ImplicitJoinUtils.isFileField(field)) {
						for (Object o : fieldValues) {
							writer.println("<p>");
							writeFile(context, catalogUrlBuilder, writer, entry, field, o);
							writer.println("</p>");
						}
					} else {
						if (ImplicitJoinUtils.isJoinableValueField(field)) {
							dao = getDSM().getOrAssembleDataSource(field.getForeignCatalogName(), context.getCatalogContext(), CatalogEntry.class);
							filter = FilterDataUtils.createSingleKeyFieldFilter(CatalogEntry.ID_FIELD, (List) fieldValues);
							foreignValues = dao.read(filter);
							if (foreignValues != null) {
								for (CatalogEntry o : foreignValues) {
									writeForeignValue(writer, context, catalog, entry, field, o, foreignValues);
								}
							}
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
							if (fieldValue == null) {
								foreignValue = null;
							} else {
								dao = getDSM().getOrAssembleDataSource(field.getForeignCatalogName(), context.getCatalogContext(), CatalogEntry.class);
								foreignValue = dao.read(String.valueOf(fieldValue));
							}

							writeForeignValue(writer, context, catalog, entry, field, foreignValue, fieldValue);
						} else {
							writeFieldValue(writer, field, fieldValue);
						}
					}
				}
				writer.println("</td>");
				writer.println("</tr>");
			}
		}
		writer.println("</table>");

		writeFooter(context, task, catalog, writer, entry);
		return CONTINUE_PROCESSING;
	}

	protected abstract void writeFooter(DesktopBuilderContext context, ProcessTaskDescriptor task, CatalogDescriptor catalog, PrintWriter writer,
			CatalogEntry entry);

	protected abstract void writeHeader(DesktopBuilderContext context, ProcessTaskDescriptor task, CatalogDescriptor catalog, PrintWriter writer,
			CatalogEntry entry);

	protected abstract void writeForeignValue(PrintWriter writer, DesktopBuilderContext context, CatalogDescriptor catalog, CatalogEntry parentEntry,
			FieldDescriptor field, CatalogEntry foreignEntry, Object fieldValue);

	protected abstract void writeFieldValue(PrintWriter writer, FieldDescriptor field, Object fieldValue);

	protected abstract void writeFile(DesktopBuilderContext context, VegetateUrlServiceBuilder catalogUrlBuilder, PrintWriter writer, CatalogEntry parentEntry,
			FieldDescriptor field, Object fieldValue);

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
}
