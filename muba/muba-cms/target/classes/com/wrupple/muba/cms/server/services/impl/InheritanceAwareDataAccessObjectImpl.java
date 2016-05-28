package com.wrupple.muba.cms.server.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.CatalogTokenInterpret;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.PropertyMapDAO;
import com.wrupple.muba.catalogs.server.service.impl.StorageManagerImpl;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.HasAccesablePropertyValues;
import com.wrupple.vegetate.server.chain.CatalogManager;

/**
 * @author japi
 * 
 */
public class InheritanceAwareDataAccessObjectImpl implements CatalogDataAccessObject<HasAccesablePropertyValues> {
	protected final PropertyMapDAO<HasAccesablePropertyValues> delegateDao;
	protected final CatalogDescriptor catalog;
	private final String ancestorIdField;

	private final CatalogExcecutionContext context;
	protected final CatalogPropertyAccesor access;
	private final DatabasePlugin serverSide;

	// protected TimelineCreatedEventHandler timelineListener;

	public InheritanceAwareDataAccessObjectImpl(DatabasePlugin serverSide, PropertyMapDAO<HasAccesablePropertyValues> delegateDao, CatalogDescriptor catalog,
			String ancestorFieldId, CatalogExcecutionContext contextProvider, CatalogPropertyAccesor access) {
		this.serverSide = serverSide;
		this.delegateDao = delegateDao;
		this.ancestorIdField = ancestorFieldId;
		this.access = access;
		this.catalog = catalog;
		this.context = contextProvider;
	}

	@Override
	public HasAccesablePropertyValues create(HasAccesablePropertyValues o) throws Exception {
		Session session = access.newSession(o);
		return create(o, session);
	}

	@Override
	public HasAccesablePropertyValues read(Object targetEntryId) throws Exception {
		// read child entity
		HasAccesablePropertyValues childEntity = delegateDao.read(targetEntryId);
		// we are certain this catalog has a parent, otherwise this DAO would
		// not be called
		String parentCatalogId = catalog.getParent();
		// aquire parent id
		String parentEntityId = getAllegedParentId(childEntity);
		// delegate deeper inheritance to another instance of an AncestorAware
		// DAO
		Session session = access.newSession(childEntity);
		processChild(childEntity, parentCatalogId, parentEntityId, context.getRequest().getStorageManager().spawn(context), session);
		return childEntity;
	}

	@Override
	public List<HasAccesablePropertyValues> read(FilterData filterData) throws Exception {
		// read child Results
		List<HasAccesablePropertyValues> children = delegateDao.read(filterData);
		if (children != null && !children.isEmpty()) {
			Session session = access.newSession(children.get(0));
			CatalogExcecutionContext readContext = context.getRequest().getStorageManager().spawn(context);
			processChildren(children, readContext, session);
			return children;
		} else {
			return Collections.EMPTY_LIST;
		}

	}

	protected void processChildren(List<HasAccesablePropertyValues> children, CatalogExcecutionContext readContext, Session session) throws Exception {
		// we are certain this catalog has a parent, otherwise this DAO would
		// not be called
		String parentCatalogId = catalog.getParent();
		String parentEntityId;
		for (HasAccesablePropertyValues childEntity : children) {
			parentEntityId = getAllegedParentId(childEntity);
			processChild(childEntity, parentCatalogId, parentEntityId, readContext, session);
		}
	}

	protected void processChild(HasAccesablePropertyValues childEntity, String parentCatalogId, String parentEntityId, CatalogExcecutionContext readParentEntry,
			Session session) throws Exception {

		CatalogEntry parentEntity = readEntry(parentCatalogId, parentEntityId, readParentEntry);
		// add inherited values to child Entity
		if (parentEntity instanceof HasAccesablePropertyValues) {
			addPropertyValues((HasAccesablePropertyValues) parentEntity, childEntity, catalog, false);
		} else {
			addPropertyValues(parentEntity, childEntity, catalog, false, session);
		}

	}

	private CatalogEntry readEntry(String parentCatalogId, String parentEntityId, CatalogExcecutionContext readParentEntry) throws Exception {
		readParentEntry.setCatalog(parentCatalogId);
		readParentEntry.setEntry(parentEntityId);
		readParentEntry.setAction(CatalogActionRequest.READ_ACTION);
		readParentEntry.setEntryValue(null);
		readParentEntry.setFilter(null);

		context.getRequest().getStorageManager().getRead().execute(readParentEntry);

		return readParentEntry.getResult();
	}

