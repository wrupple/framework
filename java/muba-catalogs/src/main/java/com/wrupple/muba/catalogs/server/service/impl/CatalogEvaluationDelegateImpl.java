package com.wrupple.muba.catalogs.server.service.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
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

	private final String TOKEN_SPLITTER;

	private final String ancestorIdField;
	private LargeStringFieldDataAccessObject lsdao;
	private Provider<PersistentCatalogEntity> factory;
	private final Pattern pattern;
	private final PropertyUtilsBean bean;

	@Inject
	public CatalogEvaluationDelegateImpl(@Named("template.token.splitter") String splitter /* "\\." */,
			@Named("template.pattern") Pattern pattern/** "\\$\\{([A-Za-z0-9]+\\.){0,}[A-Za-z0-9]+\\}" */
			,@Named("catalog.ancestorKeyField")String ancestorIdField, LargeStringFieldDataAccessObject lsdao, Provider<PersistentCatalogEntity> factory) {
		this.ancestorIdField = ancestorIdField;
		this.TOKEN_SPLITTER = splitter;
		this.lsdao = lsdao;
		this.pattern = pattern;
		bean = new PropertyUtilsBean();
		this.factory = factory;
	}

	@Override
	public CatalogEntry catalogCopy(CatalogDescriptor catalog, CatalogEntry entry) throws ReflectiveOperationException {
		CatalogEntry copy = synthesize(catalog);

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

	@Override
	public CatalogEntry synthesize(CatalogDescriptor catalog) throws ReflectiveOperationException {
		if (catalog.getClazz() == null || PersistentCatalogEntity.class.equals(catalog.getClazz())) {

			PersistentCatalogEntity target = factory.get();
			target.initCatalog(catalog);
			return target;
		} else {
			Class<?> clazz = catalog.getClazz();
			CatalogEntry target = (CatalogEntry) clazz.newInstance();
			return target;
		}
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
		// log.trace("[READ PROPERTY] {}.{}", catalog.getDistinguishedName(),
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
			value = valuedoReadProperty(fieldId, session, object, false);
			if (value != null && field != null && CatalogEntry.LARGE_STRING_DATA_TYPE == field.getDataType()) {
				value = lsdao.getStringValue(value);
			}
		}

		// log.trace("[READ PROPERTY] value = {}", value);
		return value;
	}

	private Object valuedoReadProperty(String fieldId, FieldAccessSession session, CatalogEntry object,
			boolean silentFail) {
		if (session.accesible) {
			try {
				return doGetAccesibleProperty(object, fieldId);
			} catch (ClassCastException e) {
				if (silentFail) {
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
				if (silentFail) {
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
				if (silentFail) {
					return null;
				}
				try {
					session.accesible = true;
					return doGetAccesibleProperty(object, fieldId);
				} catch (Exception ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				}
			} catch (InvocationTargetException e) {
				if (silentFail) {
					return null;
				}
				try {
					session.accesible = true;
					return doGetAccesibleProperty(object, fieldId);
				} catch (Exception ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				}
			} catch (IntrospectionException e) {
				if (silentFail) {
					return null;
				}
				try {
					session.accesible = true;
					return doGetAccesibleProperty(object, fieldId);
				} catch (Exception ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + object.getCatalogType(), ee);
				}
			} catch (NoSuchMethodException e) {
				if (silentFail) {
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
			Session s) throws ReflectiveOperationException {
		// log.trace("[WRITE PROPERTY] {}.{}", catalog.getDistinguishedName(),
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
		if (session.accesible) {
			return true;
		}
		return bean.isWriteable(entry, property);
	}

	@Override
	public void setPropertyValue(CatalogDescriptor mainCatalog, String fieldId, CatalogEntry object, Object value,
			Session s) throws ReflectiveOperationException {
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

		addPropertyValues(source, target, catalog, excludeInherited, session,
				(DistributiedLocalizedEntry) (source instanceof DistributiedLocalizedEntry ? source : null));
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
		return valuedoReadProperty(this.ancestorIdField, (FieldAccessSession) session, result, false);
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

		return readParentEntry.getEntryResult();
	}

	@Override
	public CatalogEntry synthesizeChildEntity(Object parentEntityId, CatalogEntry o, Session session,
			CatalogDescriptor catalog, CatalogActionContext context) throws Exception {
		CatalogEntry childEntity = synthesizeCatalogObject(o, catalog, true, session, context);
		setPropertyValue(catalog, this.ancestorIdField, childEntity, parentEntityId, session);
		return childEntity;
	}

	@Override
	public void processChild(CatalogEntry childEntity, CatalogDescriptor parentCatalogId, Object parentEntityId,
			CatalogActionContext readParentEntry, CatalogDescriptor catalog, Session session) throws Exception {

		CatalogEntry parentEntity = readEntry(parentCatalogId, parentEntityId, readParentEntry);
		// add inherited values to child Entity
		if (parentEntity instanceof DistributiedLocalizedEntry) {
			DistributiedLocalizedEntry localized = (DistributiedLocalizedEntry) parentEntity;
			addPropertyValues(parentEntity, childEntity, parentCatalogId, false, session, localized);
		} else {
			addPropertyValues(parentEntity, childEntity, parentCatalogId, false, session, null);
		}

	}

	@Override
	public Object getPropertyForeignKeyValue(CatalogDescriptor catalogDescriptor, FieldDescriptor field, CatalogEntry e,
			Session session) {
		return valuedoReadProperty(
				field.getFieldId()
						+ (field.isMultiple() ? CatalogEntry.MULTIPLE_FOREIGN_KEY : CatalogEntry.FOREIGN_KEY),
				(FieldAccessSession) session, e, true);
	}

	@Override
	public String getDenormalizedFieldValue(CatalogEntry client, String fieldId, Session session,
			CatalogActionContext context) throws Exception {
		String catalogid = client.getCatalogType();
		CatalogDescriptor type = context.getCatalogManager().getDescriptorForName(catalogid, context);
		FieldDescriptor field = type.getFieldDescriptor(fieldId);
		if (field == null) {
			throw new IllegalArgumentException("unknown field :" + fieldId);
		}

		if (field.getDefaultValueOptions() != null && !field.getDefaultValueOptions().isEmpty()
				&& field.getDataType() == CatalogEntry.INTEGER_DATA_TYPE) {
			Integer index = (Integer) getPropertyValue(type, field, client, null, session);
			if (index != null) {
				return field.getDefaultValueOptions().get(index);
			}
		}
		return null;
	}

	@Override
	public void evalTemplate(String template, PrintWriter out, String language, CatalogActionContext context) {
		log.trace("[WRITE DOCUMENT]");
		Matcher matcher = pattern.matcher(template);
		if (matcher.find()) {
			matcher.reset();
			int start;
			int end;
			int currentIndex = 0;
			String rawToken;
			while (matcher.find()) {
				start = matcher.start();
				if (start > 0 && template.charAt(start) != '\\') {
					end = matcher.end();
					out.println(template.substring(currentIndex, start));
					rawToken = matcher.group();
					try {
						out.print(synthethizeFieldValue(rawToken, context));
					} catch (Exception e) {
						out.println("Error processing token : " + rawToken);
					}
					currentIndex = end;
				}
			}
			if (currentIndex < template.length()) {
				out.println(template.substring(currentIndex, template.length()));
			}
		} else {
			out.println(template);
		}
	}

	@Override
	public Object synthethizeFieldValue(String token, CatalogActionContext context) throws Exception {
		String[] tokens = token.split(TOKEN_SPLITTER);
		ExcecutionContext excecutionContext = context.getExcecutionContext();

		ExcecutionContext childContext = excecutionContext.spawnChild();
		childContext.setSentence(tokens);
		childContext.setServiceContract(context);

		childContext.process();

		// TODO validating only the sentence would be useful cuz we wouldnt have
		// to waste resources on creating a useless child context, see
		// ExcecutionCOntext.hashCode

		if (childContext.getConstraintViolations() == null || childContext.getConstraintViolations().isEmpty()) {
			log.debug("Processing token {}, ", token);
			return childContext.getResult();
		} else {
			log.warn("Unable to process token {}, ", token);
			return token;
		}

	}

}
