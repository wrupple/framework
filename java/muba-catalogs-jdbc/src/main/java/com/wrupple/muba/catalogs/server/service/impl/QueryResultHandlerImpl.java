package com.wrupple.muba.catalogs.server.service.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.handlers.AbstractListHandler;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.PersistentCatalogEntity;
import com.wrupple.muba.event.server.domain.impl.PersistentCatalogEntityImpl;
import com.wrupple.muba.catalogs.server.service.JDBCMappingDelegate;
import com.wrupple.muba.catalogs.server.service.QueryResultHandler;

public class QueryResultHandlerImpl extends AbstractListHandler<CatalogEntry> implements QueryResultHandler {
	protected Logger log = LogManager.getLogger(QueryResultHandlerImpl.class);

	private final JDBCMappingDelegate delegate;
	private final DateFormat format;
	private final FieldAccessStrategy access;

	private CatalogRowProcessor mapper;
	private Class<? extends CatalogEntry> clazz;


	@Inject
	public QueryResultHandlerImpl(JDBCMappingDelegate delegate, DateFormat format, FieldAccessStrategy access) {
		super();
		this.delegate = delegate;
		this.format = format;
		this.access = access;
	}

	@Override
	public void setContext(CatalogActionContext context) throws Exception {
		log.trace("created result haandler");
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		mapper = new CatalogRowProcessor(catalog);
		clazz = catalog.getClazz();
	}

	@Override
	protected CatalogEntry handleRow(ResultSet rs) throws SQLException {
		log.trace("hadle row");
		CatalogEntry r;
		if (clazz == null || PersistentCatalogEntity.class.equals(clazz)) {
			Map<String, Object> map = mapper.toMap(rs);
			r = new PersistentCatalogEntityImpl(mapper.catalog, map);
		} else {
			r = mapper.toBean(rs, clazz);

		}

		return r;
	}

	class CatalogRowProcessor extends BasicRowProcessor {

		private CatalogDescriptor catalog;
		private Instrospection instrospection;

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
				log.trace("Handle column {} as map key",columnName);
				if (null == columnName || 0 == columnName.length()) {
					columnName = rsmd.getColumnName(i);
				}
				
				field = catalog.getFieldDescriptor(delegate.getFieldNameForColumn(columnName,false));
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
			} catch (InstantiationException  e) {
				throw new IllegalArgumentException("cannot instantiate " + type);
			}catch(IllegalAccessException e){
				throw new IllegalArgumentException("cannot instantiate " + type);
			}
			if (instrospection == null) {
				instrospection = access.newSession((CatalogEntry) result);
			}
			FieldDescriptor field;
			for (int i = 1; i <= cols; i++) {
				String columnName = rsmd.getColumnLabel(i);
				log.trace("Handle column {} as bean property",columnName);
				if (null == columnName || 0 == columnName.length()) {
					columnName = rsmd.getColumnName(i);
				}
				
				field = catalog.getFieldDescriptor(delegate.getFieldNameForColumn(columnName,false));
				if (field != null) {
					try {
                        access.setPropertyValue(field, (CatalogEntry) result,
								delegate.handleColumnField(rs,field, field.getDataType(), i, format), instrospection);
					} catch (Exception e) {
						throw new IllegalArgumentException(e);
					}
				}

			}
			return result;
		}
	}

}
