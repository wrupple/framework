package com.wrupple.base.server.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.wrupple.muba.catalogs.server.service.CatalogEntryBeanDAO;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;
import com.wrupple.muba.catalogs.server.service.GenericJavaObjectDAO;
import com.wrupple.muba.desktop.server.service.CatalogTableNameService;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

public class CatalogEntryBeanDAOImpl extends AbstractJDBC_DAO<CatalogEntry> implements CatalogEntryBeanDAO {

	private Class<? extends CatalogEntry> clazz;
	private BeanHandler<CatalogEntry> beaner;
	private BeanListHandler<CatalogEntry> lister;
	private CatalogEvaluationDelegate accessor;
	private Session session;

	@Inject
	public CatalogEntryBeanDAOImpl(DataSource ds,/*SingleDomainClassTableNameService*/ CatalogTableNameService tableNames, @Named("catalog.datePattern") String  formatt, CatalogEvaluationDelegate accessor,@Named("system.multitenant") Boolean multitenant,@Named("domainField") String domainField) {
		super(ds, tableNames, formatt, multitenant, domainField);
		this.accessor=accessor;
	}
	

	@Override
	public void setIncludePublicDomainInResults(boolean publicDomainReadAccess) {
		//in this implementation those are never included
	}

	@Override
	public <V extends CatalogEntry> GenericJavaObjectDAO<V> cast(Class<V> clazz, CatalogDescriptor catalog) {
		this.clazz = clazz;
		beaner = null;
		lister = null;
		this.catalog = catalog;
		return (GenericJavaObjectDAO<V>) this;
	}

	@Override
	protected CatalogDescriptor getCatalogDescriptor() {
		return catalog;
	}

	@Override
	protected Object getFieldValue(CatalogEntry e, FieldDescriptor field) {
		if(this.session==null){
			this.session=this.accessor.newSession(e);
		}
		//FIXME MOVE i18n LOCALIZATION TO daos?
		return this.accessor.getPropertyValue(catalog, field, e, null, session);
	}

	@Override
	protected void setProperty(CatalogEntry e, FieldDescriptor field, Object value) {
		if(this.session==null){
			this.session=this.accessor.newSession(e);
		}
		this.accessor.setPropertyValue(catalog, field, e, value, session);
	}

	@Override
	protected ResultSetHandler<CatalogEntry> getRSHandler() {
		if (beaner == null) {
			beaner = new BeanHandler(this.clazz);
		}
		return beaner;
	}

	@Override
	protected ResultSetHandler<List<CatalogEntry>> getRSListHandler() {
		if (lister == null) {
			lister = new BeanListHandler(this.clazz);
		}
		return lister;
	}


	@Override
	protected String getColumnForField(FieldDescriptor field) {
		return field.getFieldId();
	}


	@Override
	protected String getForeignTableName(FieldDescriptor field) {
		return tableNames.getTableNameForCatalogFiled(getCatalogDescriptor(), field, domain);
	}

}
