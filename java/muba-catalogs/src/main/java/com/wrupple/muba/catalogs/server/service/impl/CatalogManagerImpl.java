package com.wrupple.muba.catalogs.server.service.impl;

import com.google.inject.Inject;
import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bootstrap.domain.reserved.*;
import com.wrupple.muba.bootstrap.server.service.FormatDictionary;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.catalogs.server.annotations.CAPTCHA;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.domain.CatalogActionContextImpl;
import com.wrupple.muba.catalogs.server.domain.FilterDataOrderingImpl;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import com.wrupple.muba.catalogs.server.domain.fields.VersionFields;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy.Session;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.CatalogBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
public class CatalogManagerImpl extends CatalogBase implements SystemCatalogPlugin {

	protected static final Logger log = LoggerFactory.getLogger(CatalogManagerImpl.class);

	private final String TOKEN_SPLITTER;

	private final String ancestorIdField;
	private final Pattern pattern;

    ///////

	private Provider<NamespaceContext> domainContextProvider;

	private final CatalogResultCache cache;

	private final Command create;

	private final Command read;

	private final Command write;

	private final Command delete;

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
	private final Provider<CatalogDescriptor> revisionP;
	private final CatalogDescriptorBuilder builder;
	/*
     *Evaluation
	 */
    private final FieldAccessStrategy access;
    /*
     * Indexing
     */
    private final CatalogActionTriggerImpl treeIndex;
	private final CatalogActionTrigger timelineDiscriminator;
	/*
	 * versioning
	 */
	private final CatalogActionTriggerImpl versionTrigger;
	/*
	 * validation
	 */

	private Map<String, ValidationExpression> map;

	private ArrayList<String> asStringList;

	private ArrayList<String> defaultVersioningTriggerproperties;

	private CatalogActionTriggerImpl timestamp;

	private WritePublicTimelineEventDiscriminator inheritanceHandler;

    private final FormatDictionary formats;