	@Override
	public HasAccesablePropertyValues update(HasAccesablePropertyValues originalEntry, HasAccesablePropertyValues updatedEntry) throws Exception {
		// we are certain this catalog has a parent, otherwise this DAO would
		// not be called

		String parentCatalogId = catalog.getParent();
		String parentEntityId = (String) getAllegedParentId(originalEntry);

		// synthesize parent entity from all non-inherited, passing all
		// inherited field Values
		Session session = access.newSession(updatedEntry);
		CatalogEntry updatedParentEntity = synthesizeCatalogObject(updatedEntry, serverSide.getDescriptorForName(parentCatalogId, context), false, session);
		// delegate deeper inheritance to another instance of an AncestorAware
		// DAO
		CatalogExcecutionContext childContext = context.getRequest().getStorageManager().spawn(context);
		CatalogEntry originalParentEntity = readEntry(parentCatalogId, parentEntityId, childContext);
		childContext.setEntry(originalParentEntity.getId());
		childContext.setEntryValue(updatedParentEntity);
		context.getRequest().getStorageManager().getWrite().execute(childContext);
		updatedParentEntity = context.getResult();
		// synthesize childEntity (Always will be Entity Kind) ignoring all
		// inheritedFields
		HasAccesablePropertyValues childEntity = (HasAccesablePropertyValues) synthesizeCatalogObject(updatedEntry, catalog, true, session);
		// delegate dao handles datastore interaction
		childEntity = delegateDao.update(originalEntry, updatedEntry);
		// add inherited values to child Entity
		if (updatedParentEntity instanceof HasAccesablePropertyValues) {
			addPropertyValues((HasAccesablePropertyValues) updatedParentEntity, childEntity, catalog, false);
		} else {
			addPropertyValues(updatedParentEntity, childEntity, catalog, false, session);
		}
		return childEntity;
	}

	@Override
	public HasAccesablePropertyValues delete(HasAccesablePropertyValues o) throws Exception {
		String parentEntityId = getAllegedParentId(o);
		// we are certain this catalog has a parent, otherwise this DAO would
		// not be called
		String parentCatalogId = catalog.getParent();
		CatalogExcecutionContext childContext = context.getRequest().getStorageManager().spawn(context);
		// if parent not found, asume it has been deleted previously
		if (parentEntityId != null) {
			// delegate deeper inheritance to another instance of an
			// AncestorAware DAO
			childContext.setCatalog(parentCatalogId);
			childContext.setEntry(parentEntityId);

			context.getRequest().getStorageManager().getDelete().execute(childContext);

		}

		HasAccesablePropertyValues childEntity = delegateDao.delete(o);

		return childEntity;
	}

	private CatalogEntry synthesizeCatalogObject(HasAccesablePropertyValues source, CatalogDescriptor catalog, boolean excludeInherited, Session session)
			throws Exception {
		delegateDao.setNamespace();
		CatalogEntry target = access.synthesize(catalog);

		if (target instanceof HasAccesablePropertyValues) {
			addPropertyValues(source, (HasAccesablePropertyValues) target, catalog, excludeInherited);
		} else {
			addPropertyValues(source, target, catalog, excludeInherited, session);
		}
		delegateDao.unsetNamespace();
		return target;
	}

	private CatalogEntry createAncestorsRecursively(HasAccesablePropertyValues o, CatalogDescriptor parentCatalog, String allegedParentId, Session session,
			CatalogExcecutionContext childContext) throws Exception {

		// synthesize parent entity from all non-inherited, passing all
		// inherited field Values

		CatalogEntry parentEntity;
		String parentCatalogId = parentCatalog.getCatalogId();
		if (allegedParentId == null) {
			parentEntity = synthesizeCatalogObject(o, parentCatalog, false, session);
			childContext.setEntryValue(parentEntity);
			childContext.setCatalog(parentCatalogId);
			context.getRequest().getStorageManager().getNew().execute(childContext);
			parentEntity = childContext.getResult();
		} else {
			parentEntity = readEntry(parentCatalogId, allegedParentId, childContext);
			if (parentEntity == null) {
				throw new IllegalArgumentException("entry parent does not exist " + allegedParentId + "@" + parentCatalogId);
			}
		}
		return parentEntity;
	}

	private String getAllegedParentId(HasAccesablePropertyValues childEntity) {
		String parentEntityId = (String) childEntity.getPropertyValue(ancestorIdField);
		return parentEntityId;
	}

