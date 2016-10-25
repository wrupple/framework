package com.wrupple.muba.catalogs.server.service.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasAccesablePropertyValues;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.LargeStringFieldDataAccessObject;

@Singleton
public class CatalogEvaluationDelegateImpl implements CatalogEvaluationDelegate {

	protected static final Logger log = LoggerFactory.getLogger(CatalogEvaluationDelegateImpl.class);


	private final String ancestorIdField;
	private LargeStringFieldDataAccessObject lsdao;
	private Provider<PersistentCatalogEntity> factory;
	private final Pattern pattern;
	private final PropertyUtilsBean bean;

	@Inject
	public CatalogEvaluationDelegateImpl(@Named("template.pattern") Pattern pattern,
			@Named("catalog.ancestorKeyField") String ancestorIdField, LargeStringFieldDataAccessObject lsdao,
			Provider<PersistentCatalogEntity> factory) {
		this.ancestorIdField = ancestorIdField;
		this.lsdao = lsdao;
		this.pattern = pattern;
		bean = new PropertyUtilsBean();
		this.factory = factory;
	}

	@Override
	public CatalogEntry synthesize(CatalogDescriptor catalog) throws Exception {
		if (catalog.getClazz() == null || PersistentCatalogEntity.class.getCanonicalName().equals(catalog.getClazz())) {

			PersistentCatalogEntity target = factory.get();
			target.initCatalog(catalog);
			return target;
		} else {
			Class<?> clazz = Class.forName(catalog.getClazz());
			CatalogEntry target = (CatalogEntry) clazz.newInstance();
			return target;
		}
	}

	@Override
	public <T extends CatalogEntry> T catalogCopy(CatalogDescriptor catalog, T entry) throws Exception {

		T copy = (T) synthesize(catalog);

		Collection<FieldDescriptor> fields = catalog.getFieldsValues();

		Session session = newSession(copy);

		Object value;
		for (FieldDescriptor field : fields) {
			if (field.isKey() && field.getFieldId().equals(catalog.getKeyField())) {

			} else {
				value = getPropertyValue(catalog, field, entry, null, session);
				setPropertyValue(catalog, field, copy, value, session);
			}
		}

		return copy;
	}

	private class FieldAccessSession implements Session {
		boolean accesible;

		@Override
		public void resample(CatalogEntry sample) {
			if (sample == null) {
				accesible = true;
			} else {
				accesible = sample instanceof HasAccesablePropertyValues;
			}

		}

		// use PropertyUtilsBean (bean utils) and dump srping
		private Object getPropertyValue2(Object bean, String property) throws IntrospectionException,
				IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			Class<?> beanClass = bean.getClass();
			PropertyDescriptor propertyDescriptor = getPropertyDescriptor(beanClass, property);
			if (propertyDescriptor == null) {
				throw new IllegalArgumentException("No such property " + property + " for " + beanClass + " exists");
			}

			Method readMethod = propertyDescriptor.getReadMethod();
			if (readMethod == null) {
				throw new IllegalStateException("No getter available for property " + property + " on " + beanClass);
			}
			return readMethod.invoke(bean);
		}