	@Inject
	public CatalogManagerImpl(@Named("template.token.splitter") String splitter /* "\\." */,
                              @Named("template.pattern") Pattern pattern/** "\\$\\{([A-Za-z0-9]+\\.){0,}[A-Za-z0-9]+\\}" */
            , @Named("catalog.ancestorKeyField") String ancestorIdField, CatalogFactory factory, CatalogDescriptorBuilder builder, @Named("host") String host, Provider<NamespaceContext> domainContextProvider, CatalogResultCache cache, CatalogCreateTransaction create, CatalogReadTransaction read, CatalogUpdateTransaction write, CatalogDeleteTransaction delete, GarbageCollection collect, RestoreTrash restore, TrashDeleteTrigger dump, CatalogFileUploadTransaction upload, CatalogFileUploadUrlHandlerTransaction url, FieldDescriptorUpdateTrigger invalidateAll, CatalogDescriptorUpdateTrigger invalidate, EntryDeleteTrigger trash, UpdateTreeLevelIndex treeIndexHandler, Timestamper timestamper, WritePublicTimelineEventDiscriminator inheritanceHandler, IncreaseVersionNumber increaseVersionNumber, @Named(FieldDescriptor.CATALOG_ID) Provider<CatalogDescriptor> fieldProvider, @Named(CatalogDescriptor.CATALOG_ID) Provider<CatalogDescriptor> catalogProvider, @Named(CatalogPeer.CATALOG) Provider<CatalogDescriptor> peerProvider, @Named(DistributiedLocalizedEntry.CATALOG) Provider<CatalogDescriptor> i18nProvider, @Named(CatalogActionTrigger.CATALOG) Provider<CatalogDescriptor> triggerProvider, @Named(LocalizedString.CATALOG) Provider<CatalogDescriptor> localizedStringProvider, @Named(Constraint.CATALOG_ID) Provider<CatalogDescriptor> constraintProvider, @Named(Trash.CATALOG) Provider<CatalogDescriptor> trashP, @Named(ContentRevision.CATALOG) Provider<CatalogDescriptor> revisionP, @Named("catalog.plugins") Provider<Object> pluginProvider, FieldAccessStrategy access,FormatDictionary formats) {
        super();
        this.formats=formats;
		this.ancestorIdField = ancestorIdField;
		this.TOKEN_SPLITTER = splitter;
		this.pattern = pattern;
		this.builder = builder;
        this.access = access;
        versionTrigger = new CatalogActionTriggerImpl(1, IncreaseVersionNumber.class.getSimpleName(), true, null, null,
                null);
		versionTrigger.setFailSilence(true);
		versionTrigger.setStopOnFail(true);

		treeIndex = new CatalogActionTriggerImpl(0, UpdateTreeLevelIndex.class.getSimpleName(), true, null, null, null);
		treeIndex.setFailSilence(true);
		treeIndex.setStopOnFail(true);
		timelineDiscriminator = new CatalogActionTriggerImpl(0,
				WritePublicTimelineEventDiscriminator.class.getSimpleName(), false, null, null, null);
		timelineDiscriminator.setFailSilence(true);
		timelineDiscriminator.setStopOnFail(true);

		timestamp = new CatalogActionTriggerImpl(0, Timestamper.class.getSimpleName(), true, null, null, null);
		timestamp.setFailSilence(false);
		timestamp.setStopOnFail(true);

		this.defaultVersioningTriggerproperties = new ArrayList<String>(5);
		String putCatalogId = HasCatalogId.CATALOG_FIELD + "=" + CatalogActionRequest.CATALOG_FIELD;
		String putEntryId = HasEntryId.ENTRY_ID_FIELD + "=" + SystemCatalogPlugin.SOURCE_OLD + ".id";
		defaultVersioningTriggerproperties
				.add(Versioned.FIELD + "=" + SystemCatalogPlugin.SOURCE_OLD + "." + Versioned.FIELD);
		defaultVersioningTriggerproperties.add(putEntryId);
		defaultVersioningTriggerproperties.add(putCatalogId);
		defaultVersioningTriggerproperties
				.add("value=" + SystemCatalogPlugin.SOURCE_OLD + "." + CatalogActionTrigger.SERIALIZED);

		factory.addCatalog(CatalogActionRequest.NAME_FIELD, this);
		addCommand(CatalogDescriptorUpdateTrigger.class.getSimpleName(), invalidate);
		addCommand(FieldDescriptorUpdateTrigger.class.getSimpleName(), invalidateAll);
		addCommand(EntryDeleteTrigger.class.getSimpleName(), trash);
		addCommand(TrashDeleteTrigger.class.getSimpleName(), dump);
		addCommand(RestoreTrash.class.getSimpleName(), restore);
		addCommand(GarbageCollection.class.getSimpleName(), collect);
		addCommand(CatalogActionRequest.CREATE_ACTION, create);
		addCommand(CatalogActionRequest.READ_ACTION, read);
		addCommand(CatalogActionRequest.WRITE_ACTION, write);
		addCommand(CatalogActionRequest.DELETE_ACTION, delete);
		addCommand(CatalogActionRequest.UPLOAD_ACTION, upload);
		addCommand(IncreaseVersionNumber.class.getSimpleName(), increaseVersionNumber);
		addCommand(UpdateTreeLevelIndex.class.getSimpleName(), treeIndexHandler);
		this.inheritanceHandler = inheritanceHandler;
		addCommand(WritePublicTimelineEventDiscriminator.class.getSimpleName(), inheritanceHandler);
		addCommand(Timestamper.class.getSimpleName(), timestamper);
		addCommand("url", url);
		this.peerProvider = peerProvider;
		this.cache = cache;
		this.domainContextProvider = domainContextProvider;
		this.host = host;
		this.fieldProvider = fieldProvider;
		this.revisionP = revisionP;
		this.catalogProvider = catalogProvider;
		this.i18nProvider = i18nProvider;
		this.triggerProvider = triggerProvider;
		this.constraintProvider = constraintProvider;
		this.localizedStringProvider = localizedStringProvider;
		this.plugins = (CatalogPlugin[]) pluginProvider.get();
		this.trashP = trashP;
		this.create = create;
		this.read = read;
		this.write = write;
		this.delete = delete;

		Command[] actions;
		for(CatalogPlugin plugin: plugins){
			actions = plugin.getActions();
			if(actions!=null){
				for(Command action:actions){
					addCommand(action.getClass().getSimpleName(),action);
				}
			}

		}

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
		return makeContext(parent.getRuntimeContext(), parent);
	}