	private void addInheritedValuesToChild(CatalogEntry parentEntity, HasAccesablePropertyValues childEntity, Session session) {
		if (parentEntity instanceof HasAccesablePropertyValues) {
			addPropertyValues((HasAccesablePropertyValues) parentEntity, childEntity, catalog, false);
		} else {
			addPropertyValues(parentEntity, childEntity, catalog, false, session);
		}
	}

	private HasAccesablePropertyValues synthesizeChildEntity(String parentEntityId, HasAccesablePropertyValues o, Session session) throws Exception {
		HasAccesablePropertyValues childEntity = (HasAccesablePropertyValues) synthesizeCatalogObject(o, catalog, true, session);
		childEntity.setPropertyValue(parentEntityId, ancestorIdField);
		return childEntity;
	}

	protected HasAccesablePropertyValues create(HasAccesablePropertyValues o, Session session) throws Exception {
		String parentCatalogId = catalog.getParent();
		String allegedParentId = getAllegedParentId(o);
		CatalogDescriptor parentCatalog = serverSide.getDescriptorForName(parentCatalogId, context);
		CatalogEntry parentEntity = createAncestorsRecursively(o, parentCatalog, allegedParentId, session,
				context.getRequest().getStorageManager().spawn(context));
		String parentEntityId = parentEntity.getIdAsString();
		HasAccesablePropertyValues childEntity = synthesizeChildEntity(parentEntityId, o, session);
		childEntity = delegateDao.create(childEntity);
		addInheritedValuesToChild(parentEntity, childEntity, session);
		return childEntity;
	}

	private void addPropertyValues(HasAccesablePropertyValues source, CatalogEntry target, CatalogDescriptor catalog, boolean excludeInherited,
			Session session) {
		Collection<FieldDescriptor> fields = catalog.getFieldsValues();
		String fieldId;
		Object value;
		for (FieldDescriptor field : fields) {
			if (excludeInherited && field.isInherited()) {
				// ignore
			} else {
				fieldId = field.getFieldId();
				// ignore id fields
				if (!(CatalogEntry.ID_FIELD.equals(fieldId))) {
					value = source.getPropertyValue(fieldId);
					access.setPropertyValue(catalog, field, target, value, session);
				}
			}
		}
	}

	private void addPropertyValues(HasAccesablePropertyValues source, HasAccesablePropertyValues target, CatalogDescriptor catalog, boolean excludeInherited) {
		Collection<FieldDescriptor> fields = catalog.getFieldsValues();
		String fieldId;
		Object value;
		for (FieldDescriptor field : fields) {
			if (excludeInherited && field.isInherited()) {
				// ignore
			} else {
				fieldId = field.getFieldId();
				// ignore id fields
				if (!(CatalogEntry.ID_FIELD.equals(fieldId))) {
					value = source.getPropertyValue(fieldId);
					if (value != null) {
						target.setPropertyValue(value, fieldId);
					}
				}
			}
		}
	}

	private void addPropertyValues(CatalogEntry source, HasAccesablePropertyValues target, CatalogDescriptor catalog, boolean excludeInherited,
			Session session) {
		Collection<FieldDescriptor> fields = catalog.getFieldsValues();
		String fieldId;
		Object value;
		for (FieldDescriptor field : fields) {
			if (excludeInherited && field.isInherited()) {
				// ignore
			} else {
				fieldId = field.getFieldId();
				// ignore id fields
				if (!CatalogEntry.ID_FIELD.equals(fieldId)) {
					try {
						value = access.getPropertyValue(catalog, field, source, null, session);
						if (value != null) {
							target.setPropertyValue(value, fieldId);
						}
					} catch (Exception e) {
						System.err.println("unable to read property " + fieldId);
					}
				}
			}
		}
	}

	@Override
	public void setContext(CatalogExcecutionContext context) {
		delegateDao.setContext(context);
	}

	@Override
	public void beginTransaction() throws NotSupportedException, SystemException {
		delegateDao.beginTransaction();
	}

	@Override
	public void commitTransaction()
			throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
		delegateDao.commitTransaction();
	}

	@Override
	public void rollbackTransaction() throws IllegalStateException, SecurityException, SystemException {
		delegateDao.rollbackTransaction();
	}

	@Override
	public CatalogExcecutionContext getContext() {
		return delegateDao.getContext();
	}
}