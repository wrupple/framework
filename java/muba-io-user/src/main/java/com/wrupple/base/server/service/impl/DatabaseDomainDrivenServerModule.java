package com.wrupple.base.server.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.sql.DataSource;

import com.wrupple.muba.catalogs.domain.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.wrupple.muba.catalogs.domain.CatalogContractListenerImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.domain.ConstraintDTO;
import com.wrupple.muba.catalogs.server.service.CatalogEntryBeanDAO;
import com.wrupple.muba.catalogs.server.service.GenericJavaObjectDAO;
import com.wrupple.muba.catalogs.server.service.JSRAnnotationsDictionary;
import com.wrupple.muba.catalogs.server.service.PersistentCatalogEntityDAO;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.catalogs.shared.services.CatalogTokenInterpret;
import com.wrupple.muba.desktop.domain.ConstraintImpl;
import com.wrupple.muba.desktop.server.service.CatalogTableNameService;
import com.wrupple.muba.catalogs.domain.AncestryConclusions;
import com.wrupple.muba.catalogs.domain.CatalogContractListener;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.Constraint;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.HasCatalogId;
import com.wrupple.muba.catalogs.server.domain.AnonymouslyVisibleField;
import com.wrupple.muba.catalogs.server.domain.CatalogDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.FieldDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.NameField;
import com.wrupple.muba.catalogs.server.domain.PrimaryKeyField;
import com.wrupple.muba.catalogs.server.services.SessionContext;

public class DatabaseDomainDrivenServerModule extends AbstractDatabaseDrivenServerModule {

	private QueryRunner runner;
	private Provider<CatalogTokenInterpret> namespace;
	private CatalogTableNameService tableNames;

	@Inject
	public DatabaseDomainDrivenServerModule(CatalogTableNameService tableNames, Provider<CatalogTokenInterpret> namespace, DataSource ds,
			PrimaryKeyField id,NameField name,AnonymouslyVisibleField publicField, Provider<JSRAnnotationsDictionary> validationDictionary,
			Provider<CatalogEntryBeanDAO> localDatasourceProvider, Provider<SessionContext> sessionDataServicep,
			Provider<PersistentCatalogEntityDAO> multitenantDataSourceProvider) {
		super( id, name, publicField, validationDictionary, localDatasourceProvider, sessionDataServicep, multitenantDataSourceProvider);
		this.runner = new QueryRunner(ds);
		this.namespace = namespace;
		this.tableNames = tableNames;
	}

	@Override
	public void modifyAvailableCatalogList(List<CatalogEntryImpl> names, CatalogExcecutionContext context) throws Exception {
		try {
			CatalogDescriptor catalogCatalogDescriptor=namespace.get().getDescriptorForName(CatalogDescriptor.CATALOG_ID);
			List<CatalogEntryImpl> newNames = runner.query(
					"SELECT catalogId AS id, name,image FROM " + tableNames.getTableNameForCatalog(catalogCatalogDescriptor, null),
					new BeanListHandler<CatalogEntryImpl>(CatalogEntryImpl.class));
			names.addAll(newNames);
		} catch (SQLException e) {
			if (e.getErrorCode() == 1146) {
				// TABLE OF CATALOGS NOT YET INITIALIZED
			} else {
				throw e;
			}
		}

	}

