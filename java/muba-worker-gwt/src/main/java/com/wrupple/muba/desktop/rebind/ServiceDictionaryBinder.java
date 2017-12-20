package com.wrupple.muba.desktop.rebind;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.uibinder.rebind.MortalLogger;
import com.google.gwt.uibinder.rebind.UiBinderGenerator;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.factory.DictionaryTemplate;
import com.wrupple.muba.desktop.client.factory.help.DictionaryConfigurableFactory;
import com.wrupple.muba.desktop.client.factory.help.UserAssistanceProvider;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.PanelTransformationConfig;
import com.wrupple.muba.desktop.domain.PropertyValueAvisor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogKey;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

public class ServiceDictionaryBinder {

	private static final String TEMPLATE_SUFFIX = ".dictionary.xml";

	private TypeOracle typeOracle;
	private String typeName;
	private GeneratorContext context;
	private TreeLogger logger;
	private MortalLogger mlogger;

	public ServiceDictionaryBinder(TreeLogger logger, GeneratorContext context, String typeName) {
		this.logger = logger;
		this.context = context;
		this.typeName = typeName;
		this.typeOracle = context.getTypeOracle();
		this.mlogger = new MortalLogger(logger);
	}

	public String createServicer() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		try {
			JClassType classType = typeOracle.getType(typeName);
			String templateFile = null;

			try {
				templateFile = deduceTemplateFile(mlogger, classType);
			} catch (UnableToCompleteException e) {
				logger.log(null, "error while trying to deduce the xml Template file", e);
			}

			Document doc = null;

			try {
				doc = parseXmlResource(templateFile);
			} catch (SAXParseException e) {
				e.printStackTrace();
			} catch (UnableToCompleteException e) {
				e.printStackTrace();
			}
			Node servicerNode = doc.getElementsByTagName("servicer").item(0);
			String configurationPropertyName= servicerNode.getAttributes().getNamedItem("propertyName").getFirstChild().getNodeValue();

			String defaultPropertyValue;
			try {
				defaultPropertyValue = servicerNode.getAttributes().getNamedItem("defaultPropertyValue").getFirstChild().getNodeValue();
			} catch (Exception e) {
				defaultPropertyValue = null;
			}

			String genericType = getGenericType(servicerNode);
			String serviceBusGetter = servicerNode.getAttributes().getNamedItem("serviceBus").getFirstChild().getNodeValue();

			String providerType = DictionaryConfigurableFactory.class.getCanonicalName();

			SourceWriter source = getSourceWriter(classType, genericType);
			if (source == null) {
				return classType.getParameterizedQualifiedSourceName() + "Impl";
			} else {
				NodeList services = doc.getElementsByTagName("service");
				NodeList parentNode = doc.getElementsByTagName("parent");
				NodeList parentProperties = null;
				if (parentNode != null && parentNode.getLength() > 0) {
					parentProperties = parentNode.item(0).getChildNodes();
				}
				/*
				 * FIELDS
				 */
				source.indent();
				source.println("HashMap<String, " + providerType + "<" + genericType + ">> map;");

				/*
				 * initialize method (called after the first
				 */
				source.println("public String getPropertyName(){");
				source.indent();
				source.print("return \"");
				source.print(configurationPropertyName);
				source.println("\";");
				source.outdent();
				source.println("}");
				source.println("public void init(){");
				source.indent();

				source.println("map = new HashMap<String, " + providerType + "<" + genericType + ">>(" + services.getLength() + ");");

				Node widgetNode;
				NamedNodeMap att;
				String specificType;
				String creator;
				String constantProvider;
				Node typedConstant;
				for (int i = 0; i < services.getLength(); i++) {
					widgetNode = services.item(i);
					att = widgetNode.getAttributes();
					source.print("map.put(");
					constantProvider = getConstantProviderOrNull(att);
					creator = att.getNamedItem("creator").getFirstChild().getNodeValue();
					try{
						specificType = getSpecificType(serviceBusGetter, creator);
					}catch(IllegalArgumentException e){
						specificType = genericType;
					}
					
					if (constantProvider == null) {
						typedConstant = att.getNamedItem("typedConstant");
						if (typedConstant == null) {
							source.print("\"");
							source.print(att.getNamedItem(CatalogKey.ID_FIELD).getFirstChild().getNodeValue());
							source.print("\"");
						} else {
							source.print(specificType);
							source.print(".");
							source.print(att.getNamedItem(CatalogKey.ID_FIELD).getFirstChild().getNodeValue());
						}
					} else {
						source.print(constantProvider);
						source.print(".");
						source.print(att.getNamedItem(CatalogKey.ID_FIELD).getFirstChild().getNodeValue());
					}
					source.print(",");

					source.println("new " + providerType + "<" + genericType + ">() { ");
					source.indent();
					source.println("public void adviceOnCurrentConfigurationState(JavaScriptObject currentState, JsArray<PropertyValueAvisor> regreso){");
					source.println("PropertyValueAvisor newadvice;");
					if (widgetNode.hasChildNodes()) {
						buildAdvice(widgetNode.getChildNodes(), source, serviceBusGetter);
					}
					if (parentProperties != null) {
						buildAdvice(parentProperties, source, serviceBusGetter);
					}
					source.println("}");

					source.println("@Override");
					source.println("public void configure(" + genericType
                            + " object,JavaScriptObject configuration,ProcessContextServices services, EventRegistry processSwitches,JsTransactionApplicationContext ctx) {");
                    source.print(specificType);
					source.print(" regreso = (");
					source.print(specificType);
					source.println(") object;");

					source.println("String varValue;");
					if (widgetNode.hasChildNodes()) {
						configureRegreso(widgetNode.getChildNodes(), source, specificType, serviceBusGetter);
					}
					if (parentProperties != null) {
						configureRegreso(parentProperties, source, specificType, serviceBusGetter);
					}

					source.println("}");

					source.println("@Override");
					source.println("public void validateValue(String fieldId, Object value,JsArrayString violations) {");
					// TODO validate field id is one of property names?
					source.println("}");
					source.println("@Override");
					source.println("public void setRuntimeParameters(String type, ProcessContextServices ctx) {}");
					// <---
					source.println("public " + specificType + " get() { ");

					source.print(specificType);
					source.print(" regreso= ");
					source.print(serviceBusGetter);
					source.print("().");
					source.print(creator);
					source.print("();");
					source.println();
					source.println("return regreso;");
					source.println("}");

					source.println("public " + genericType
                            + " get(final JavaScriptObject configuration, ProcessContextServices services, EventRegistry processSwitches, JsTransactionApplicationContext ctx) { ");
                    source.indent();
					source.print(specificType);
					source.println(" regreso= get();");

                    source.println("configure(regreso, configuration, services, processSwitches, ctx);");

					source.println("return regreso;");
					source.outdent();
					source.println("}");
					source.outdent();
					source.println("}");

					source.print(");");
				}
				source.outdent();
				source.println();
				source.println("}");

				/*
				 * METHODS
				 */
				logger.log(TreeLogger.TRACE, "created servicer with signature " + genericType);

				source.println("public Set<String> keySet(){");
				source.indent();
				checkInitialization(source);
				source.println("return map.keySet();");
				source.outdent();
				source.println("}");

				source.println("public " + genericType + " get(String id) {");
				source.indent();
				checkInitialization(source);
				source.println(providerType + "<" + genericType + "> provider = map.get(id);");
				source.println("assert provider!=null:\"Null Service Provider \"+id;");
				source.println("return provider.get();");
				source.outdent();
				source.println("}");

				source.println("public String getDefault(){");
				if (defaultPropertyValue == null) {
					source.println("return null;");
				} else {
					source.println("return \"" + defaultPropertyValue + "\";");
				}
				source.println("}");

				/*
				 * CONFIGURATION METHOD
				 */

				// TODO pass a String prefix Argument to read property value?
				source.println("public " + genericType
                        + " getConfigured(JavaScriptObject configuration, ProcessContextServices services, EventRegistry processSwitches, JsTransactionApplicationContext ctx) {");
                source.indent();
				// read property
				source.print("String propValue =  JSOHelper.getAttribute(configuration,\"");
				source.print(configurationPropertyName);
				source.print("\"); ");
				source.println();
				// set default value
				if (defaultPropertyValue != null) {
					source.println("if(propValue==null){");
					source.indent();
					source.println("propValue=getDefault();");
					source.outdent();
					source.println("}");
				}
				checkInitialization(source);

				source.println(providerType + "<" + genericType + "> provider = map.get(propValue);");
				source.println("if(provider==null){");
				source.indent();
                source.println("return map.get(getDefault()).get(configuration,services,processSwitches,ctx);");
                source.outdent();
				source.println("}else{");
				source.indent();
                source.println(genericType + " regreso=provider.get(configuration,services,processSwitches,ctx);");
                source.println("return  regreso;");
				source.outdent();
				source.println("}");
				source.outdent();
				source.println("}");

				source.println("public void adviceOnCurrentConfigurationState(JavaScriptObject currentState, JsArray<PropertyValueAvisor> regreso) {");
				source.indent();
				source.println("String mathingValue = JSOHelper.getAttribute(currentState, \"" + configurationPropertyName + "\");");
				checkInitialization(source);
				source.println("if (mathingValue == null) {");
				source.println("Set<String> set = keySet();");
				source.println("PropertyValueAvisor advice;");
				source.println("for(String value:set){");
				source.println("advice=PropertyValueAvisor.createObject().cast();");
				source.println("advice.setName(\"" + configurationPropertyName + "\");");
				source.println("advice.setValue(value);");
				source.println("regreso.push(advice);");

				source.println("}");
				source.println("} else {");
				source.println("UserAssistanceProvider advisor = map.get(mathingValue);");
				source.println("if(advisor!=null){");
				source.println("advisor.adviceOnCurrentConfigurationState(currentState, regreso);");
				source.println("}");
				source.println("}");
				source.outdent();
				source.println("}");

				source.println("@Override");
				source.print("public void reconfigure(PanelTransformationConfig configuration,");
				source.print(genericType);
				source.println(" regreso,ProcessContextServices contextServices, EventRegistry eventBus,JsTransactionApplicationContext contextParameters) {");
				source.indent();
				source.println("String mathingValue = JSOHelper.getAttribute(configuration, \"" + configurationPropertyName + "\");");
				checkInitialization(source);
				source.print(providerType);
				source.println("<" + genericType + "> provider = map.get(mathingValue);");
				source.println("	if (provider != null) {");
				source.println("		provider.configure(regreso, configuration, contextServices, eventBus, contextParameters);");
				source.println("	}");
				source.println("}");

				source.println("@Override");
				source.println("public void validateValue(String fieldId, Object v,JsArrayString regreso){");
				source.println("String value=(String)v;");
				checkInitialization(source);
				// WE ONLY SUPPORT ONE FIELD (THE ONE DECLARED) ... dont we?
				if (configurationPropertyName != null) {
					// check fieldid is equal?
				}
				// FIXME user DesktopCOnstants Messages
				source.println("if(value==null){regreso.push(\"{dictionary.null}\");}");
				source.println("if(value!=null && !map.containsKey(value)){regreso.push(\"dictionary.unrecognized\");}");
				source.println("}");

				source.println("public void setRuntimeParameters(String type,ProcessContextServices ctx){}");
				source.println();
				source.outdent();
				source.commit(logger);
				return classType.getParameterizedQualifiedSourceName() + "Impl";
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getConstantProviderOrNull(NamedNodeMap att) {
		Node node = att.getNamedItem("constantFieldProvider");
		if (node == null) {
			return null;
		} else {
			return node.getFirstChild().getNodeValue();
		}
	}

	private void buildAdvice(NodeList properties, SourceWriter source, String serviceBusGetter) {
		Node propertyNode;
		NamedNodeMap att;
		String propertyName;
		String dictionary;
		String sourceField;
		boolean suggest;
		for (int j = 0; j < properties.getLength(); j++) {
			propertyNode = properties.item(j);
			if ("property".equals(propertyNode.getNodeName())) {
				att = propertyNode.getAttributes();
				suggest = getSuggest(att);
				if (suggest) {
					propertyName = att.getNamedItem(CatalogEntry.NAME_FIELD).getFirstChild().getNodeValue();
					if (propertyName == null) {
						// do nothing
					} else {
						dictionary = getDictionaryOrNull(att);
						if (dictionary == null) {
							sourceField = getSourceOrNull(att);
							if (sourceField == null) {
								sourceField = propertyName;
							}
							source.println("newadvice=PropertyValueAvisor.createObject().cast();");
							source.println("newadvice.setName(\"" + sourceField + "\");");
							// TODO filter-like expressions for open value
							// validation
							source.println("regreso.push(newadvice);");

						} else {

							// delegated property value
							source.print(serviceBusGetter);
							source.print("().getServiceBus().getServiceDictionary(\"" + dictionary
									+ "\").adviceOnCurrentConfigurationState(currentState,regreso);");
							source.println();
						}
					}
				}
			} else if ("hint".equals(propertyNode.getNodeName())) {
				att = propertyNode.getAttributes();
				propertyName = att.getNamedItem(CatalogEntry.NAME_FIELD).getFirstChild().getNodeValue();
				dictionary = getEnumOrNull(att);
				if (dictionary == null) {

					sourceField = att.getNamedItem("value").getFirstChild().getNodeValue();

					source.println("newadvice=PropertyValueAvisor.createObject().cast();");
					source.println("newadvice.setName(\"" + propertyName + "\");");
					source.println("newadvice.setValue(\"" + sourceField + "\");");
					source.println("regreso.push(newadvice);");

				} else {
					// enum
					source.println("for(" + dictionary + " v : " + dictionary + ".values()){");
					source.println("newadvice=PropertyValueAvisor.createObject().cast();");
					source.println("newadvice.setName(\"" + propertyName + "\");");
					source.println("newadvice.setValue( v.toString() );");
					source.println("regreso.push(newadvice);");
					source.println("}");
				}
			}
		}

	}

	private boolean getSuggest(NamedNodeMap att) {
		Node seggestNode = att.getNamedItem("suggest");
		if (seggestNode != null) {
			String value = seggestNode.getFirstChild().getNodeValue();
			return Boolean.parseBoolean(value);
		}
		return true;
	}

	private String getSourceOrNull(NamedNodeMap att) {
		Node dict = att.getNamedItem("source");
		if (dict != null) {
			return dict.getFirstChild().getNodeValue();
		} else {
			return null;
		}
	}

	private String getDictionaryOrNull(NamedNodeMap att) {
		Node dict = att.getNamedItem("dictionary");
		if (dict != null) {
			return dict.getFirstChild().getNodeValue();
		} else {
			return null;
		}
	}

	private String getEnumOrNull(NamedNodeMap att) {
		Node dict = att.getNamedItem("valueEnum");
		if (dict != null) {
			return dict.getFirstChild().getNodeValue();
		} else {
			return null;
		}
	}

	private String getDefaultOrNull(NamedNodeMap att) {
		Node dict = att.getNamedItem("default");
		if (dict != null) {
			return dict.getFirstChild().getNodeValue();
		} else {
			return null;
		}
	}

	private void configureRegreso(NodeList properties, SourceWriter source, String specificType, String serviceBusGetter) throws NotFoundException {
		Node propertyNode;
		NamedNodeMap att;
		String propertyName;
		String setter;
		String dictionary;
		String sourceField;
		String defaultValue;
		for (int j = 0; j < properties.getLength(); j++) {
			propertyNode = properties.item(j);
			if ("property".equals(propertyNode.getNodeName())) {
				att = propertyNode.getAttributes();
				defaultValue = getDefaultOrNull(att);
				propertyName = att.getNamedItem(CatalogEntry.NAME_FIELD).getFirstChild().getNodeValue();
				if (propertyName == null) {
					// do nothing
				} else {
					setter = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

					dictionary = getDictionaryOrNull(att);
					if (dictionary == null) {
						sourceField = getSourceOrNull(att);
						if (sourceField == null) {
							sourceField = propertyName;
						}
						source.print("varValue=JSOHelper.getAttribute(configuration,\"");
						source.print(sourceField);
						source.println("\");");
						if (defaultValue != null) {
							source.println("varValue=varValue==null? \"" + defaultValue + "\":varValue;");
						}
						source.println("if(varValue!=null){");
						source.print("regreso.");
						source.print(setter);
						source.print("(");
						source.print("varValue");
						passRuntimeParameters(source, specificType, setter);
						source.print(");}");
						source.println();
					} else {

						source.print("regreso.");
						source.print(setter);
						source.print("(");
						castToSetterParameterType(source, specificType, setter);
						source.print(serviceBusGetter);
                        source.print("().getServiceBus().getServiceDictionary(\"" + dictionary + "\").getConfigured(configuration,services,processSwitches,ctx)");
                        passRuntimeParameters(source, specificType, setter);
						source.print(");");
					}
				}
			}
		}

	}

	private void castToSetterParameterType(SourceWriter source, String specificType, String setter) throws NotFoundException {
		JClassType c = typeOracle.getType(specificType);
		JMethod[] methods = c.getMethods();
		String name;
		JType[] parameters;
		JType param;
		for (JMethod method : methods) {
			name = method.getName();
			if (name.equals(setter)) {
				parameters = method.getParameterTypes();
				param = parameters[0];
				source.print("(");
				source.print(param.getParameterizedQualifiedSourceName());
				source.print(")");
				return;
			}
		}
		methods = c.getInheritableMethods();
		for (JMethod method : methods) {
			name = method.getName();
			if (name.equals(setter)) {
				parameters = method.getParameterTypes();
				param = parameters[0];
				source.print("(");
				source.print(param.getParameterizedQualifiedSourceName());
				source.print(")");
				return;
			}
		}
		throw new IllegalArgumentException("unable to find setter type of " + setter + " in " + specificType);

	}

	private void passRuntimeParameters(SourceWriter source, String specificType, String setter) throws NotFoundException {
		JClassType c = typeOracle.getType(specificType);
		JMethod[] methods = c.getMethods();
		String name;
		JType[] parameters;
		JType param;
		for (JMethod method : methods) {
			name = method.getName();
			if (name.equals(setter)) {
				parameters = method.getParameterTypes();
				if (parameters.length > 1) {
					for (int i = 1; i < parameters.length; i++) {
						param = parameters[i];
						if (ProcessContextServices.class.getCanonicalName().equals(param.getParameterizedQualifiedSourceName())) {
							source.print(", services");
						} else if (EventBus.class.getCanonicalName().equals(param.getParameterizedQualifiedSourceName())) {
                            source.print(", processSwitches");
                        } else if (JavaScriptObject.class.getCanonicalName().equals(param.getParameterizedQualifiedSourceName())) {
							source.print(", configuration");
						} else if (JsTransactionApplicationContext.class.getCanonicalName().equals(param.getParameterizedQualifiedSourceName())) {
							source.print(", ctx");
						}
					}
					return;
				}
			}
		}

		methods = c.getInheritableMethods();

		for (JMethod method : methods) {
			name = method.getName();
			if (name.equals(setter)) {
				parameters = method.getParameterTypes();
				if (parameters.length > 1) {
					for (int i = 1; i < parameters.length; i++) {
						param = parameters[i];
						if (ProcessContextServices.class.getCanonicalName().equals(param.getParameterizedQualifiedSourceName())) {
							source.print(", services");
						} else if (EventBus.class.getCanonicalName().equals(param.getParameterizedQualifiedSourceName())) {
                            source.print(", processSwitches");
                        } else if (JavaScriptObject.class.getCanonicalName().equals(param.getParameterizedQualifiedSourceName())) {
							source.print(", configuration");
						} else if (JsTransactionApplicationContext.class.getCanonicalName().equals(param.getParameterizedQualifiedSourceName())) {
							source.print(", ctx");
						}
					}
					return;
				}
			}
		}
	}

	private String getSpecificType(String fullMethodName, String creatorMethodName) throws NotFoundException {
		int lastDot = fullMethodName.lastIndexOf('.');
		String referenceCannonicalClassName = fullMethodName.substring(0, lastDot);
		String referenceMethodName = fullMethodName.substring(lastDot + 1, fullMethodName.length());
		JClassType c = typeOracle.getType(referenceCannonicalClassName);
		JMethod referenceMethod = getMethod(referenceMethodName, c);
		JType moduleType = referenceMethod.getReturnType();
		JMethod targetMethod = getMethod(creatorMethodName, moduleType);
		JType specificType = targetMethod.getReturnType();
		return specificType.getQualifiedSourceName();
	}

	private JMethod getMethod(String referenceMethodName, JType cc) throws NotFoundException {
		JClassType c = typeOracle.getType(cc.getParameterizedQualifiedSourceName());
		JMethod[] methods = c.getMethods();
		String name;
		for (JMethod m : methods) {
			name = m.getName();
			if (name.equals(referenceMethodName)) {
				return m;
			}
		}
		methods = c.getInheritableMethods();
		for (JMethod m : methods) {
			name = m.getName();
			if (name.equals(referenceMethodName)) {
				return m;
			}
		}
		throw new IllegalArgumentException("No method " + referenceMethodName + " in " + cc.getParameterizedQualifiedSourceName());
	}

	private void checkInitialization(SourceWriter source) {
		source.println("if(map == null){");
		source.println("init();");
		source.println("}");

	}

	/**
	 * SourceWriter instantiation. Return null if the resource already exist.
	 * 
	 * @param genericType
	 * 
	 * @return sourceWriter
	 */
	public SourceWriter getSourceWriter(JClassType classType, String genericType) {
		String packageName = classType.getPackage().getName();
		String simpleName = classType.getSimpleSourceName() + "Impl";

		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
		composer.addImport("java.util.HashMap");
		composer.addImport("java.util.Set");
		composer.addImport(UserAssistanceProvider.class.getCanonicalName());
		composer.addImport(GWTUtils.class.getCanonicalName());
		composer.addImport(PropertyValueAvisor.class.getCanonicalName());
		composer.addImport(JsArray.class.getCanonicalName());
		composer.addImport(JavaScriptObject.class.getCanonicalName());
		composer.addImport(EventBus.class.getCanonicalName());
		composer.addImport(PanelTransformationConfig.class.getCanonicalName());
		composer.addImport(JsTransactionApplicationContext.class.getCanonicalName());
		composer.addImport(ProcessContextServices.class.getCanonicalName());
		composer.addImport(JsArrayString.class.getCanonicalName());
		composer.addImplementedInterface(classType.getSimpleSourceName());

		// composer.addImplementedInterface("Servicer<" + genericType + ">");
		PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
		if (printWriter == null) {
			return null;
		} else {
			SourceWriter sw = composer.createSourceWriter(context, printWriter);
			return sw;
		}
	}

	private static String deduceTemplateFile(MortalLogger logger, JClassType interfaceType) throws UnableToCompleteException {
		String templateName = null;
		DictionaryTemplate annotation = interfaceType.getAnnotation(DictionaryTemplate.class);
		if (annotation == null) {
			// if the interface is defined as a nested class, use the name of
			// the
			// enclosing type
			if (interfaceType.getEnclosingType() != null) {
				interfaceType = interfaceType.getEnclosingType();
			}
			return slashify(interfaceType.getQualifiedSourceName()) + TEMPLATE_SUFFIX;
		} else {
			templateName = annotation.value();
			if (!templateName.endsWith(TEMPLATE_SUFFIX)) {
				logger.die("Widget Template file name must end with " + TEMPLATE_SUFFIX);
			}

			/*
			 * If the template file name (minus suffix) has no dots, make it
			 * relative to the binder's package, otherwise slashify the dots
			 */
			String unsuffixed = templateName.substring(0, templateName.lastIndexOf(TEMPLATE_SUFFIX));
			if (!unsuffixed.contains(".")) {
				templateName = slashify(interfaceType.getPackage().getName()) + "/" + templateName;
			} else {
				templateName = slashify(unsuffixed) + TEMPLATE_SUFFIX;
			}
		}
		return templateName;
	}

	private Document parseXmlResource(final String resourcePath) throws SAXParseException, UnableToCompleteException {
		// Get the document builder. We need namespaces, and automatic expanding
		// of entity references (the latter of which makes life somewhat easier
		// for XMLElement).
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setExpandEntityReferences(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

		try {
			ClassLoader classLoader = UiBinderGenerator.class.getClassLoader();
			URL url = classLoader.getResource(resourcePath);
			if (null == url) {
				die("Unable to find resource: " + resourcePath);
			}

			InputStream stream = url.openStream();
			InputSource input = new InputSource(stream);
			input.setSystemId(url.toExternalForm());

			return builder.parse(input);
		} catch (SAXParseException e) {
			// Let SAXParseExceptions through.
			throw e;
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void die(String message) throws UnableToCompleteException {
		mlogger.die(message, (Object) null);
	}

	private static String slashify(String s) {
		return s.replace(".", "/");
	}

	private String getGenericType(Node node) throws NullPointerException {
		if (node.hasAttributes()) {
			node = node.getAttributes().getNamedItem("type");
			if (node == null) {
				throw new NullPointerException("Servicer does not define a 'type' attribute in 'servicer' tag");
			} else {
				return node.getFirstChild().getNodeValue();
			}
		} else {
			throw new NullPointerException("Servicer does not define any attributes in 'servicer' tag");
		}
	}
}