		private PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyname)
				throws IntrospectionException {
			PropertyDescriptor propertyDescriptor = getDescriptorFromCache(beanClass, propertyname);

			if (propertyDescriptor == null) {
				BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

				for (int i = 0; i < propertyDescriptors.length; i++) {
					PropertyDescriptor currentPropertyDescriptor = propertyDescriptors[i];
					if (currentPropertyDescriptor.getName().equals(propertyname)) {
						propertyDescriptor = currentPropertyDescriptor;
					}

				}
			}

			return propertyDescriptor;
		}

		private PropertyDescriptor getDescriptorFromCache(Class<?> beanClass, String propertyname) {
			// FIXME cache (in outer class)
			return null;
		}

		private Object getPropertyValue(CatalogEntry object, String fieldId)
				throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

			return bean.getProperty(object, fieldId);
		}

	}

	@Override
	public Session newSession(CatalogEntry sample) {
		FieldAccessSession session = new FieldAccessSession();
		session.resample(sample);
		return session;
	}

	@Override
	public Object getPropertyValue(CatalogDescriptor catalog, FieldDescriptor field, CatalogEntry object,
			DistributiedLocalizedEntry localizedObject, Session s) throws RuntimeException {
		// log.trace("[READ PROPERTY] {}.{}", catalog.getCatalog(),
		// field.getFieldId());
		FieldAccessSession session = (FieldAccessSession) s;
		/*
		 * if(s==null){ session = new FieldAccessSession(entry instanceof
		 * HasAccesablePropertyValues); }
		 */
		String fieldId = field.getFieldId();
		Object value = null;

		if (localizedObject != null && field.isLocalized()) {
			// support non-string values
			value = localizedObject.getLocalizedFieldValue(fieldId);
		}

		/*
		 * TODO cache in catalog descriptor
		 */
		if (value == null) {
			value = valuedoReadProperty(fieldId, session, object,false);
			if (value != null && field != null && CatalogEntry.LARGE_STRING_DATA_TYPE == field.getDataType()) {
				value = lsdao.getStringValue(value);
			}
		}

		// log.trace("[READ PROPERTY] value = {}", value);
		return value;
	}

	private Object valuedoReadProperty(String fieldId, FieldAccessSession session, CatalogEntry object, boolean silentFail) {
		if (session.accesible) {
			try {
				return doGetAccesibleProperty(object, fieldId);
			} catch (ClassCastException e) {
				if(silentFail){
					return null;
				}
				try {
					log.debug("Catalog Property Session Changed State");
					session.accesible = false;
					return goBeanGet(session, object, fieldId);
				} catch (IllegalArgumentException ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				} catch (IllegalAccessException ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				} catch (InvocationTargetException ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				} catch (IntrospectionException ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				} catch (NoSuchMethodException ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				}
			}

		} else {
			try {
				return goBeanGet(session, object, fieldId);
			} catch (IllegalArgumentException e) {
				if(silentFail){
					return null;
				}
				try {
					log.debug("Catalog Property Session Changed State");
					session.accesible = true;
					return doGetAccesibleProperty(object, fieldId);
				} catch (Exception ee) {

					log.debug("Access", e);
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				}
			} catch (IllegalAccessException e) {
				if(silentFail){
					return null;
				}
				try {
					session.accesible = true;
					return doGetAccesibleProperty(object, fieldId);
				} catch (Exception ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				}
			} catch (InvocationTargetException e) {
				if(silentFail){
					return null;
				}
				try {
					session.accesible = true;
					return doGetAccesibleProperty(object, fieldId);
				} catch (Exception ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				}
			} catch (IntrospectionException e) {
				if(silentFail){
					return null;
				}
				try {
					session.accesible = true;
					return doGetAccesibleProperty(object, fieldId);
				} catch (Exception ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				}
			} catch (NoSuchMethodException e) {
				if(silentFail){
					return null;
				}
				try {
					session.accesible = true;
					return doGetAccesibleProperty(object, fieldId);
				} catch (Exception ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				}
			}
		}
	}

	@Override
	public void setPropertyValue(CatalogDescriptor catalog, FieldDescriptor field, CatalogEntry object, Object value,
			Session s) throws Exception {
		// log.trace("[WRITE PROPERTY] {}.{}", catalog.getCatalog(),
		// field.getFieldId());
		// log.trace("[WRITE PROPERTY] value = {}", value);
		FieldAccessSession session = (FieldAccessSession) s;
		/*
		 * if(s==null){ session = new FieldAccessSession(entry instanceof
		 * HasAccesablePropertyValues); }
		 */
		String fieldId = field.getFieldId();

		if (value != null && field != null && CatalogEntry.LARGE_STRING_DATA_TYPE == field.getDataType()) {
			value = lsdao.processRawLongString((String) value);
		}
		setPropertyValue(catalog, fieldId, object, value, session);

	}

	@Override
	public CatalogEntry synthethize(CatalogDescriptor catalog, CatalogEntry synthesizedEntry,
			CatalogDescriptor targetCatalog, CatalogEntry entry, CatalogEntry old, CatalogActionContext context,
			Map<String, String> properties) throws Exception {

		context.setCatalog(targetCatalog.getCatalog());

		Session session = newSession(synthesizedEntry);
		Collection<FieldDescriptor> fields = targetCatalog.getFieldsValues();
		String fieldId;
		String token;
		Object fieldValue;
		Session lowSession = newSession(entry);

		for (FieldDescriptor field : fields) {
			fieldId = field.getFieldId();
			if (!CatalogEntry.ID_FIELD.equals(fieldId) && !field.isEphemeral()) {
				token = properties.get(fieldId);
				if (token != null) {
					fieldValue = synthethizeFieldValue(token, entry, old, catalog, context, lowSession, field);
					setPropertyValue(targetCatalog, field, synthesizedEntry, fieldValue, session);
				}
			}

		}
		return synthesizedEntry;
	}

	@Override
	public Object synthethizeFieldValue(String token, CatalogEntry entry, CatalogEntry old, CatalogDescriptor catalog,
			CatalogActionContext context, Session session, FieldDescriptor field) throws Exception {
		if (!field.isMultiple() && !field.isKey() && !field.isEphemeral()
				&& (CatalogEntry.STRING_DATA_TYPE == field.getDataType()
						|| CatalogEntry.LARGE_STRING_DATA_TYPE == field.getDataType())
				&& token.startsWith("template:")) {
			StringBuilder out = new StringBuilder(token.length());
			writeDocument(token, out, entry, old, catalog, context, session, field);
			return out.toString();
		} else {
			return readPathVariable(token, entry, old, catalog, context, session, field);
		}

	}

	private Object readPathVariable(String token, CatalogEntry entry, CatalogEntry old, CatalogDescriptor catalog,
			CatalogActionContext context, Session session, FieldDescriptor field) throws Exception {
		String[] pointer = token.split("\\.");
		String source = pointer[0];

		if (source.equals(SOURCE_ENTRY)) {
			FieldDescriptor fieldD = catalog.getFieldDescriptor(pointer[1]);
			if (fieldD == null) {
				throw new IllegalArgumentException("Trigger invokes nonexistent field " + pointer[1]);
			}
			return getPropertyValue(catalog, fieldD, entry, null, session);
		} else if (source.equals(SOURCE_CONTEXT)) {
			// TODO: GAE version only :: context should not expose sensitive
			// variables and apache web context maybe does?
			Context temp = context;
			;
			for (int i = 1; i < pointer.length; i++) {
				if (i == pointer.length - 1) {
					return temp.get(pointer[i]);
				} else {
					temp = (Context) temp.get(pointer[i]);
				}
			}
		} else if (source.equals(SOURCE_OLD)) {
			FieldDescriptor fieldD = catalog.getFieldDescriptor(pointer[1]);
			if (fieldD == null) {
				return null;
			} else {
				return getPropertyValue(catalog, fieldD, old, null, session);
			}
		}
		if (source.equals(CatalogActionRequest.CATALOG_FIELD)) {
			return catalog.getCatalog();
		} else {
			if (field != null && field.getDataType() == CatalogEntry.STRING_DATA_TYPE) {
				return source;
			} else if (field != null && field.getDataType() == CatalogEntry.INTEGER_DATA_TYPE) {
				try {
					return Integer.parseInt(source);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("INVALID SOURCE:" + source);
				}
			} else {
				throw new IllegalArgumentException("INVALID SOURCE:" + source);
			}

		}
	}

	private void writeDocument(String rawHtml, StringBuilder out, CatalogEntry entry, CatalogEntry old,
			CatalogDescriptor catalog, CatalogActionContext context, Session session, FieldDescriptor field)
			throws Exception {

		Matcher matcher = pattern.matcher(rawHtml);
		if (matcher.find()) {
			matcher.reset();
			int start;
			int end;
			int currentIndex = 0;
			String rawToken;
			String token;
			while (matcher.find()) {
				start = matcher.start();
				end = matcher.end();
				out.append(rawHtml.substring(currentIndex, start));
				rawToken = matcher.group();
				token = digestToken(rawToken, entry, old, catalog, context, session, field);
				out.append(token);
				currentIndex = end;
			}
			if (currentIndex < rawHtml.length()) {
				out.append(rawHtml.substring(currentIndex, rawHtml.length()));
			}
		} else {
			out.append(rawHtml);
		}
	}

	private String digestToken(String rawToken, CatalogEntry entry, CatalogEntry old, CatalogDescriptor catalog,
			CatalogActionContext context, Session session, FieldDescriptor field) throws Exception {
		String token = rawToken.substring(2, rawToken.length() - 1);
		return String.valueOf(readPathVariable(token, entry, old, catalog, context, session, field));
	}

	private void doSetAccesibleProperty(CatalogEntry object, String fieldId, Object value) {
		HasAccesablePropertyValues entry = (HasAccesablePropertyValues) object;
		entry.setPropertyValue(value, fieldId);
	}

	private Object doGetAccesibleProperty(CatalogEntry object, String fieldId) {
		HasAccesablePropertyValues entry = (HasAccesablePropertyValues) object;
		return entry.getPropertyValue(fieldId);
	}

	private Object goBeanGet(FieldAccessSession session, CatalogEntry object, String fieldId)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IntrospectionException,
			NoSuchMethodException {
		return session.getPropertyValue(object, fieldId);
	}

	private void doBeanSet(FieldAccessSession session, CatalogEntry object, String fieldId, Object value)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		bean.setProperty(object, fieldId, value);
	}

	@Override
	public boolean isWriteableProperty(String property, CatalogEntry entry, Session s) {
		FieldAccessSession session = (FieldAccessSession) s;
		if(session.accesible){
			return true;
		}
		return bean.isWriteable(entry, property) ;
	}

	@Override
	public void setPropertyValue(CatalogDescriptor mainCatalog, String fieldId, CatalogEntry object, Object value,
			Session s) throws Exception {
		FieldAccessSession session = (FieldAccessSession) s;
		if (session.accesible) {
			try {
				doSetAccesibleProperty(object, fieldId, value);
			} catch (ClassCastException e) {
				session.accesible = false;
				try {
					doBeanSet(session, object, fieldId, value);
				} catch (IllegalAccessException ee) {
					throw new IllegalArgumentException("access field " + fieldId, ee);
				} catch (InvocationTargetException ee) {
					throw new IllegalArgumentException("access field " + fieldId, ee);
				}

			}
		} else {
			try {
				// TODO this shoudl not succed when a HasProperties is passed,
				// and yet it does succedd without altering content
				doBeanSet(session, object, fieldId, value);
			} catch (IllegalAccessException e) {
				session.accesible = true;
				try {
					doSetAccesibleProperty(object, fieldId, value);
				} catch (ClassCastException ee) {
					throw new IllegalArgumentException("access field " + fieldId, ee);
				}

			} catch (InvocationTargetException e) {
				session.accesible = true;
				try {
					doSetAccesibleProperty(object, fieldId, value);
				} catch (ClassCastException ee) {
					throw new IllegalArgumentException("access field " + fieldId, ee);
				}

			}

		}
	}

	/*
	 * INHERITANCE
	 */

	@Override
	public CatalogEntry synthesizeCatalogObject(CatalogEntry source, CatalogDescriptor catalog,
			boolean excludeInherited, Session session, CatalogActionContext context) throws Exception {
		context.getNamespaceContext().setNamespace(context);
		CatalogEntry target = synthesize(catalog);

		addPropertyValues(source, target, catalog, excludeInherited, session,(DistributiedLocalizedEntry) (source instanceof DistributiedLocalizedEntry ? source : null));
		context.getNamespaceContext().unsetNamespace(context);
		return target;
	}

	@Override
	public void addPropertyValues(CatalogEntry source, CatalogEntry target, CatalogDescriptor catalog,
			boolean excludeInherited, Session session, DistributiedLocalizedEntry localizedObject) throws Exception {
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

					value = getPropertyValue(catalog, field, source, localizedObject, session);
					if (value != null) {
						setPropertyValue(catalog, field, target, value, session);
					}
				}
			}
		}
	}

	@Override
	public Object getAllegedParentId(CatalogEntry result, Session session) {
		return valuedoReadProperty(this.ancestorIdField, (FieldAccessSession) session, result,false);
	}

	@Override
	public void addInheritedValuesToChild(CatalogEntry parentEntity, CatalogEntry regreso, Session session,
			CatalogDescriptor childCatalog) throws Exception {
		Collection<FieldDescriptor> fields = childCatalog.getFieldsValues();
		for (FieldDescriptor field : fields) {
			if (field.isInherited()) {
				setPropertyValue(childCatalog, field, regreso, getPropertyValue(
						childCatalog/*
									 * actually belongs to a totally different
									 * catalog, bus this implementation diesnt
									 * really care so no need to FIXME ?
									 */, field, regreso,
						(DistributiedLocalizedEntry) (parentEntity instanceof DistributiedLocalizedEntry ? parentEntity
								: null),
						session), session);
			}
		}
	}

	@Override
	public CatalogEntry readEntry(CatalogDescriptor catalogId, Object parentId, CatalogActionContext readParentEntry)
			throws Exception {
		readParentEntry.setCatalogDescriptor(catalogId);
		readParentEntry.setEntry(parentId);
		readParentEntry.setAction(CatalogActionRequest.READ_ACTION);
		readParentEntry.setEntryValue(null);
		readParentEntry.setFilter(null);

		readParentEntry.getCatalogManager().getRead().execute(readParentEntry);

		return readParentEntry.getResult();
	}
	
	@Override
	public CatalogEntry synthesizeChildEntity(Object parentEntityId, CatalogEntry o, Session session, CatalogDescriptor catalog, CatalogActionContext context) throws Exception {
		CatalogEntry childEntity = synthesizeCatalogObject(o, catalog, true, session, context);
		setPropertyValue(catalog, this.ancestorIdField, childEntity, parentEntityId, session);
		return childEntity;
	}

	@Override
	public void processChild(CatalogEntry childEntity, CatalogDescriptor parentCatalogId, Object parentEntityId, CatalogActionContext readParentEntry,
			CatalogDescriptor catalog, Session session) throws Exception {

		CatalogEntry parentEntity = readEntry(parentCatalogId, parentEntityId, readParentEntry);
		// add inherited values to child Entity
		if (parentEntity instanceof DistributiedLocalizedEntry) {
			DistributiedLocalizedEntry localized=(DistributiedLocalizedEntry) parentEntity;
			addPropertyValues(parentEntity, childEntity, parentCatalogId, false, session, localized);
		} else {
			addPropertyValues(parentEntity, childEntity, parentCatalogId, false, session, null);
		}

	}

	@Override
	public Object getPropertyForeignKeyValue(CatalogDescriptor catalogDescriptor, FieldDescriptor field, CatalogEntry e,
			Session session) {
		return valuedoReadProperty(field.getFieldId()+(field.isMultiple()?CatalogEntry.MULTIPLE_FOREIGN_KEY:CatalogEntry.FOREIGN_KEY), (FieldAccessSession) session, e, true);
	}
	

}
