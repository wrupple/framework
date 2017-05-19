package com.wrupple.muba.desktop.server.service.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.Script;

import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.HasAccesablePropertyValues;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.catalogs.server.service.LocalizationStrategy.LocalizedEntity;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;

@Singleton
public class CatalogPropertyAccessorImpl implements CatalogPropertyAccesor {

	private LargeStringFieldDataAccessObject lsdao;
	private PersistentCatalogEntityFactory factory;
	private final Pattern pattern;
	private final Provider<JexlEngine> jexl;

	@Inject
	public CatalogPropertyAccessorImpl(@Named("template.pattern") Pattern pattern, Provider<JexlEngine> jexl, LargeStringFieldDataAccessObject lsdao,
			PersistentCatalogEntityFactory factory) {
		this.lsdao = lsdao;
		this.pattern = pattern;
		this.jexl = jexl;
		this.factory = factory;
	}

	@Override
	public CatalogEntry synthesize(CatalogDescriptor catalog) throws Exception {
		if (catalog.getClazz() == null || PersistentCatalogEntity.class.getCanonicalName().equals(catalog.getClazz())) {

			HasAccesablePropertyValues target = factory.newEntity(catalog);

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

		Collection<FieldDescriptor> fields = catalog.getOwnedFieldsValues();

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

	private static class FieldAccessSession implements Session {
		boolean accesible;

		@Override
		public void resample(CatalogEntry sample) {
			if (sample == null) {
				accesible = true;
			} else {
				accesible = sample instanceof HasAccesablePropertyValues;
			}

		}

		private Object getPropertyValue(Object bean, String property)
				throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
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

		private PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyname) throws IntrospectionException {
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

	}

	@Override
	public Session newSession(CatalogEntry sample) {
		FieldAccessSession session = new FieldAccessSession();
		session.resample(sample);
		return session;
	}

	@Override
	public Object getPropertyValue(CatalogDescriptor catalog, FieldDescriptor field, CatalogEntry object, LocalizedEntity localizedObject, Session s) {
		System.err.println("[READ]" + catalog.getCatalogId() + "." + field.getFieldId());
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
		 * TODO use PropertyUtilsBean (bean utils) and dump srping
		 * //java.lang.ClassNotFoundException:
		 * org.apache.commons.collections.Transformer TODO cache in catalog
		 * descriptor, update CatalogDescriptorMixin JSONIgnore
		 */
		if (value == null) {
			if (session.accesible) {
				try {
					value = doGetAccesibleProperty(object, fieldId);
				} catch (ClassCastException e) {
					try {
						session.accesible = false;
						value = goBeanGet(session, object, fieldId);
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | IntrospectionException ee) {
						throw new IllegalArgumentException("access field " + fieldId + "@" + (catalog != null ? catalog.getCatalogId() : null), ee);
					}
				}

			} else {
				try {
					value = goBeanGet(session, object, fieldId);
				} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
					try {
						session.accesible = true;
						value = doGetAccesibleProperty(object, fieldId);
					} catch (Exception ee) {
						throw new IllegalArgumentException("access field " + fieldId + "@" + (catalog != null ? catalog.getCatalogId() : null), ee);
					}
				}
			}
			if (value != null && field != null && CatalogEntry.LARGE_STRING_DATA_TYPE == field.getDataType()) {
				value = lsdao.getStringValue(value);
			}
		}
		System.err.println("\t" + value);
		return value;
	}

	@Override
	public void setPropertyValue(CatalogDescriptor catalog, FieldDescriptor field, CatalogEntry object, Object value, Session s) {
		System.err.println("[Write]" + catalog.getCatalogId() + "." + field.getFieldId() + "=" + value);
		FieldAccessSession session = (FieldAccessSession) s;
		/*
		 * if(s==null){ session = new FieldAccessSession(entry instanceof
		 * HasAccesablePropertyValues); }
		 */
		String fieldId = field.getFieldId();

		if (value != null && field != null && CatalogEntry.LARGE_STRING_DATA_TYPE == field.getDataType()) {
			value = lsdao.processRawLongString((String) value);
		}

		if (session.accesible) {
			try {
				doSetAccesibleProperty(object, fieldId, value);
			} catch (ClassCastException e) {
				session.accesible = false;
				try {
					doBeanSet(session, object, fieldId, value);
				} catch (IllegalAccessException | InvocationTargetException ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + catalog != null ? catalog.getCatalogId() : null, ee);
				}

			}
		} else {
			try {
				// TODO this shoudl not succed when a HasProperties is passed,
				// and yet it does succedd without altering content
				doBeanSet(session, object, fieldId, value);
			} catch (IllegalAccessException | InvocationTargetException e) {
				session.accesible = true;
				try {
					doSetAccesibleProperty(object, fieldId, value);
				} catch (ClassCastException ee) {
					throw new IllegalArgumentException("access field " + fieldId + "@" + catalog != null ? catalog.getCatalogId() : null, ee);
				}

			}

		}
	}