	@Override
	public CatalogActionContext spawn(RuntimeContext system) {
		return makeContext(system, null);
	}

	private CatalogActionContext makeContext(RuntimeContext runtimeContext, CatalogActionContext parent) {
		CatalogActionContext regreso = new CatalogActionContextImpl(this,
				parent == null ? domainContextProvider.get() : parent.getNamespaceContext(), runtimeContext, parent);
		log.debug("[SPAWN ] {}", regreso);
		regreso.put(WriteAuditTrails.CONTEXT_START, System.currentTimeMillis());
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
	public void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogActionContext context) {
		names.add(new CatalogIdentificationImpl(CatalogDescriptor.CATALOG_ID, "Catalogs", "/static/img/catalog.png"));
		names.add(new CatalogIdentificationImpl(FieldDescriptor.CATALOG_ID, "Fields", "/static/img/fields.png"));
		names.add(new CatalogIdentificationImpl(Constraint.CATALOG_ID, "Validation Data", "/static/img/check.png"));
		names.add(new CatalogIdentificationImpl(CatalogActionTrigger.CATALOG, "Action Triggers",
				"/static/img/excecute.png"));
		names.add(new CatalogIdentificationImpl(WebEventTrigger.CATALOG, "Web Triggers", "/static/img/excecute.png"));
		names.add(new CatalogIdentificationImpl(DistributiedLocalizedEntry.CATALOG, "Localized Entity",
				"/static/img/locale.png"));
		names.add(new CatalogIdentificationImpl(LocalizedString.CATALOG, "i18n", "/static/img/locale.png"));
		names.add(new CatalogIdentificationImpl(ContentRevision.CATALOG, "Revision", "/static/img/revision.png"));
		if (context.getNamespaceContext().isRecycleBinEnabled()) {
			names.add(new CatalogIdentificationImpl(Trash.CATALOG, "Trash", "/static/img/trash.png"));
		}
	}

	@Override
	public CatalogDescriptor getDescriptorForKey(Long key, CatalogActionContext context) throws RuntimeException {

		long value = key.longValue();
		log.trace("assemble catalog descriptor FOR KEY {} ", value);
		CatalogDescriptor regreso = null;
		if (value == -1) {
			regreso = builder.fromClass(ContentNodeImpl.class, ContentNode.CATALOG, ContentNode.class.getSimpleName(),
					-1l, null);
		}

		if (regreso == null) {
			// POLLING plugins
			for (CatalogPlugin plugin : plugins) {
				log.trace("asking {} for descriptor", plugin);
				regreso = plugin.getDescriptorForKey(key, context);
				if (regreso != null) {
					break;
				}
			}
		}

		return processDescriptor(regreso.getDistinguishedName(), regreso, context, cache);
	}