	@Override
	protected CatalogDescriptor getFirstResultDescriptor(String name, Long domain) throws Exception {
		CatalogTokenInterpret namespace = this.namespace.get();
		CatalogEntryBeanDAO dao = localDatasourceProvider.get();
		CatalogDescriptor catalogCatalogDescriptor=namespace.getDescriptorForName(CatalogDescriptor.CATALOG_ID);
		GenericJavaObjectDAO<CatalogDescriptorImpl> catalogDao = dao.cast(CatalogDescriptorImpl.class,
				catalogCatalogDescriptor);

		catalogDao.setDomain(domain);

		// entries?
		List<CatalogDescriptorImpl> descriptors = catalogDao.read(FilterDataUtils.createSingleFieldFilter(HasCatalogId.FIELD, name));
		if (descriptors == null || descriptors.isEmpty()) {
			return null;
		}
		CatalogDescriptorImpl regreso = descriptors.get(0);
		// Add all fields

		List<Long> ownedFields = regreso.getOwnedFields();

		FieldDescriptorImpl field;
		Map<String, FieldDescriptor> builtFields = new LinkedHashMap<String, FieldDescriptor>();
		dao = localDatasourceProvider.get();
		dao.setDomain(domain);
		GenericJavaObjectDAO<FieldDescriptorImpl> fieldDao = dao.cast(FieldDescriptorImpl.class, namespace.getDescriptorForName(FieldDescriptor.CATALOG_ID));
		dao = localDatasourceProvider.get();
		GenericJavaObjectDAO<ConstraintImpl> constraintDao = dao.cast(ConstraintImpl.class,
				namespace.getDescriptorForName(Constraint.CATALOG_ID));
		constraintDao.setDomain(domain);
		if (ownedFields != null) {
			for (Long fieldId : ownedFields) {
				field = fieldDao.read(String.valueOf(fieldId));
				List<Long> constrains = field.getConstraints();
				if (constrains != null) {
					ArrayList<Constraint> constrainsList = new ArrayList<Constraint>(constrains.size());
					ConstraintImpl constraint;
					for (Long constraintId : constrains) {
						constraint = constraintDao.read(String.valueOf(constraintId));
						constrainsList.add(constraint);
					}
					field.setConstraintsValues(constrainsList);
				}

				String foreinCatalogDomainId = field.getForeignCatalogName();
				if (foreinCatalogDomainId == null) {
					Long foreignCatalogId = field.getForeignCatalog();
					if (foreignCatalogId == null) {
						// nothing to do
					} else {
						//

						foreinCatalogDomainId = runner.query(
								"SELECT * FROM " + tableNames.getTableNameForCatalog(catalogCatalogDescriptor, domain) + " WHERE id=?", new ScalarHandler<String>(
										HasCatalogId.FIELD), foreignCatalogId);
					}
				}
				field.setForeignCatalogName(foreinCatalogDomainId);
				field.setOwnerCatalogId(name);

				builtFields.put(field.getFieldId(), field);
			}
		}
		regreso.setFields(builtFields);

		// handle inheritance
		Long parentId = regreso.getParentId();
		if (parentId == null) {
			//regreso.addFields(defaultFields, false);
		} else {
			CatalogDescriptorImpl parent = null;
			String parentCatalogId;
			AncestryConclusions ancestry = new AncestryConclusions();
			ancestry.childestChild = regreso.getCatalogId();
			Collection<FieldDescriptor> ingeretedFields;
			FieldDescriptorImpl childField;

			while (parentId != null) {
				parentCatalogId = runner.query("SELECT * FROM " + tableNames.getTableNameForCatalog(catalogCatalogDescriptor, domain) + " WHERE id=?",
						new ScalarHandler<String>(HasCatalogId.FIELD), parentId);
				if (parent == null) {
					parent = (CatalogDescriptorImpl) namespace.getDescriptorForName(parentCatalogId);
					regreso.addAppliedCriteria(parent.getAppliedCriteria());
					regreso.addAppliedSorts(parent.getAppliedSorts());
					ingeretedFields = parent.getOwnedFieldsValues();
					for (FieldDescriptor ingeritedField : ingeretedFields) {
						if (!builtFields.containsKey(ingeritedField.getFieldId())) {
							childField = new FieldDescriptorImpl();
							BeanUtils.copyProperties(childField, ingeritedField);
							childField.setInherited(true);
							builtFields.put(ingeritedField.getFieldId(), childField);
						}

					}
				}

				ancestry.eldestParent = parentCatalogId;
				if (ContentNode.CATALOG_TIMELINE.equals(parentCatalogId)) {
					ancestry.isTimeline = true;
				}
				if (regreso.getParent() == null) {
					regreso.setParent(parentCatalogId);
					regreso.setAncestryConclusions(ancestry);
				}
				parentId = parent.getParentId();
			}
		}

		/*
		 * TRIGGERS
		 */
		List<Long> triggers = regreso.getTriggers();

		if (triggers != null) {
			dao = localDatasourceProvider.get();
			GenericJavaObjectDAO<CatalogContractListenerImpl> triggerDao = dao.cast(CatalogContractListenerImpl.class,
					namespace.getDescriptorForName(CatalogContractListenerImpl.CATALOG));
			CatalogContractListenerImpl trigger;
			List<CatalogContractListener> values = regreso.getTriggersValues();
			for (Long triggerId : triggers) {
				trigger = triggerDao.read(String.valueOf(triggerId));
				values.add(trigger);
			}
		}

		regreso.setDomainDriven(true);
		return regreso;
	}

	@Override
	public void registerConstraints(Map<String, ? extends ConstraintDTO> map) {
		// TODO user defined constraints (user can already define custom evaluation context functions right?)
		
	}
	
	@Override
	public CatalogDescriptor loadFromCache(String host, String domain, String catalog) {
		throw new UnsupportedOperationException("this is only supported client side");
	}


}
