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

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.CatalogBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.catalogs.domain.CacheInvalidationEvent;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogActionTrigger;
import com.wrupple.muba.catalogs.domain.CatalogActionTriggerImpl;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.domain.CatalogIdentificationImpl;
import com.wrupple.muba.catalogs.domain.CatalogPeer;
import com.wrupple.muba.catalogs.domain.DistributiedLocalizedEntry;
import com.wrupple.muba.catalogs.domain.FieldConstraint;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.LocalizedString;
import com.wrupple.muba.catalogs.domain.NamespaceContext;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.domain.WebEventTrigger;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.catalogs.server.annotations.CAPTCHA;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDeleteTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadUrlHandlerTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.EntryDeleteTrigger;
import com.wrupple.muba.catalogs.server.chain.command.FieldDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.chain.command.GarbageCollection;
import com.wrupple.muba.catalogs.server.chain.command.TrashDeleteTrigger;
import com.wrupple.muba.catalogs.server.chain.command.ValidateUserData;
import com.wrupple.muba.catalogs.server.domain.CatalogActionContextImpl;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import com.wrupple.muba.catalogs.server.service.CatalogManager;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.JSRAnnotationsDictionary;
import com.wrupple.muba.catalogs.server.service.RestoreTrash;
import com.wrupple.muba.catalogs.shared.services.PrimaryKeyEncodingService;

@Singleton
public class CatalogManagerImpl extends CatalogBase implements CatalogManager, CatalogPlugin,JSRAnnotationsDictionary {

	protected static final Logger log = LoggerFactory.getLogger(CatalogManagerImpl.class);

	

	private Provider<NamespaceContext> domainContextProvider;

	private final  CatalogResultCache cache;

	private final Command create;

	private final Command read;

	private final Command write;

	private final Command delete;

	private final PrimaryKeyEncodingService keyEncodingService;

	private final String host;
	private final CatalogPlugin[] plugins;
	private final Provider<CatalogDescriptor> fieldProvider;
	private final Provider<CatalogDescriptor> catalogProvider;
	private final Provider<CatalogDescriptor> peerProvider;
	private final Provider<CatalogDescriptor> i18nProvider;
	private final Provider<CatalogDescriptor> triggerProvider;
	private final Provider<CatalogDescriptor> localizedStringProvider;
	private final Provider<CatalogDescriptor> constraintProvider;
	private final Provider<CatalogDescriptor> trashP;

	/*
	 * validation
	 */

	private Map<String, ValidationExpression> map;

	private ArrayList<String> asStringList;



	


	@Inject
	public CatalogManagerImpl(CatalogFactory factory,@Named("host") String host, Provider<NamespaceContext> domainContextProvider,
			PrimaryKeyEncodingService keyEncodingService, CatalogResultCache cache,
			CatalogCreateTransaction create, CatalogReadTransaction read, CatalogUpdateTransaction write,
			CatalogDeleteTransaction delete, GarbageCollection collect, ValidateUserData validate, RestoreTrash restore,
			TrashDeleteTrigger dump, CatalogFileUploadTransaction upload, CatalogFileUploadUrlHandlerTransaction url,
			FieldDescriptorUpdateTrigger invalidateAll, CatalogDescriptorUpdateTrigger invalidate,
			EntryDeleteTrigger trash, @Named(FieldDescriptor.CATALOG_ID) Provider<CatalogDescriptor> fieldProvider,
			@Named(CatalogDescriptor.CATALOG_ID) Provider<CatalogDescriptor> catalogProvider,@Named(CatalogPeer.CATALOG) Provider<CatalogDescriptor> peerProvider,
			@Named(DistributiedLocalizedEntry.CATALOG) Provider<CatalogDescriptor> i18nProvider,
			@Named(CatalogActionTrigger.CATALOG) Provider<CatalogDescriptor> triggerProvider,
			@Named(LocalizedString.CATALOG) Provider<CatalogDescriptor> localizedStringProvider,
			@Named(FieldConstraint.CATALOG_ID) Provider<CatalogDescriptor> constraintProvider,
			@Named(Trash.CATALOG) Provider<CatalogDescriptor> trashP,@Named("catalog.plugins")Provider<Object> pluginProvider) {
		super();
		factory.addCatalog(CatalogActionRequest.CATALOG_ACTION_PARAMETER, this);
		addCommand(CatalogDescriptorUpdateTrigger.class.getSimpleName(), invalidate);
		addCommand(FieldDescriptorUpdateTrigger.class.getSimpleName(), invalidateAll);
		addCommand(EntryDeleteTrigger.class.getSimpleName(), trash);
		addCommand(TrashDeleteTrigger.class.getSimpleName(), dump);
		addCommand(RestoreTrash.class.getSimpleName(), restore);
		addCommand(GarbageCollection.class.getSimpleName(), collect);
		addCommand(CatalogActionRequest.CREATE_ACTION, create);
		addCommand(CatalogActionRequest.READ_ACTION, create);
		addCommand(CatalogActionRequest.WRITE_ACTION, create);
		addCommand(CatalogActionRequest.DELETE_ACTION, create);
		addCommand(CatalogActionRequest.UPLOAD_ACTION, upload);
		addCommand("url", url);
		this.peerProvider=peerProvider;
		this.cache = cache;
		this.domainContextProvider = domainContextProvider;
		this.host = host;
		this.fieldProvider = fieldProvider;
		this.catalogProvider = catalogProvider;
		this.i18nProvider = i18nProvider;
		this.triggerProvider = triggerProvider;
		this.constraintProvider = constraintProvider;
		this.localizedStringProvider = localizedStringProvider;
		this.plugins = (CatalogPlugin[]) pluginProvider.get();
		this.trashP = trashP;
		this.keyEncodingService = keyEncodingService;
		this.create = create;
		this.read = read;
		this.write = write;
		this.delete = delete;

	}