	@Override
	public CatalogDescriptor getDescriptorForName(String catalogId, CatalogActionContext context)
			throws RuntimeException {
		CatalogDescriptor regreso = cache.get(context, DOMAIN_METADATA, catalogId);
		if (regreso == null) {
			log.trace("assemble catalog descriptor {} ", catalogId);
			if (FieldDescriptor.CATALOG_ID.equals(catalogId)) {
				regreso = fieldProvider.get();
				CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1,
						FieldDescriptorUpdateTrigger.class.getSimpleName(), false, null, null, null);
				trigger.setFailSilence(true);
				trigger.setStopOnFail(true);
				regreso.addTrigger(trigger);
				trigger = new CatalogActionTriggerImpl(2, FieldDescriptorUpdateTrigger.class.getSimpleName(), false,
						null, null, null);
				trigger.setFailSilence(true);
				trigger.setStopOnFail(true);
				regreso.addTrigger(trigger);
				return regreso;
			} else if (CatalogDescriptor.CATALOG_ID.equals(catalogId)) {
				regreso = catalogProvider.get();
				CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1,
						CatalogDescriptorUpdateTrigger.class.getSimpleName(), false, null, null, null);
				trigger.setFailSilence(true);
				trigger.setStopOnFail(true);
				regreso.addTrigger(trigger);
				trigger = new CatalogActionTriggerImpl(2, CatalogDescriptorUpdateTrigger.class.getSimpleName(), false,
						null, null, null);
				trigger.setFailSilence(true);
				trigger.setStopOnFail(true);
				regreso.addTrigger(trigger);
			} else if (CatalogPeer.CATALOG.equals(catalogId)) {
				regreso = peerProvider.get();
			} else if (DistributiedLocalizedEntry.CATALOG.equals(catalogId)) {
				regreso = i18nProvider.get();
			} else if (CatalogActionTrigger.CATALOG.equals(catalogId)) {
				regreso = triggerProvider.get();
			} else if (LocalizedString.CATALOG.equals(catalogId)) {
				regreso = localizedStringProvider.get();
			} else if (Constraint.CATALOG_ID.equals(catalogId)) {
				regreso = constraintProvider.get();
			} else if (Trash.CATALOG.equals(catalogId)) {
				regreso = trashP.get();
				CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1, RestoreTrash.class.getSimpleName(),
						true, null, null, null);
				trigger.setFailSilence(false);
				trigger.setStopOnFail(false);
				regreso.addTrigger(trigger);
				trigger = new CatalogActionTriggerImpl(2, TrashDeleteTrigger.class.getSimpleName(), false, null, null,
						null);
				trigger.setFailSilence(true);
				trigger.setStopOnFail(true);
				regreso.addTrigger(trigger);
			} else if (ContentNode.NUMERIC_ID.equals(catalogId)) {
				regreso = builder.fromClass(ContentNodeImpl.class, ContentNode.class.getSimpleName(),
						CatalogEntry.class.getSimpleName(), -1l, null);
			} else if (ContentRevision.CATALOG.equals(catalogId)) {
				return revisionP.get();
			} else {
				// POLLING plugins
				for (CatalogPlugin plugin : plugins) {
					log.trace("asking {} for descriptor", plugin);
					regreso = plugin.getDescriptorForName(catalogId, context);
					if (regreso != null) {
						break;
					}
				}
			}

			if (regreso == null) {
				throw new IllegalArgumentException("No catalog plugin recognized catalogid: " + catalogId);
			}

			return processDescriptor(catalogId, regreso, context, cache);

		} else {
			return regreso;
		}
	}

	private CatalogDescriptor processDescriptor(String name, CatalogDescriptor catalog, CatalogActionContext context,
			CatalogResultCache cache) throws RuntimeException {
		if(log.isTraceEnabled()){
			log.trace("process descriptor with DN :"+name);
		}
		boolean versioned = catalog.getFieldDescriptor(Versioned.FIELD) != null;

		if (versioned || catalog.isVersioned()) {
			if (!versioned) {
				// MUST HAVE VERSION FIELD
				catalog.putField(new VersionFields());
			}
			catalog.addTrigger(getVersioningTrigger());
		}

		if (catalog.isRevised()) {
			catalog.addTrigger(getRevisionTrigger(catalog));
		}
		if (catalog.getParent() != null) {
			if (catalog.getGreatAncestor() == null) {
				// find great ancestor
				CatalogDescriptor parent = getDescriptorForKey(catalog.getParent(), context);

				while (parent != null) {
					catalog.setGreatAncestor(parent.getDistinguishedName());
					parent = parent.getParent() == null ? null : getDescriptorForKey(parent.getParent(), context);
				}
			}
			if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()
					&& ContentNode.CATALOG.equals(catalog.getGreatAncestor())) {

				catalog.addTrigger(timestamp);
				List<FilterDataOrdering> sorts = catalog.getAppliedSorts();
				FilterDataOrderingImpl index;
				if (sorts == null) {
					sorts = new ArrayList<FilterDataOrdering>(5);
					catalog.setAppliedSorts(sorts);
				}
				if (catalog.getFieldDescriptor(IsPinnable.FIELD) != null) {
					index = new FilterDataOrderingImpl(IsPinnable.FIELD, false);
					sorts.add(index);
				}
				if (catalog.getFieldDescriptor(HasTimestamp.FIELD) != null) {
					index = new FilterDataOrderingImpl(HasTimestamp.FIELD, false);
					sorts.add(index);
				}

				FieldDescriptor field = catalog.getFieldDescriptor(HasChildren.FIELD);

				if (catalog.getFieldDescriptor(inheritanceHandler.getDiscriminatorField()) != null
						&& catalog.getFieldDescriptor(inheritanceHandler.getDiscriminatorField()) != null) {
					log.debug("catalog is public timeline");
					catalog.addTrigger(afterCreateHandledTimeline());
				}

				if (catalog.getFieldDescriptor(ContentNode.CHILDREN_TREE_LEVEL_INDEX) != null && field != null
						&& catalog.getDistinguishedName().equals(field.getCatalog())) {
					index = new FilterDataOrderingImpl(ContentNode.CHILDREN_TREE_LEVEL_INDEX, true);
					sorts.add(index);
					// INDEXED TREE
					catalog.addTrigger(beforeIndexedTreeCreate());

				}
			}
		}

		for (CatalogPlugin interpret2 : plugins) {
			log.trace("POST process {} IN {}", name, interpret2);
			interpret2.postProcessCatalogDescriptor(catalog);
		}
		if (catalog.getHost() == null) {
			log.trace("locally bound catalog {} @ {}", name, host);
			catalog.setHost(host);
		}
		cache.put(context, DOMAIN_METADATA, name, catalog);
		context.getTransactionHistory().didMetadataRead(catalog);
		log.trace("BUILT catalog {}={}", name, catalog);
		return catalog;
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

		regreso.addTrigger(trigger);

		FieldDescriptor field = regreso.getFieldDescriptor(Trash.TRASH_FIELD);
		if (field != null && field.getDataType() == CatalogEntry.BOOLEAN_DATA_TYPE) {

			trigger = new CatalogActionTriggerImpl(1, EntryDeleteTrigger.class.getSimpleName(), false, null, null,
					null);
			trigger.setFailSilence(true);
			trigger.setStopOnFail(true);
			regreso.addTrigger(trigger);
		}

	}

	public CatalogPlugin[] getPlugins() {
		return plugins;
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
        if (registry == null || registry.getGivenVariable()==null) {
            return null;
        } else {
            String givenValue = registry.getGivenVariable();
            ConstraintImpl constraint = new ConstraintImpl();
            constraint.setDistinguishedName(name);
            constraint.setProperties(Arrays.asList(givenValue+"="+getGivenValue(annotation,givenValue)));
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
							Constraint.EVALUATING_VARIABLE + "==null?\"{validator.null}\":null",null));
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
							Constraint.EVALUATING_VARIABLE + "==null?\"{captcha.message}\":null",null));

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
    public Command[] getActions() {
	    //get all registered actions?
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

	@Override
	public CatalogResultCache getCache() {
		return cache;
	}

	public CatalogResultCache getCache(CatalogDescriptor catalog, CatalogActionContext context) {
		CatalogResultCache cache = catalog.getOptimization() != 2/* NO_CACHE */ ? context.getCatalogManager().getCache()
				: null;

		if (cache == null) {
			log.debug("[NUMERIC_ID DAO] NO CACHE");
		} else {
		}
		return cache;
	}



	@Override
	public void createRefereces(CatalogActionContext context, CatalogDescriptor catalog, FieldDescriptor field,
								  Object foreignValue,CatalogEntry owner, Session session) throws Exception {
        String reservedField;
		context.setCatalog(field.getCatalog());
		if (field.isMultiple()) {
			Collection<CatalogEntry> entries = (Collection<CatalogEntry>) foreignValue;
			List<CatalogEntry> createdValues = new ArrayList<CatalogEntry>(entries.size());
			for (CatalogEntry entry : entries) {
				if (entry.getId() == null) {
					createdValues.add(createEntry(context, entry, field.getCatalog()));
				} else {
					createdValues.add(entry);
				}
			}

            reservedField = field.getFieldId() + CatalogEntry.MULTIPLE_FOREIGN_KEY;
            if (access.isWriteableProperty(reservedField, owner, session)) {
                access.setPropertyValue(reservedField, owner, createdValues, session);
            }
            List<Object> keys = createdValues.stream().map(v -> v.getId()).collect(Collectors.toList());

            access.setPropertyValue(field, owner, keys, session);
        } else {
			CatalogEntry value =  createEntry(context, foreignValue, field.getCatalog());
            reservedField = field.getFieldId() + CatalogEntry.FOREIGN_KEY;
            if (access.isWriteableProperty(reservedField, owner, session)) {
                access.setPropertyValue(reservedField, owner, value, session);
            }
            access.setPropertyValue(field, owner, value.getId(), session);
        }
	}

	private CatalogEntry createEntry(CatalogActionContext context, Object foreignValue, String catalog) throws Exception {
		// FIXME this is not batch at all, must be able to load a context with
		// multile input entries, just like there is multiple results by default
		context.setEntryValue(foreignValue);
		context.getCatalogManager().getNew().execute(context);
		return context.getEntryResult();
	}

	private CatalogActionTrigger afterCreateHandledTimeline() {

		return timelineDiscriminator;
	}

	private CatalogActionTrigger beforeIndexedTreeCreate() {
		return treeIndex;
	}

	private CatalogActionTrigger getVersioningTrigger() {
		return versionTrigger;
	}

	private CatalogActionTrigger getRevisionTrigger(CatalogDescriptor c) {
		ArrayList<String> properties = new ArrayList<String>(5);

		properties.addAll(this.defaultVersioningTriggerproperties);

		properties.add("name=" + SystemCatalogPlugin.SOURCE_OLD + "." + c.getDescriptiveField());

		CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1, CatalogActionRequest.CREATE_ACTION, true,
				ContentRevision.CATALOG, properties, null);
		trigger.setFailSilence(true);
		trigger.setStopOnFail(true);
		return trigger;
	}

	/*
	 * KEY SERVICES
	 */

	@Override
	public Object encodeClientPrimaryKeyFieldValue(Object rawValue, FieldDescriptor field, CatalogDescriptor catalog) {
		if (rawValue == null) {
			return null;
		}
		if (field != null && field.isMultiple()) {
			List<Object> rawCollection = (List<Object>) rawValue;
			List<String> encodedValues = new ArrayList<String>(rawCollection.size());
			boolean wasTested = false;
			boolean expectLongValues = false;
			for (Object rawCollectionValue : rawCollection) {
				if (!wasTested) {
					wasTested = true;
					expectLongValues = rawCollectionValue instanceof Long;
				}
				if (expectLongValues) {
					encodedValues.add(encodeLongKey((Long) rawCollectionValue));
				} else {
					encodedValues.add(String.valueOf(rawCollectionValue));
				}
			}
			return encodedValues;
		} else {
			if (field == null || field.isKey() || field.getDataType() == CatalogEntry.INTEGER_DATA_TYPE) {
				try {
					return encodeLongKey((Long) rawValue);
				} catch (Exception e) {
					System.err.println("Unable to encode numeric key most likely already a String");
					return String.valueOf(rawValue);
				}

			} else {
				return rawValue;
			}
		}
	}

	@Override
	public List<Object> decodePrimaryKeyFilters(List<Object> values) {
		if (values != null && !values.isEmpty()) {
			try {
				List<Object> ids = new ArrayList<Object>(values.size());
				String raw;
				for (int i = 0; i < values.size(); i++) {
					raw = (String) values.get(i);
					ids.add(decodeKey(raw));
				}
				return ids;
			} catch (NumberFormatException e) {
				return values;
			} catch (ClassCastException e) {
				return values;
			}
		}
		return values;
	}

	@Override
	public boolean qualifiesForEncoding(FieldDescriptor field, CatalogDescriptor catalog) {
		return field.isKey();
	}

	@Override
	public Long decodePrimaryKeyToken(Object targetEntryId) {
		if (targetEntryId == null) {
			return null;
		}

		if (targetEntryId instanceof Long) {
			return (Long) targetEntryId;
		} else {
			return decodeKey((String) targetEntryId);
		}
	}

	private String encodeLongKey(Long key) {
		return Long.toString(key, 36);
	}

	private Long decodeKey(String key) {
		return Long.parseLong(key, 36);
	}

	@Override
	public boolean isPrimaryKey(String vanityId) {
		/*
		 * if (StringUtils.isEmpty(str)) { return false; } for (int i = 0; i <
		 * str.length(); i++) { if (!Character.isDigit(str.charAt(i))) { return
		 * false; } }
		 */
		// lets try, shall we?
		return true;
	}

	public String[][] getJoins(SystemCatalogPlugin serverSide, Object clientSide, CatalogDescriptor descriptor,
			String[][] customJoins, Object domain, String host) throws Exception {
		// TODO configure how many levels/orders deep to go into for
		// joinable fields

		Collection<FieldDescriptor> thisCatalogFields = descriptor.getFieldsValues();
		List<FieldDescriptor> thisCatalogJoinableFields = new ArrayList<FieldDescriptor>();
		String foreignCatalogId;
		String foreignField;
		String localField;

		// GATHER JOINABLE FIELDS

		for (FieldDescriptor field : thisCatalogFields) {
			if (isJoinableValueField(field)) {
				thisCatalogJoinableFields.add(field);
			}
		}
		int size = thisCatalogJoinableFields.size();
		int customJoinsSize = customJoins == null ? 0 : customJoins.length;
		// Generate join sentence
		String[][] allJoinSentences = new String[size + customJoinsSize][];
		FieldDescriptor currentJoinableField;
		int i;
		CatalogDescriptor foreign;
		for (i = 0; i < size; i++) {
			currentJoinableField = thisCatalogJoinableFields.get(i);
			foreignCatalogId = currentJoinableField.getCatalog();
			localField = null;
			foreignField = null;
			if (serverSide == null) {
				throw new RuntimeException("not implemented");
				// foreign =clientSide.loadFromCache(host, (String)domain,
				// foreignCatalogId);
			} else {
				foreign = serverSide.getDescriptorForName(foreignCatalogId, (CatalogActionContext) domain);
			}

			if (currentJoinableField.isKey()) {
				localField = currentJoinableField.getFieldId();
				foreignField = getCatalogKeyFieldId(foreign);
			} else if (currentJoinableField.isEphemeral()) {
				localField = descriptor.getKeyField();
				foreignField = getIncomingForeignJoinableFieldId(foreign, descriptor.getDistinguishedName());
			}
			if (localField == null) {
				localField = CatalogKey.ID_FIELD;
			}
			allJoinSentences[i] = new String[] { foreignCatalogId, foreignField, localField };
		}
		if (customJoins != null) {
			for (String[] customJoinStatement : customJoins) {
				allJoinSentences[i] = customJoinStatement;
				i++;
			}
		}
		return allJoinSentences;
	}

	public String getIncomingForeignJoinableFieldId(CatalogDescriptor foreignDescriptor, String catalog) {

		Collection<FieldDescriptor> fields = foreignDescriptor.getFieldsValues();
		String fieldsForeignCatalog;
		for (FieldDescriptor field : fields) {
			fieldsForeignCatalog = field.getCatalog();
			if (catalog.equals(fieldsForeignCatalog)) {
				return field.getFieldId();
			}
		}
		throw new IllegalArgumentException(
				"No fields in " + foreignDescriptor.getDistinguishedName() + " point to " + catalog);

	}

	private String getCatalogKeyFieldId(CatalogDescriptor descriptor) {
		if (descriptor == null) {
			return CatalogEntry.ID_FIELD;
		} else {
			String keyField = descriptor.getKeyField();
			if (keyField == null) {
				return CatalogEntry.ID_FIELD;
			} else {
				return keyField;
			}
		}
	}

    @Override
    public FieldAccessStrategy access() {
        return this.access;
    }

    @Override
    public boolean isJoinableValueField(FieldDescriptor field) {
	    //data that lives on another layer
        return (field.getCatalog() != null && (field.isEphemeral() || field.isKey() && !formats.isFile(field.getCatalog())));
	}




	/*
	 * INHERITANCE
	 */

	@Override
	public CatalogEntry synthesizeCatalogObject(CatalogEntry source, CatalogDescriptor catalog,
			boolean excludeInherited, Session session, CatalogActionContext context) throws Exception {
		context.getNamespaceContext().setNamespace(context);
        CatalogEntry target = access.synthesize(catalog);

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

                    value = access.getPropertyValue(field, source, localizedObject, session);
                    if (value != null) {
                        access.setPropertyValue(field, target, value, session);
                    }
				}
			}
		}
	}

	@Override
    public Object getAllegedParentId(CatalogEntry result, Session session) throws ReflectiveOperationException {
        //return objectNativeInterface.valuedoReadProperty(this.ancestorIdField, session, result, false);
        return access.getPropertyValue(ancestorIdField, result, null, session);
    }

	@Override
	public void addInheritedValuesToChild(CatalogEntry parentEntity, CatalogEntry regreso, Session session,
			CatalogDescriptor childCatalog) throws Exception {
		Collection<FieldDescriptor> fields = childCatalog.getFieldsValues();
		for (FieldDescriptor field : fields) {
			if (field.isInherited()) {
                access.setPropertyValue(field, regreso, access.getPropertyValue(
                        /*
									 * actually belongs to a totally different
									 * catalog, bus this implementation diesnt
									 * really care so no need to FIXME ?
									 */ field, regreso,
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
		readParentEntry.setName(CatalogActionRequest.READ_ACTION);
		readParentEntry.setEntryValue(null);
		readParentEntry.setFilter(null);

		readParentEntry.getCatalogManager().getRead().execute(readParentEntry);

		return readParentEntry.getEntryResult();
	}

	@Override
	public CatalogEntry synthesizeChildEntity(Object parentEntityId, CatalogEntry o, Session session,
			CatalogDescriptor catalog, CatalogActionContext context) throws Exception {
		CatalogEntry childEntity = synthesizeCatalogObject(o, catalog, true, session, context);
        access.setPropertyValue(this.ancestorIdField, childEntity, parentEntityId, session);
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
                                             Session session) throws ReflectiveOperationException {
        String foreignKeyValuePropertyName = field.getFieldId()
                + (field.isMultiple() ? CatalogEntry.MULTIPLE_FOREIGN_KEY : CatalogEntry.FOREIGN_KEY);
		//check if property exists
        if(access.isReadableProperty(foreignKeyValuePropertyName,e,session)){
           /*return valuedoReadProperty(
				field.getFieldId()
						+ (field.isMultiple() ? CatalogEntry.MULTIPLE_FOREIGN_KEY : CatalogEntry.FOREIGN_KEY),
				(FieldAccessSession) session, e, true);*/
            return access.getPropertyValue(foreignKeyValuePropertyName, e, null, session);
        }else{
            return null;

        }
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

		return getDenormalizedFieldValue(field, session, client, type);
	}

	@Override
	public String getDenormalizedFieldValue(FieldDescriptor field, Session session, CatalogEntry client,
                                            CatalogDescriptor type) throws ReflectiveOperationException {
        if (field.getDefaultValueOptions() != null && !field.getDefaultValueOptions().isEmpty()
				&& field.getDataType() == CatalogEntry.INTEGER_DATA_TYPE) {
            Integer index = (Integer) access.getPropertyValue(field, client, null, session);
            if (index != null) {
				return field.getDefaultValueOptions().get(index.intValue());
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
						out.print(synthethizeFieldValue(rawToken.split(" "), context));
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
    public Object synthethizeFieldValue(String[] split, CatalogActionContext context) throws Exception {
        RuntimeContext ex = context.getRuntimeContext().spawnChild();
        ex.setSentence(split);
        ex.process();
        return ex.getResult();
    }




}