	@Override
	public void evaluate(String jexlExp, CatalogEntry entry, CatalogEntry old, CatalogExcecutionContext context) {
		// Create or retrieve a JexlEngine
		// Create an expression object
		Script e = jexl.get().createScript(jexlExp);

		// Create a context and add data
		JexlContext jc = new MapContext();
		// TODO context.transaction may expose unsecure methods
		jc.set("context", context);
		jc.set("entry", entry);
		jc.set("old", old);

		// Now evaluate the expression, getting the result
		/* Object o = */e.execute(jc);

	}

	@Override
	public void evaluate(String jexlExp, Map<String, Object> payload, CatalogExcecutionContext context) {
		Script e = jexl.get().createScript(jexlExp);

		JexlContext jc = new MapContext();
		// TODO context.transaction may expose unsecure methods
		jc.set("context", context);
		jc.set("payload", payload);
		e.execute(jc);
	}

	@Override
	public CatalogEntry synthethize(CatalogDescriptor catalog, CatalogEntry synthesizedEntry, CatalogDescriptor targetCatalog, CatalogEntry entry,
			CatalogEntry old, CatalogExcecutionContext context, Map<String, String> properties) throws Exception {

		context.setCatalogDescriptor(targetCatalog);

		Session session = newSession(synthesizedEntry);
		Collection<FieldDescriptor> fields = targetCatalog.getOwnedFieldsValues();
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
	public Object synthethizeFieldValue(String token, CatalogEntry entry, CatalogEntry old, CatalogDescriptor catalog, CatalogExcecutionContext context,
			Session session, FieldDescriptor field) throws IOException {
		if (!field.isMultiple() && !field.isKey() && !field.isEphemeral()
				&& (CatalogEntry.STRING_DATA_TYPE == field.getDataType() || CatalogEntry.LARGE_STRING_DATA_TYPE == field.getDataType())
				&& token.startsWith("template:")) {
			StringBuilder out = new StringBuilder(token.length());
			writeDocument(token, out, entry, old, catalog, context, session, field);
			return out.toString();
		} else {
			return readPathVariable(token, entry, old, catalog, context, session, field);
		}

	}

	private Object readPathVariable(String token, CatalogEntry entry, CatalogEntry old, CatalogDescriptor catalog, CatalogExcecutionContext context,
			Session session, FieldDescriptor field) throws IOException {
		String[] pointer = token.split("\\.");
		String source = pointer[0];

		if (source.equals(CatalogActionTrigger.SOURCE_ENTRY)) {
			FieldDescriptor fieldD = catalog.getFieldDescriptor(pointer[1]);
			if (fieldD == null) {
				throw new IllegalArgumentException("Trigger invokes nonexistent field " + pointer[1]);
			}
			return getPropertyValue(catalog, fieldD, entry, null, session);
		} else if (source.equals(CatalogActionTrigger.SOURCE_CONTEXT)) {
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
		} else if (source.equals(CatalogActionTrigger.SOURCE_OLD)) {
			FieldDescriptor fieldD = catalog.getFieldDescriptor(pointer[1]);
			if (fieldD == null) {
				return null;
			} else {
				return getPropertyValue(catalog, fieldD, old, null, session);
			}
		}
		if (source.equals(CatalogActionRequest.CATALOG_ID_PARAMETER)) {
			return catalog.getCatalogId();
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

	private void writeDocument(String rawHtml, StringBuilder out, CatalogEntry entry, CatalogEntry old, CatalogDescriptor catalog,
			CatalogExcecutionContext context, Session session, FieldDescriptor field) throws IOException {

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

	private String digestToken(String rawToken, CatalogEntry entry, CatalogEntry old, CatalogDescriptor catalog, CatalogExcecutionContext context,
			Session session, FieldDescriptor field) throws IOException {
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
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IntrospectionException {
		return session.getPropertyValue(object, fieldId);
	}

	private void doBeanSet(FieldAccessSession session, CatalogEntry object, String fieldId, Object value)
			throws IllegalAccessException, InvocationTargetException {
		BeanUtils.setProperty(object, fieldId, value);
	}

}
