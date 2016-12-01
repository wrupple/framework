package com.wrupple.base.server.service.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.wrupple.base.domain.PersistentCatalogEntityImpl;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.PersistentCatalogEntityDAO;
import com.wrupple.muba.desktop.server.service.CatalogTableNameService;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

public class PersistentCatalogJDBCEntityDAOImpl extends AbstractJDBC_DAO<PersistentCatalogEntity> implements PersistentCatalogEntityDAO {

	ResultSetHandler<PersistentCatalogEntity> beaner;
	ResultSetHandler<List<PersistentCatalogEntity>> lister;
	CatalogRowProcessor processor ;

	@Inject
	public PersistentCatalogJDBCEntityDAOImpl(DataSource ds, CatalogTableNameService tableNames, @Named("catalog.datePattern") String formatt,
			@Named("system.multitenant") Boolean multitenant, @Named("domainField") String domainField) {
		super(ds, tableNames, formatt, multitenant, domainField);
		processor = new CatalogRowProcessor();
	}

	@Override
	public PersistentCatalogEntity create(PersistentCatalogEntity o) throws Exception {
		passThroughPreUpdateHandlers(o);
		return super.create(o);
	}

	@Override
	public PersistentCatalogEntity update(PersistentCatalogEntity originalEntry, PersistentCatalogEntity updatedEntry) throws Exception {
		passThroughPreUpdateHandlers(updatedEntry);
		return super.update(originalEntry, updatedEntry);
	}

	@Override
	public PersistentCatalogEntity delete(PersistentCatalogEntity o) throws Exception {
		passThroughPreDeleteHandlers(o);
		return super.delete(o);
	}

	@Override
	protected ResultSetHandler<PersistentCatalogEntity> getRSHandler() {

		if (beaner == null) {
			beaner = new ResultSetHandler<PersistentCatalogEntity>() {

				MapHandler mapper = new MapHandler(processor);

				@Override
				public PersistentCatalogEntity handle(ResultSet rs) throws SQLException {
					Map<String, Object> map = mapper.handle(rs);
					PersistentCatalogEntityImpl r = new PersistentCatalogEntityImpl(getCatalogDescriptor());
					r.setPersistentProperties(map);
					passThroughHandlers(r);
					return r;
				}
			};
		}
		return beaner;
	}

	@Override
	protected ResultSetHandler<List<PersistentCatalogEntity>> getRSListHandler() {
		if (lister == null) {
			lister = new ResultSetHandler<List<PersistentCatalogEntity>>() {

				MapListHandler mapper = new MapListHandler(processor);

				@Override
				public List<PersistentCatalogEntity> handle(ResultSet rs) throws SQLException {
					List<Map<String, Object>> rawResults = mapper.handle(rs);
					List<PersistentCatalogEntity> results = new ArrayList<PersistentCatalogEntity>(rawResults.size());
					PersistentCatalogEntityImpl r;
					for (Map<String, Object> map : rawResults) {
						r = new PersistentCatalogEntityImpl(getCatalogDescriptor());
						r.setPersistentProperties(map);
						passThroughHandlers(r);
						results.add(r);
					}

					return results;
				}

			};
		}
		return lister;
	}

	class CatalogRowProcessor extends BasicRowProcessor {
		
		public Map<String, Object> toMap(ResultSet rs) throws SQLException {

			ResultSetMetaData rsmd = rs.getMetaData();
			CatalogDescriptor catslago = getCatalog();
			int cols = rsmd.getColumnCount();
			Map<String, Object> result = new HashMap<String, Object>(cols);
			FieldDescriptor field;
			for (int i = 1; i <= cols; i++) {
				String columnName = rsmd.getColumnLabel(i);
				if (null == columnName || 0 == columnName.length()) {
					columnName = rsmd.getColumnName(i);
				}
				field = catslago.getFieldDescriptor(columnName);
				if(field==null){
					result.put(columnName, rs.getObject(i));
				}else{
					result.put(columnName, handlerColumnField(rs, field.getDataType(), i, formatt));
				}
				
			}
			return result;
		}
		
	}

	private List<EntityFieldPersistanceHandler> fieldHandlers;

	@Override
	public void addFieldGenerator(EntityFieldPersistanceHandler handler) {
		if (fieldHandlers == null) {
			fieldHandlers = new ArrayList<EntityFieldPersistanceHandler>();
		}
		fieldHandlers.add(handler);
	}

	private void passThroughPreUpdateHandlers(PersistentCatalogEntity o) throws SQLException {
		if (fieldHandlers != null) {
			for (EntityFieldPersistanceHandler handler : fieldHandlers) {
				handler.preUpdate(o, domain);
			}
		}
	}

	private void passThroughPreDeleteHandlers(PersistentCatalogEntity o) throws SQLException {
		if (fieldHandlers != null) {
			for (EntityFieldPersistanceHandler handler : fieldHandlers) {
				handler.predelete(o);
			}
		}
	}

	private void passThroughHandlers(PersistentCatalogEntityImpl r) throws SQLException {
		if (fieldHandlers != null) {
			for (EntityFieldPersistanceHandler handler : fieldHandlers) {
				handler.postRead(r);
			}
		}
	}

	@Override
	public void setCatalogDescriptor(CatalogDescriptor catalog) {
		this.catalog = catalog;
	}

	@Override
	public void overridePersistentKind(String domainRegistryEntity) {
		setOverridenTableName(domainRegistryEntity);
	}

	@Override
	public void setNamespace() {

	}

	@Override
	public void unsetNamespace() {

	}

	@Override
	protected CatalogDescriptor getCatalogDescriptor() {
		return catalog;
	}

	@Override
	protected Object getFieldValue(PersistentCatalogEntity e, FieldDescriptor field) {
		return e.getPropertyValue(field.getFieldId());
	}

	@Override
	protected void setProperty(PersistentCatalogEntity r, FieldDescriptor field, Object fieldValue) {
		r.setPropertyValue(fieldValue, field.getFieldId());
	}

	@Override
	protected String getColumnForField(FieldDescriptor field) {
		String fieldId = field.getFieldId();
		if (fieldHandlers != null) {
			// TODO build a cache of column field mappings
			for (EntityFieldPersistanceHandler handler : fieldHandlers) {
				if (fieldId.equals(handler.getFieldId())) {
					return handler.getColumn();
				}
			}
		}
		return fieldId;
	}

	@Override
	protected String getForeignTableName(FieldDescriptor field) {
		if (fieldHandlers != null) {
			String fieldId = field.getFieldId();
			// TODO build a cache of column field mappings
			for (EntityFieldPersistanceHandler handler : fieldHandlers) {
				if (fieldId.equals(handler.getFieldId())) {
					// handler handles forign entries
					return null;
				}
			}
		}

		return tableNames.getTableNameForCatalogFiled(getCatalogDescriptor(), field, domain);
	}

	@Override
	public void setContext(CatalogExcecutionContext context) {

	}

}
