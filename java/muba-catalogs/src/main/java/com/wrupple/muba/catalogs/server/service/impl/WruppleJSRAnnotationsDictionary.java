package com.wrupple.muba.catalogs.server.service.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.beanutils.ConvertUtils;

import com.wrupple.muba.catalogs.server.annotations.CAPTCHA;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.JSRAnnotationsDictionary;
import com.wrupple.vegetate.domain.FieldConstraint;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.server.domain.ValidationExpression;
import com.wrupple.vegetate.server.domain.annotations.CatalogFieldValues;
import com.wrupple.vegetate.server.domain.annotations.CatalogKey;

@Singleton
public class WruppleJSRAnnotationsDictionary implements JSRAnnotationsDictionary {

	private Map<String, ValidationExpression> map;
	private List<String> asStringList;
	private Provider<DatabasePlugin> registryP;

	@Inject
	public WruppleJSRAnnotationsDictionary(Provider<DatabasePlugin> registryP) {
		this.registryP = registryP;

	}

	@Override
	public Annotation buildAnnotation(FieldConstraint constraint) {
		String name = constraint.getConstraint();
		if (map == null) {
			initialize();
		}
		ValidationExpression registry = map.get(name);
		if (registry == null) {
			throw new IllegalArgumentException("Unrecognized constraint: " + name);
		} else {
			Class<? extends Annotation> clazz = registry.clazz;
			Annotation annotation = Defaults.of(clazz, constraint);
			return annotation;
		}

	}

	@Override
	public List<String> getAvailableAnnotationNames() {
		if (map == null) {
			initialize();
		}
		return Collections.unmodifiableList(asStringList);
	}

	@Override
	public synchronized void initialize() {
		// needs to be synchronized so as not to invoke server modules too early
		if (map == null) {
			map = new LinkedHashMap<String, ValidationExpression>();
			map.put(NotNull.class.getSimpleName(), new ValidationExpression(NotNull.class, FieldConstraint.EVALUATING_VARIABLE,
					FieldConstraint.EVALUATING_VARIABLE + "==null?\"{validator.null}\":null"));
			// fields must declare a "value" property specifiying min or max
			// value
			map.put(Min.class.getSimpleName(), new ValidationExpression(Min.class, FieldConstraint.EVALUATING_VARIABLE + ",value",
					FieldConstraint.EVALUATING_VARIABLE + "<value?\"{validator.min}\":null"));
			map.put(Max.class.getSimpleName(), new ValidationExpression(Max.class, FieldConstraint.EVALUATING_VARIABLE + ",value",
					FieldConstraint.EVALUATING_VARIABLE + ">value?\"{validator.max}\":null"));
			map.put(CAPTCHA.class.getSimpleName(), new ValidationExpression(CAPTCHA.class, FieldConstraint.EVALUATING_VARIABLE,
					FieldConstraint.EVALUATING_VARIABLE + "==null?\"{captcha.message}\":null"));

			CatalogPlugin[] modules = this.registryP.get().getPlugins();

			ValidationExpression[] exprs;
			for (CatalogPlugin module : modules) {
				
				exprs = module.getValidations();
				
				if(exprs!=null){
					for(ValidationExpression expr :exprs){
						map.put(expr.getName(), expr);
					}
					}
				}

			asStringList = new ArrayList<String>(map.keySet());

		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public CatalogKey buildCatalogKeyValidation(FieldDescriptor field) {
		Map properties = Collections.singletonMap("foreignCatalog", field.getForeignCatalogName());

		CatalogKey annotation = Defaults.of(CatalogKey.class, properties);
		return annotation;

	}

	@Override
	@SuppressWarnings("unchecked")
	public CatalogFieldValues buildNormalizationValidation(FieldDescriptor field) {

		Map properties = Collections.singletonMap("defaultValueOptions", field.getDefaultValueOptions().toArray());

		CatalogFieldValues annotation = Defaults.of(CatalogFieldValues.class, properties);
		return annotation;
	}


	private static class Defaults implements InvocationHandler {
		private final Class<?> type;
		private final Map<String, Object> properties;

		public Defaults(Class<?> annotation, FieldConstraint constraint) {
			this.type = annotation;
			if (constraint.getProperties() == null || constraint.getProperties().isEmpty()) {
				properties = null;
			} else {
				properties = parseProperties(constraint.getProperties());
			}
		}

		public Defaults(Class<?> annotation, Map<String, Object> properties) {
			this.type = annotation;
			this.properties = properties;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Class<?> declaringClass = method.getDeclaringClass();
			Class<?> returnType = method.getReturnType();
			String methodName = method.getName();
			if (declaringClass.equals(Annotation.class) && returnType.equals(java.lang.Class.class) && methodName.equals("annotationType")) {
				return type;
			} else if (type.equals(declaringClass)) {
				// if the method is declared by the working annotation (we care
				// about it)
				Object value = properties.get(methodName);
				if (value != null) {
					// we declared a constraint value for this method
					if (returnType.equals(value.getClass())) {
						// and it's in the same return type!
						return value;
					} else {
						// we need to convert it
						value = ConvertUtils.convert(value, returnType);
						// and store it, for future use
						properties.put(methodName, value);
						// and return it
						return value;
					}
				}
			}
			return method.getDefaultValue();
		}

		@SuppressWarnings("unchecked")
		public static <A extends Annotation> A of(Class<A> annotation, FieldConstraint constraint) {
			return (A) Proxy.newProxyInstance(annotation.getClassLoader(), new Class[] { annotation }, new Defaults(annotation, constraint));
		}

		@SuppressWarnings("unchecked")
		public static <A extends Annotation> A of(Class<A> annotation, Map<String, Object> props) {
			return (A) Proxy.newProxyInstance(annotation.getClassLoader(), new Class[] { annotation }, new Defaults(annotation, props));
		}

		private Map<String, Object> parseProperties(List<String> rawProperties) {
			Map<String, Object> regreso = new HashMap<String, Object>(rawProperties.size());
			String value;
			String element;
			int split;
			for (String property : rawProperties) {
				split = property.indexOf('=');
				if (split > 0) {
					element = property.substring(0, split);
					value = property.substring(split + 1, property.length());
					regreso.put(element, value);
				}
			}
			return regreso;
		}

	}

}