	/*
	 * sets all configuration data, except for permissions which the Data Store
	 * // Manager handles public
	 * SimpleVegetateCatalogDataAceessObject(ObjectMapper mapper,
	 * CatalogEntryAssembler entryAssembler,CatalogDescriptor catalogdescriptor,
	 * String host, String vegetateUrlBase, String targetDomain,
	 * CatalogServiceManifest serviceManifest,Provider<? extends
	 * SignatureGenerator> signatureGeneratorProvider) { super();
	 * this.entryAssembler = entryAssembler; this.domain = targetDomain;
	 * this.channel = new CatalogVegetateChannel(host, vegetateUrlBase, mapper,
	 * serviceManifest,signatureGeneratorProvider);
	 * this.catalogdescriptor=catalogdescriptor; }
	 */

	@Override
	public CatalogActionContext spawn(CatalogActionContext parent) {
		return makeContext(parent.getExcecutionContext(), parent);
	}
	
	@Override
	public Context spawn(ExcecutionContext system) {
		return makeContext(system, null);
	}

	private CatalogActionContext makeContext(ExcecutionContext excecutionContext, CatalogActionContext parent) {
		CatalogActionContext regreso = new CatalogActionContextImpl(this,
				parent == null ? domainContextProvider.get() : parent.getNamespaceContext(), excecutionContext, parent);
		log.debug("[SPAWN ] {}", regreso);
		return regreso;
	}

	@Override
	public Command getRead() {
		return read;
	}

	@Override
	public Command getWrite() {
		return write;
	}

	@Override
	public Command getDelete() {
		return delete;
	}

	@Override
	public Command getNew() {
		return create;
	}

	@Override
	public PrimaryKeyEncodingService getKeyEncodingService() {
		return keyEncodingService;
	}

