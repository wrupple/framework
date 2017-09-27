package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;

import org.apache.commons.chain.Context;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.server.service.ObjectMapper;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.catalogs.server.chain.command.FormatResultSet;
import com.wrupple.muba.catalogs.server.chain.command.ImplicitDataJoin;
import com.wrupple.muba.catalogs.server.domain.CatalogActionResultImpl;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;

@Singleton
public class FormatResultSetImpl implements FormatResultSet {
	protected final ObjectMapper mapper;
	private final ImplicitDataJoin resultService;
	private final CatalogKeyServices pkes;

	@Inject
	public FormatResultSetImpl(ObjectMapper mapper, ImplicitDataJoin resultService, CatalogKeyServices pkes) {
		super();
		this.mapper = mapper;
		this.resultService = resultService;
		this.pkes = pkes;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;

		String action = (String) c.get(CatalogActionRequest.NAME_FIELD);

		List<String> warnings = context.getRuntimeContext().resetWarnings();
		Set<ConstraintViolation<?>> constraintViolations = context.getRuntimeContext().getConstraintViolations();
		List<CatalogColumnResultSet> responseList = (List<CatalogColumnResultSet>) context
				.get(CompleteCatalogGraph.JOINED_DATA);

		if (constraintViolations == null || constraintViolations.isEmpty()) {

			if (responseList == null) {
				if (!CatalogActionRequest.DELETE_ACTION.equals(action)) {
					resultService.execute(context);
					responseList = (List<CatalogColumnResultSet>) context.get(CompleteCatalogGraph.JOINED_DATA);
				}
			}
		} else {
			// warn user of constraint violations
			if (warnings == null) {
				warnings = new ArrayList<String>();
			}
			for (ConstraintViolation<?> violation : constraintViolations) {
				warnings.add( violation.getMessage());
			}
		}

		long responseTimestamp = System.currentTimeMillis();
		PrintWriter out = context.getRuntimeContext().getScopedWriter(context);
		encodePrimaryKeys(responseList);
		if (responseList == null || responseList.isEmpty()) {
			warnings.add(EMPTY_RESPONSE);
		}
		CatalogActionResultImpl responseContract = new CatalogActionResultImpl(responseTimestamp, responseList,
				warnings);

		LengthReportingWriter wrapper = new LengthReportingWriter(out);
		mapper.writeValue(wrapper, responseContract);
		long length = wrapper.getLength();
		context.getRuntimeContext().setTotalResponseSize(length);
		return CONTINUE_PROCESSING;
	}

	static class LengthReportingWriter extends PrintWriter {
		long length;
		int offset;

		public LengthReportingWriter(PrintWriter out) {
			super(out);
			length = 0;
			offset = 0;
		}

		// TODO override all other writing methods, we know jackson only uses
		// this one though
		@Override
		public void write(char[] buf, int off, int len) {
			if (buf != null) {
				length += len;
			}
			super.write(buf, off, len);
		}

		public long getLength() {
			return length;
		}

		/*
		 * public void write(byte[] b) { write(b, 0, b.length); } public void
		 * write(byte[] b, int off, int len) { inner.write(b, off, len); written
		 * += len; } public abstract void write(int b) { written ++;
		 * inner.write(b); }
		 */
	}

	private void encodePrimaryKeys(List<CatalogColumnResultSet> responseList) {
		CatalogDescriptor descriptor;
		Collection<FieldDescriptor> fields;
		String fieldId;
		List<Object> column;
		Object row;
		Object encodedRow;
		for (CatalogColumnResultSet resultSet : responseList) {
			descriptor = resultSet.getCatalogDescriptor();
			resultSet.setCatalogDescriptor(null);
			fields = descriptor.getFieldsValues();
			for (FieldDescriptor field : fields) {
				if (pkes.qualifiesForEncoding(field, descriptor)) {
					fieldId = field.getFieldId();
					column = resultSet.getContents().get(fieldId);
					if (column != null) {
						for (int i = 0; i < column.size(); i++) {
							row = column.get(i);
							if (row != null) {
								encodedRow = pkes.encodeClientPrimaryKeyFieldValue(row, field, descriptor);
								column.set(i, encodedRow);
							}
						}
					}
				}
			}
		}
	}

}
