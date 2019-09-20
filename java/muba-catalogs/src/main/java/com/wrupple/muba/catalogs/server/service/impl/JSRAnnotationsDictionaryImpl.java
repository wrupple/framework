package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.ConstraintImpl;
import com.wrupple.muba.catalogs.server.annotations.CAPTCHA;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.JSRAnnotationsDictionary;
import com.wrupple.muba.event.domain.Constraint;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import org.apache.commons.beanutils.ConvertUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
@Singleton
public class JSRAnnotationsDictionaryImpl implements JSRAnnotationsDictionary {
    private final Provider<Object> pluginProvider;



	/*
	 * validation
	 */

    private Map<String, ValidationExpression> map;

    private ArrayList<String> asStringList;



    @Inject
    public JSRAnnotationsDictionaryImpl(@Named("catalog.plugins") Provider<Object> pluginProvider) {
        this.pluginProvider=pluginProvider;

        

    }

    @Override
    public Annotation buildAnnotation(Constraint constraint) {
        String name = constraint.getDistinguishedName();
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
    public Constraint buildConstraint(Annotation annotation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<? extends Annotation> clazz = annotation.annotationType();
        String name = clazz.getSimpleName();
        if (map == null) {
            initialize();
        }
        ValidationExpression registry = map.get(name);
        if (registry == null ) {
            return null;
        } else {
            List<String> list = registry.getGivenVariable();
            ConstraintImpl constraint = new ConstraintImpl();
            constraint.setDistinguishedName(name);
            if(list!=null && !list.isEmpty()){
                List<String> properties = new ArrayList<String>(list.size());
                for(String givenValue : list){
                    properties.add(givenValue+"="+getGivenValue(annotation,givenValue));
                }
                constraint.setProperties(properties);
            }
            return constraint;
        }
    }

    private String getGivenValue(Annotation annotation, String givenValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object r = annotation.getClass().getMethod(givenValue).invoke(annotation);
        if(r==null){
            return null;
        }else{
            return r.toString();
        }
    }

    @Override
    public List<String> getAvailableAnnotationNames() {
        //FIXME expose as catalog
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
            map.put(NotNull.class.getSimpleName(),
                    new ValidationExpression(NotNull.class, Constraint.EVALUATING_VARIABLE,
                            Constraint.EVALUATING_VARIABLE + "==null?\"{validator.null}\":null",new String[0]));
            map.put(Pattern.class.getSimpleName(),
                    new ValidationExpression(Pattern.class, Constraint.EVALUATING_VARIABLE+ ",value",
                            "matches("+Constraint.EVALUATING_VARIABLE + ",regexp)?\"{validator.null}\":null","regexp"));
            // fields must declare a "value" property specifiying min or max
            // value
            map.put(Min.class.getSimpleName(),
                    new ValidationExpression(Min.class, Constraint.EVALUATING_VARIABLE + ",value",
                            Constraint.EVALUATING_VARIABLE + "<value?\"{validator.min}\":null"));
            map.put(Max.class.getSimpleName(),
                    new ValidationExpression(Max.class, Constraint.EVALUATING_VARIABLE + ",value",
                            Constraint.EVALUATING_VARIABLE + ">value?\"{validator.max}\":null"));
            map.put(CAPTCHA.class.getSimpleName(),
                    new ValidationExpression(CAPTCHA.class, Constraint.EVALUATING_VARIABLE,
                            Constraint.EVALUATING_VARIABLE + "==null?\"{captcha.message}\":null",new String[0]));

            CatalogPlugin[] catalogPlugins = (CatalogPlugin[]) pluginProvider.get();




            if (catalogPlugins != null) {
                ValidationExpression[] exprs;
                for (CatalogPlugin plugin : catalogPlugins) {
                    exprs = plugin.getValidations();
                    if (exprs != null) {
                        for (ValidationExpression expr : exprs) {
                            map.put(expr.getName(), expr);
                        }
                    }
                }

            }

            asStringList = new ArrayList<String>(map.keySet());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ForeignKey buildCatalogKeyValidation(FieldDescriptor field) {
        Map properties = Collections.singletonMap("foreignCatalog", field.getCatalog());

        ForeignKey annotation = Defaults
                .of(ForeignKey.class, properties);
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

        public Defaults(Class<?> annotation, Constraint constraint) {
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
            if (declaringClass.equals(Annotation.class) && returnType.equals(java.lang.Class.class)
                    && methodName.equals("annotationType")) {
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
        public static <A extends Annotation> A of(Class<A> annotation, Constraint constraint) {
            return (A) Proxy.newProxyInstance(annotation.getClassLoader(), new Class[] { annotation },
                    new Defaults(annotation, constraint));
        }

        @SuppressWarnings("unchecked")
        public static <A extends Annotation> A of(Class<A> annotation, Map<String, Object> props) {
            return (A) Proxy.newProxyInstance(annotation.getClassLoader(), new Class[] { annotation },
                    new Defaults(annotation, props));
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
                    value = property.substring(split + 1, property.length() - 1);
                    regreso.put(element, value);
                }
            }
            return regreso;
        }

    }

}
