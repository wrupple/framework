package com.wrupple.muba.catalogs.server.service.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.handlers.AbstractListHandler;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntityImpl;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;
import com.wrupple.muba.catalogs.server.service.JDBCMappingDelegate;
import com.wrupple.muba.catalogs.server.service.QueryResultHandler;

public class QueryResultHandlerImpl extends AbstractListHandler<CatalogEntry> implements QueryResultHandler {

	private final JDBCMappingDelegate delegate;
	private final DateFormat format;

	private CatalogRowProcessor mapper;
	private Class<? extends CatalogEntry> clazz;
	private final CatalogEvaluationDelegate axs;

	@Inject
	public QueryResultHandlerImpl(JDBCMappingDelegate delegate, DateFormat format, CatalogEvaluationDelegate axs) {
		super();
		this.axs = axs;
		this.delegate = delegate;
		this.format = format;
	}

	@Override
	public void setContext(CatalogActionContext context) throws Exception {

		CatalogDescriptor catalog = context.getCatalogDescriptor();
		mapper = new CatalogRowProcessor(catalog);
		clazz = catalog.getJavaClass();
	}

	@Override
	protected CatalogEntry handleRow(ResultSet rs) throws SQLException {
		CatalogEntry r;
		if (clazz == null || PersistentCatalogEntity.class.getCanonicalName().equals(clazz)) {
			Map<String, Object> map = mapper.toMap(rs);
			r = new PersistentCatalogEntityImpl(mapper.catalog, map);
		} else {
			r = mapper.toBean(rs, clazz);

		}

		return r;
	}

	class CatalogRowProcessor extends BasicRowProcessor {

		private CatalogDescriptor catalog;
		private Session session;

		public CatalogRowProcessor(CatalogDescriptor catalog) {
			this.catalog = catalog;
		}

		public Map<String, Object> toMap(ResultSet rs) throws SQLException {

			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			Map<String, Object> result = new HashMap<String, Object>(cols);
			FieldDescriptor field;
			for (int i = 1; i <= cols; i++) {
				String columnName = rsmd.getColumnLabel(i);
				if (null == columnName || 0 == columnName.length()) {
					columnName = rsmd.getColumnName(i);
				}
				field = catalog.getFieldDescriptor(columnName);
				if (field == null) {
					result.put(columnName, rs.getObject(i));
				} else {
					result.put(columnName, delegate.handleColumnField(rs, field,field.getDataType(), i, format));
				}

			}
			return result;
		}

		@Override
		public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {

			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			T result;
			try {
				result = type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException("cannot instantiate " + type);
			}
			if (session == null) {
				session = axs.newSession((CatalogEntry) result);
			}
			FieldDescriptor field;
			for (int i = 1; i <= cols; i++) {
				String columnName = rsmd.getColumnLabel(i);
				if (null == columnName || 0 == columnName.length()) {
					columnName = rsmd.getColumnName(i);
				}
				field = catalog.getFieldDescriptor(columnName);
				if (field != null) {
					try {
						axs.setPropertyValue(catalog, field, (CatalogEntry) result,
								delegate.handleColumnField(rs,field, field.getDataType(), i, format), session);
					} catch (Exception e) {
						throw new IllegalArgumentException(e);
					}
				}

			}
			return result;
		}
	}

}