	@Override
	public void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogActionContext context) {
		names.add(new CatalogIdentificationImpl(CatalogDescriptor.CATALOG_ID, "Catalogs", "/static/img/catalog.png"));
		names.add(new CatalogIdentificationImpl(FieldDescriptor.CATALOG_ID, "Fields", "/static/img/fields.png"));
		names.add(
				new CatalogIdentificationImpl(FieldConstraint.CATALOG_ID, "Validation Data", "/static/img/check.png"));
		names.add(new CatalogIdentificationImpl(CatalogActionTrigger.CATALOG, "Action Triggers",
				"/static/img/excecute.png"));
		names.add(new CatalogIdentificationImpl(WebEventTrigger.CATALOG, "Web Triggers", "/static/img/excecute.png"));
		names.add(new CatalogIdentificationImpl(DistributiedLocalizedEntry.CATALOG, "Localized Entity",
				"/static/img/locale.png"));
		names.add(new CatalogIdentificationImpl(LocalizedString.CATALOG, "i18n", "/static/img/locale.png"));
	}

	@Override
	public CatalogDescriptor getDescriptorForName(String catalogId, CatalogActionContext context) throws Exception {
		CatalogDescriptor regreso = cache.get(context, DOMAIN_METADATA, catalogId);
		if (regreso == null) {
			log.trace("assemble catalog descriptor {} ", catalogId);
			if (FieldDescriptor.CATALOG_ID.equals(catalogId)) {
				regreso = fieldProvider.get();
				CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1,
						FieldDescriptorUpdateTrigger.class.getSimpleName(), false, null, null, null);
				trigger.setFailSilence(true);
				trigger.setStopOnFail(true);
				regreso.getTriggersValues().add(trigger);
				trigger = new CatalogActionTriggerImpl(2, FieldDescriptorUpdateTrigger.class.getSimpleName(), false,
						null, null, null);
				trigger.setFailSilence(true);
				trigger.setStopOnFail(true);
				regreso.getTriggersValues().add(trigger);
				return regreso;
			} else if (CatalogDescriptor.CATALOG_ID.equals(catalogId)) {
				regreso = catalogProvider.get();
				CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1,CatalogDescriptorUpdateTrigger.class.getSimpleName(), false, null, null, null);
				trigger.setFailSilence(true);
				trigger.setStopOnFail(true);
				regreso.getTriggersValues().add(trigger);
				trigger = new CatalogActionTriggerImpl(2, CatalogDescriptorUpdateTrigger.class.getSimpleName(), false,
						null, null, null);
				trigger.setFailSilence(true);
				trigger.setStopOnFail(true);
				regreso.getTriggersValues().add(trigger);
			} else if (CatalogPeer.CATALOG.equals(catalogId)) {
				regreso = peerProvider.get();
			} else if (DistributiedLocalizedEntry.CATALOG.equals(catalogId)) {
				regreso = i18nProvider.get();
			} else if (CatalogActionTrigger.CATALOG.equals(catalogId)) {
				regreso = triggerProvider.get();
			} else if (LocalizedString.CATALOG.equals(catalogId)) {
				regreso = localizedStringProvider.get();
			} else if (FieldConstraint.CATALOG_ID.equals(catalogId)) {
				regreso = constraintProvider.get();
			} else if (Trash.CATALOG.equals(catalogId)) {
				regreso = trashP.get();
				CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1, RestoreTrash.class.getSimpleName(),
						true, null, null, null);
				trigger.setFailSilence(false);
				trigger.setStopOnFail(false);
				regreso.getTriggersValues().add(trigger);
				trigger = new CatalogActionTriggerImpl(2, TrashDeleteTrigger.class.getSimpleName(), false, null, null,
						null);
				trigger.setFailSilence(true);
				trigger.setStopOnFail(true);
				regreso.getTriggersValues().add(trigger);
			} else {
				// POLLING plugins
				for (CatalogPlugin plugin : plugins) {
					log.trace("asking {} for descriptor",plugin);
					regreso = plugin.getDescriptorForName(catalogId, context);
					if (regreso != null) {
						break;
					}
				}
			}

			return processDescriptor(catalogId, regreso, context, cache);

		} else {
			return regreso;
		}
	}

	private CatalogDescriptor processDescriptor(String name, CatalogDescriptor regreso, CatalogActionContext context,CatalogResultCache cache) {
		if (regreso == null) {
			throw new IllegalArgumentException("No catalog plugin recognized catalogid: "+name);
		}
		for (CatalogPlugin interpret2 : plugins) {
			log.trace("POST process {} IN {}", name, interpret2);
			interpret2.postProcessCatalogDescriptor(regreso);
		}
		if (regreso.getHost() == null) {
			log.trace("locally bound catalog {} @ {}", name, host);
			regreso.setHost(host);
		}
		cache.put(context, DOMAIN_METADATA,name, regreso);
		context.getTransactionHistory().didMetadataRead(regreso);
		log.trace("BUILT catalog {}={}", name, regreso);
		return regreso;
	}

	@Override
	public List<CatalogIdentification> getAvailableCatalogs(CatalogActionContext context) throws Exception {
		List<CatalogIdentification> names = new ArrayList<CatalogIdentification>();

		modifyAvailableCatalogList(names, context);

		CatalogPlugin[] modules = getPlugins();
		if (modules != null) {
			for (CatalogPlugin module : modules) {
				module.modifyAvailableCatalogList(names, context);
			}
		}
		return names;
	}

	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor regreso) {

		CatalogActionTriggerImpl trigger;

		trigger = new CatalogActionTriggerImpl(2, GarbageCollection.class.getSimpleName(), false, null, null, null);
		trigger.setFailSilence(true);
		trigger.setStopOnFail(true);

		regreso.getTriggersValues().add(trigger);

		FieldDescriptor field = regreso.getFieldDescriptor(Trash.TRASH_FIELD);
		if (field != null && field.getDataType() == CatalogEntry.BOOLEAN_DATA_TYPE) {

			trigger = new CatalogActionTriggerImpl(1, EntryDeleteTrigger.class.getSimpleName(), false, null, null,
					null);
			trigger.setFailSilence(true);
			trigger.setStopOnFail(true);
			regreso.getTriggersValues().add(trigger);
		}

	}




	public CatalogPlugin[] getPlugins() {
		return plugins;
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
			map.put(NotNull.class.getSimpleName(),
					new ValidationExpression(NotNull.class, FieldConstraint.EVALUATING_VARIABLE,
							FieldConstraint.EVALUATING_VARIABLE + "==null?\"{validator.null}\":null"));
			// fields must declare a "value" property specifiying min or max
			// value
			map.put(Min.class.getSimpleName(),
					new ValidationExpression(Min.class, FieldConstraint.EVALUATING_VARIABLE + ",value",
							FieldConstraint.EVALUATING_VARIABLE + "<value?\"{validator.min}\":null"));
			map.put(Max.class.getSimpleName(),
					new ValidationExpression(Max.class, FieldConstraint.EVALUATING_VARIABLE + ",value",
							FieldConstraint.EVALUATING_VARIABLE + ">value?\"{validator.max}\":null"));
			map.put(CAPTCHA.class.getSimpleName(),
					new ValidationExpression(CAPTCHA.class, FieldConstraint.EVALUATING_VARIABLE,
							FieldConstraint.EVALUATING_VARIABLE + "==null?\"{captcha.message}\":null"));

			CatalogPlugin[] catalogPlugins = getPlugins();

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
	public ValidationExpression[] getValidations() {// TODO catalog field
													// consistency
													// and inheritance loop
													// detection
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public com.wrupple.muba.catalogs.domain.annotations.CatalogKey buildCatalogKeyValidation(FieldDescriptor field) {
		Map properties = Collections.singletonMap("foreignCatalog", field.getCatalog());

		com.wrupple.muba.catalogs.domain.annotations.CatalogKey annotation = Defaults
				.of(com.wrupple.muba.catalogs.domain.annotations.CatalogKey.class, properties);
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
		public static <A extends Annotation> A of(Class<A> annotation, FieldConstraint constraint) {
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
					value = property.substring(split + 1, property.length()-1);
					regreso.put(element, value);
				}
			}
			return regreso;
		}

	}

	@Override
	public CatalogResultCache getCache() {
		return cache;
	}

	
	public  void addBroadcastable(CacheInvalidationEvent data, CatalogActionContext ctx) {
		if (data != null) {
			if (data.getEntry() != null) {
				ctx = ctx.getRootAncestor();
				List<CacheInvalidationEvent> list = (List<CacheInvalidationEvent>) ctx.get(CacheInvalidationEvent.class.getSimpleName());
				if (list == null) {
					list = new ArrayList<CacheInvalidationEvent>(2);
					ctx.put(CacheInvalidationEvent.class.getName(), list);
				}
				log.trace("[stored catalog broadcast event to dispatch later...]");
				list.add(data);
			}
		}
	}

	
	
	public  CatalogResultCache getCache(CatalogDescriptor catalog, CatalogActionContext context){
		CatalogResultCache cache =catalog.getOptimization() != 2 /* NO_CACHE */ ? context.getCatalogManager().getCache() : null;
		
		if (cache == null) {
			log.debug("[CATALOG DAO] NO CACHE");
		}else{
		}
		return cache;
	}

}
