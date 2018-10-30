package com.wrupple.muba.catalogs.client.services.evaluation.impl;

import com.google.gwt.core.client.*;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.catalogs.client.services.ClientCatalogCacheManager;
import com.wrupple.muba.catalogs.client.services.evaluation.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.CatalogCache;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.*;
import com.wrupple.vegetate.domain.*;

import javax.inject.Provider;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Singleton
public class WruppleCatalogEvaluationDelegate implements CatalogEvaluationDelegate {

	@Override
	public JsArray<JsCatalogEntry> getCachedFilteredEntries(CatalogDescriptor descriptor, CatalogCache cache, JsFilterData filter, EventBus bus, ClientCatalogCacheManager ccm) {
		// GWT.log(new JSONObject(filter).toString());
		JsArray<JsFilterCriteria> filters = filter.getFilterArray();
		JsArray<JsFilterDataOrdering> orders = filter.getOrderArray();
		JsArray<JsArrayString> joins = filter.getJoinsArray(false);

		JsArray<JsCatalogEntry> fullCache = ((JsMemoryCache) cache).getBackingArray().cast();

		int fullCacheLength = fullCache.length();
		/*
		 * PROCESS DATA
		 */

		/*
         * TODO WE ARE PROCESSING JOINS AND EVALUATING EPHEMERALS ON EACH CACHE
		 * READ... while this preserves real-time-ness ... it's really wastefull
		 *
		 * FIXME join tree structures (children)
		 */
		processJoinData(fullCache, filter, joins, descriptor, ccm);

		Collection<FieldDescriptor> fields = descriptor.getOwnedFieldsValues();
		String formula;
		for (FieldDescriptor field : fields) {
			if (field.getFormula() != null) {
				formula = field.getFormula();
				eval(descriptor, field, fullCache, formula);
			}
		}

		JsArray<JsCatalogEntry> filterMatches = JavaScriptObject.createArray().cast();

		/*
		 * FIND MATCHES
		 */

		JsCatalogEntry entry;

		for (int i = 0; i < fullCacheLength; i++) {
			entry = fullCache.get(i);
			// GWT.log("[ATTEMPT TO MATCH "+new JSONObject(entry).toString());
			// GWT.log(" INTO : "+new
			// JSONObject((JavaScriptObject)filters).toString());

			if (matchAgainstFilters(entry, filters, descriptor)) {
				// GWT.log(" ENTRY PASSED]");
				filterMatches.push(entry);
			} else {
				// GWT.log(" ENTRY DID NOT PASS]");
			}
		}

		/*
		 * ORDER ALL ENTRIES IN CACHE
		 */
		if (orders != null && orders.length() > 0) {
			// fullCache = copy(fullCache);
			List<JsCatalogEntry> wrapper = JsArrayList.arrayAsList(filterMatches);
			FilterDataOrdering order;
			JsComparator comparator = new JsComparator(null, true);
			for (int i = orders.length() - 1; i >= 0; i--) {
				order = orders.get(i);
				comparator.setField(order.getField());
				comparator.setAsc(order.isAscending());
				Collections.sort(wrapper,comparator );
			}
		}

		/*
		 * CUT RANGE
		 */

		// total filter matches are enough to satisfy the last required entry?
		// if (filterMatchesLength >= filterEnd) {
		// return truncated or complete result
		return cutRange(filterMatches, filter.getStart(), filter.getLength());
    }

    //math implements recursive functions so treeIndex must use those expression trees
    private final Evaluator delegate;
    /*
     * TODO this effectively renders the entire evaluation system incompatible
     * setRuntimeContext internet explorer prior to ie-8 (see math js docs for solution that
     * should be delivered by server-side adding of scripts (many) ONLY to i-e
     * users) loaded MathJs expression evaluator
     * http://mathjs.org/examples/expressions.js.html
     */
    private Provider<StorageManager> catalogDescriptionService;
    private Provider<DesktopManager> dmp;

    @Inject
    public WruppleCatalogEvaluationDelegate(Provider<StorageManager> catalogDescriptionService, Provider<DesktopManager> dmp) {
        this.catalogDescriptionService = catalogDescriptionService;
        this.dmp = dmp;
        MathJsApi INSTANCE = GWT.create(MathJsApi.class);
        String js = INSTANCE.get().getText();
        ScriptInjector.fromString(js).inject();
        this.delegate = Evaluator.createEvaluator();
        JsArrayString contextExpressions = WruppleJsBridge.getGlobalContextExpressions();
        JavaScriptObject globalScope = JavaScriptObject.createObject();
        if (contextExpressions != null) {
            String expr;


			/*
			 * Import native functions
			 */

            WruppleJsBridge.setPutCatalogs(globalScope);
            WruppleJsBridge.setTreeSum(globalScope);
            WruppleJsBridge.setPPath(globalScope);
			
			/*TODO
			 * Import System Functions overwriting domain scope
			 */
            for (int i = 0; i < contextExpressions.length(); i++) {
                expr = contextExpressions.get(i);
                try {
                    if (expr.startsWith("registerFactory:")) {
                        // FIXME if domain initialization params are hijacked, a sub-api could
                        // be built to send sensible data to another server (by building an
                        // image or something)
                        WruppleJsBridge.registerFromWindow(expr.substring(expr.indexOf(':') + 1), globalScope);
                    } else {
                        delegate.eval(expr, globalScope);
                    }
                } catch (Exception e) {
                    GWT.log("Error initializing evaluator: " + expr, e);
                }

            }
            delegate.importScope(globalScope);
        }

    }

    /*
     *
     * FIELD EVALUATION
     */
	@Override
	public void validate(CatalogDescriptor catalog, String fieldId, JsCatalogEntry value, JsArrayString violations) {

		ScopedEvaluator scoped = delegate.scoped();

		JsFieldDescriptor field = (JsFieldDescriptor) catalog.getFieldDescriptor(fieldId);
		if(field==null){
			violations.push("{catalog.invalidField}");
			return ;
		}
		JsArray<JsConstraint> constraintsDef = field.getConstraintsValuesArray();
		if(constraintsDef!=null && constraintsDef.length()>0){
			StringBuffer parameterDeclaration;
			scoped.assignFromSource(value, fieldId, Constraint.EVALUATING_VARIABLE);
			JsConstraint constraint;
			JsArrayString properties;
			String property;
			String jsrConstraint;
			String violation;
			String parameters;
			int delimeterIndex;
			for(int i = 0; i< constraintsDef.length(); i++){
				constraint = constraintsDef.get(i);
				jsrConstraint=constraint.getConstraint();
				properties = constraint.getPropertiesArray();
				if(properties!=null && properties.length()>0){
					parameterDeclaration = new StringBuffer(Constraint.EVALUATING_VARIABLE.length()+properties.length()*7);
					parameterDeclaration.append(Constraint.EVALUATING_VARIABLE);
					//properties define the variables of validation
					for(int j = 0 ; j<properties.length(); j++){
						property = properties.get(j);
						delimeterIndex = property.indexOf('=');
						if(delimeterIndex>0){
							parameterDeclaration.append(',');
							parameterDeclaration.append(property.substring(0, delimeterIndex));
						}
						scoped.eval(property);
					}
				}else{
					parameterDeclaration =null;
				}
				if(parameterDeclaration==null){
					parameters = Constraint.EVALUATING_VARIABLE;
				}else{
					parameters = parameterDeclaration.toString();
				}

				violation = scoped.evalExpectingString(jsrConstraint+"("+parameters+")");
				if(violation!=null){
                    violations.push(violation);
                }
			}
		}

    }

    private boolean mathAgainstCriteria(JsCatalogKey entry, JsFilterCriteria criteria, FieldDescriptor fieldDescriptor) {
        String operator = criteria.getOperator();
        JsArrayString fieldTokens = criteria.getPathArray();
        JsArrayMixed values = criteria.getValuesArray().cast();

        int valuesSize = values.length();
        boolean match;
        boolean mustMatchAll = !(FilterData.EQUALS.equals(operator) || FilterData.DIFFERENT.equals(operator));
        boolean matchedAtLeastOne = false;
        boolean nested = fieldTokens.length() > 1 && (fieldDescriptor.isEphemeral() || fieldDescriptor.isKey());
        for (int i = 0; i < valuesSize; i++) {
            // perform or
            match = matchRecursive(entry, fieldTokens, operator, values, 0, i, nested, mustMatchAll);
            if (match) {
                if (mustMatchAll) {
                    matchedAtLeastOne = true;
                } else {
                    return true;
                }
            } else {
                if (mustMatchAll) {
                    return false;
                }
            }
        }
        return matchedAtLeastOne;

    }


	@Override
	public void eval(CatalogDescriptor c, FieldDescriptor field, JsCatalogKey entry, String rawFormula) {
		JsCatalogDescriptor catalog = (JsCatalogDescriptor) c;
		JavaScriptObject scope = newScope(catalog, field,entry);
		
		ParsedExpression parsedFormula = delegate.parse(rawFormula);
		CompiledExpression formula = parsedFormula.compile(delegate);

		// FIXME interpret ephemeral value formula as constraint (different interpret)

		formula.evalAssign(scope, field.getFieldId(), entry,field.alwaysRecalculate());
	}
	
	@Override
	public void eval(CatalogDescriptor c, FieldDescriptor field, JsArray<JsCatalogEntry> result, String rawFormula) {
		JsCatalogDescriptor catalog = (JsCatalogDescriptor) c;
		ParsedExpression parsedFormula = delegate.parse(rawFormula);
		CompiledExpression formula = parsedFormula.compile(delegate);
		JavaScriptObject scope = newScope(catalog,field, null);
		JsCatalogKey entry;
		for(int i = 0 ; i < result.length(); i++ ){
			entry = result.get(i);
			scope = rewriteScope(catalog, entry, scope);
			formula.evalAssign(scope, field.getFieldId(), entry,field.alwaysRecalculate());
		}
	}


	@Override				
	public void eval(CatalogDescriptor c, FieldDescriptor field, List<JsCatalogEntry> entries, String rawFormula) {
		JsCatalogDescriptor catalog = (JsCatalogDescriptor) c;
		ParsedExpression parsedFormula = delegate.parse(rawFormula);
		CompiledExpression formula = parsedFormula.compile(delegate);
		JavaScriptObject scope = newScope(catalog,field, null);
		for(JsCatalogKey entry : entries){
			scope = rewriteScope(catalog, entry, scope);
			formula.evalAssign(scope, field.getFieldId(), entry,field.alwaysRecalculate());
		}
		
	}
	

	
	private native void setFieldValue(String fieldId, JavaScriptObject scope, JsCatalogEntry value,String f) /*-{
		scope[f]=value[fieldId];
	}-*/;

	
	
	/*
	 * 
	 * FILTER MATCHING
	 */
	
	@Override
	public boolean matchAgainstFilters(JsCatalogKey entry, JsArray<JsFilterCriteria> filters, CatalogDescriptor descriptor) {
		if (entry == null) {
			return false;
		} else if (filters == null || filters.length() == 0) {
			return true;
		} else {
			JsFilterCriteria criteria;
			String firstPathToken;
			String rootKeyField;
			FieldDescriptor fieldDescriptor;
			for (int i = 0; i < filters.length(); i++) {
				criteria = filters.get(i);
				firstPathToken = criteria.getPath(0);
				fieldDescriptor = descriptor.getFieldDescriptor(firstPathToken);
				if (fieldDescriptor == null) {
					rootKeyField = null;
					// attempt to find root key field
					if (firstPathToken.endsWith(JsCatalogEntry.MULTIPLE_FOREIGN_KEY)) {
						rootKeyField = firstPathToken.substring(0, firstPathToken.length() - 6);
					} else if (firstPathToken.endsWith(JsCatalogEntry.FOREIGN_KEY)) {
						rootKeyField = firstPathToken.substring(0, firstPathToken.length() - 5);
					}
					if (rootKeyField != null) {
						fieldDescriptor = descriptor.getFieldDescriptor(rootKeyField);
					}
				}
				if (fieldDescriptor != null) {

					if (!mathAgainstCriteria(entry, criteria, fieldDescriptor)) {
						// GWT.log(" FAILED ON CRITERIA  : "+new
						// JSONObject((JavaScriptObject)criteria).toString());
						return false;
					}
				}
			}
			return true;
		}
	}


	@Override
	public JsArrayMixed getTranslatedFilterValues(JsArrayMixed rawValues, int dataType, String operator) {
		if (FilterData.REGEX.equals(operator)) {
			return regrexArray(rawValues);
		} else if (FilterData.LIKE.equals(operator)) {
			JsArrayMixed exprexions = translateToExpresions(rawValues);
			return regrexArray(exprexions);
		} else if (FilterData.STARTS.equals(operator)) {
			JsArrayMixed exprexions = translateToStartsWithExpresions(rawValues);
			return regrexArray(exprexions);
		} else if (FilterData.ENDS.equals(operator)) {
			JsArrayMixed exprexions = translateToEndsWithExpresions(rawValues);
			return regrexArray(exprexions);
		} else {
			switch (dataType) {
			case CatalogEntry.BOOLEAN_DATA_TYPE:
				return readBooleans(rawValues);
			case CatalogEntry.NUMERIC_DATA_TYPE:
			case CatalogEntry.INTEGER_DATA_TYPE:
				return readNumbers(rawValues);
			case CatalogEntry.DATE_DATA_TYPE:
				// date format is sortable as a natural string
			case CatalogEntry.LARGE_STRING_DATA_TYPE:
			case CatalogEntry.STRING_DATA_TYPE:
			case CatalogEntry.BLOB_DATA_TYPE:
			default:
				// do nothing
				return rawValues.cast();
			}

		}
	}
	

	

	@Override// TODO rewrite as a more efficient native method?
	public JsArray<JsCatalogEntry> getCachedEntriesByKeyCriteria(CatalogCache cache, JsFilterCriteria keyCriteria, EventBus bus) {
		JsArray<JavaScriptObject> regreso = JavaScriptObject.createArray().cast();
		JsArrayMixed valuesArr = keyCriteria.getValuesArray();
		JsArrayNumber valuesArrNumber = valuesArr.cast();
		String entryId;
		JavaScriptObject entry;
		int number;

		for (int i = 0; i < valuesArr.length(); i++) {
			try {
				entryId = valuesArr.getString(i);
			} catch (Exception e) {
				number = (int) valuesArrNumber.get(i);
				entryId = String.valueOf(number);
			}
			entry = cache.read(entryId);
			if (entry != null) {
				regreso.push(entry.cast());
			}
		}
		if (regreso != null) {
			int expectedSize = keyCriteria.getValuesArray().length();
			int actualSize = regreso.length();
			if (expectedSize == actualSize) {
				return regreso.cast();
			} else {
				return null;
			}
		}
		return null;
	}

	/*
	 * 
	 * JOINING DATA
	 */

	@Override
	public JsArray<JsCatalogEntry> processJoinData(JsArray<JsCatalogEntry> result, JsFilterData filter, JsArray<JsArrayString> joins,
			CatalogDescriptor descriptor, ClientCatalogCacheManager ccm) {
		if (joins != null && joins.length() > 0) {
			String foreignCatalogId;
			String foreignField;
			String localField;
			CatalogCache cache;
			FieldDescriptor field;
			FieldDescriptor foreignFieldDescriptor;
			Collection<FieldDescriptor> thisCatalogFields;
			CatalogDescriptor foreignCatalogDescriptor;
			JsArrayString sentence;
			String[] localFieldTokens;
			for (int joinIndex = 0; joinIndex < joins.length(); joinIndex++) {
				sentence = joins.get(joinIndex);
				foreignCatalogId = sentence.get(0);
				foreignField = sentence.get(1);
				localField = sentence.get(2);
				cache = ccm.getIdentityCache(foreignCatalogId);
				if (cache != null) {

					if (JsCatalogEntry.ID_FIELD.equals(foreignField)) {
						// local field is a set of foreign keys
						if (localField.contains(".")) {
							localFieldTokens = localField.split("\TOKEN_SPLITTER");
							nestedJoinOneToMany(result, cache, localFieldTokens);
						} else {
							field = descriptor.getFieldDescriptor(localField);
							joinOneToMany(result, cache, field);
						}
					} else {
						// is there a field where to store incomming values?
						field = null;
						thisCatalogFields = descriptor.getOwnedFieldsValues();
						for (FieldDescriptor d : thisCatalogFields) {
							if (d.isEphemeral() && foreignCatalogId.equals(d.getForeignCatalogName())) {
								// this ephemeral field points to the joined
								// catalog
								field = d;
							}
						}
						if (field != null) {

							foreignCatalogDescriptor = this.catalogDescriptionService.get().loadFromCache(dmp.get().getCurrentActivityHost(), dmp.get().getCurrentActivityDomain(), foreignCatalogId);
							foreignFieldDescriptor = foreignCatalogDescriptor.getFieldDescriptor(foreignField);
							// find foreign fields where foreignField is local
							// entry id
							joinManyToOne(result, field.getFieldId(), foreignField, cache, foreignFieldDescriptor.isMultiple());
						}
					}
				}
			}
		}
		return result;
	}

	private void nestedJoinOneToMany(JsArray<JsCatalogEntry> result, CatalogCache cache, String[] localFieldTokens) {
		// TODO support a more rubust method that loads required catalog
		// descriptors, and supports array fields

		int length = result.length();
		JsCatalogKey entry;
		JsCatalogKey temp;
		String fieldid = null;
		boolean brokenChain;
		for (int i = 0; i < length; i++) {
			entry = result.get(i);

			brokenChain = false;
			for (int j = 0; j < localFieldTokens.length; j++) {
				fieldid = localFieldTokens[j];
				if (j == localFieldTokens.length - 1) {
					// lastToken
				} else {
					temp = entry.getForeignKeyValue(fieldid);
					if (temp == null) {
						brokenChain = true;
						break;
					} else {
						entry = temp;
					}
				}
			}
			if (fieldid != null && !brokenChain) {
				oneToMany(fieldid, fieldid + JsCatalogEntry.FOREIGN_KEY, false, entry, (JavaScriptObject) cache);
			}
		}
	}

	private void joinManyToOne(JsArray<JsCatalogEntry> result, String targetField, String foreignField, CatalogCache cache, boolean multiple) {
		String localId;
		JsCatalogKey entry;
		for (int i = 0; i < result.length(); i++) {
			entry = result.get(i);
			localId = entry.getId();
			manyToOne(entry, targetField, localId, foreignField, (JavaScriptObject) cache, multiple);
		}
	}

	private void joinOneToMany(JsArray<JsCatalogEntry> result, CatalogCache cache, FieldDescriptor field) {
		String fieldid = field.getFieldId();
		boolean multiple = field.isMultiple();
		String targetField = fieldid + (multiple ? JsCatalogEntry.MULTIPLE_FOREIGN_KEY : JsCatalogEntry.FOREIGN_KEY);
		JsCatalogKey entry;
		for (int i = 0; i < result.length(); i++) {
			entry = result.get(i);
			// GWT.log(new JSONObject(entry).toString());
			// GWT.log(new JSONObject((JavaScriptObject) cache).toString());
			oneToMany(fieldid, targetField, multiple, entry, (JavaScriptObject) cache);
		}
	}

	private native void manyToOne(JsCatalogKey entry, String targetField, String localId, String foreignField, JavaScriptObject cache, boolean multiple) /*-{
		var foreignEntries = cache.identityCache;
		if (foreignEntries != null) {
			var valuesToStore = null;
			var foreignEntry;
			var foreignFieldValue;
			var rawLocalId;
			var atomicForeignFieldValue;
			for ( var foreignKey in foreignEntries) {
				foreignEntry = foreignEntries[foreignKey];
				foreignFieldValue = foreignEntry[foreignField];
				rawLocalId = entry.id;
				if (foreignFieldValue != null) {
					if (multiple) {
						var atomicForeignFieldValue;
						for (var i = 0; i < foreignFieldValue.length; i++) {
							atomicForeignFieldValue = foreignFieldValue[i];
							if (rawLocalId == atomicForeignFieldValue
									|| localId == atomicForeignFieldValue
									|| localId == (atomicForeignFieldValue + "")) {
								if (valuesToStore == null) {
									valuesToStore = [];
								}
								valuesToStore.push(foreignEntry);
							}
						}
					} else {
						if (rawLocalId == foreignFieldValue
								|| localId == (foreignFieldValue + "")) {
							if (valuesToStore == null) {
								valuesToStore = [];
							}
							valuesToStore.push(foreignEntry);
						}
					}
				}
			}
			entry[targetField] = valuesToStore;
		}
	}-*/;

	private native void oneToMany(String fieldid, String targetField, boolean multiple, JsCatalogKey entry, JavaScriptObject cache) /*-{
		var fieldValue = entry[fieldid];
		var keyValue;
		var foreignEntry;
		if (fieldValue != null) {
			if (multiple) {
				var multipleValue = [];
				for (var i = 0; i < fieldValue.length; i++) {
					keyValue = fieldValue[i];
					foreignEntry = cache.identityCache[keyValue];
					if (foreignEntry != null) {
						multipleValue.push(foreignEntry);
					}
				}
				entry[targetField] = multipleValue;
			} else {
				keyValue = fieldValue;
				foreignEntry = cache.identityCache[keyValue];
				entry[targetField] = foreignEntry;
			}
		}
	}-*/;


	private JavaScriptObject newScope(JsCatalogDescriptor catalog, FieldDescriptor field, JsCatalogKey entry) {
		JavaScriptObject template = catalog.getEntryEvaluationScope();
		if(template==null){
			template=JavaScriptObject.createObject();
			
			GWTUtils.setAttribute(template, CatalogActionRequest.CATALOG_ID_PARAMETER, catalog.getCatalogId());
			JsArrayString contextExpressions = catalog.getContextExpressionsArray();
			if(contextExpressions!=null){
				String expr ;
				for(int i = 0 ; i< contextExpressions.length() ; i++){
					expr = contextExpressions.get(i);
					delegate.eval(expr, template);
				}
			}
			catalog.setEntryEvaluationScope(template);
		}
		rewriteScope(catalog, entry, template);
		return template;
	}

	private JavaScriptObject rewriteScope(JsCatalogDescriptor catalog, JsCatalogKey entry, JavaScriptObject template) {
//_valorGrupal*_reglaAplicable
		if(entry!=null){
			GWTUtils.setAttribute(template, "entry", entry);
			JsArrayString fields = catalog.getFieldNamesAsJsArray();
			if (fields != null) {
				GWTUtils.copyProperties(template, entry, fields, "_");
			}
		}
		
		return template;
	}

    public interface MathJsApi extends ClientBundle {
        @Source("math.min.js")
        TextResource get();
    }

	private JsArrayMixed readBooleans(JsArrayMixed rawValues) {
		if (rawValues == null || rawValues.length() == 0) {
			return rawValues;
		} else {
			return rawValues.cast();
		}

	}

	private native JsArrayMixed readNumbers(JsArrayMixed exprexions) /*-{
		var expression;
		for (var i = 0; i < exprexions.length; i++) {
			expression = exprexions[i];
			exprexions[i] = Number(expression);
		}
		return exprexions;
	}-*/;

	private JsArrayMixed translateToEndsWithExpresions(JsArrayMixed rawValues) {
		String endsWith;
		StringBuilder regex;
		for (int i = 0; i < rawValues.length(); i++) {
			endsWith = rawValues.getString(i);
			regex = new StringBuilder(endsWith.length() + 6);
			regex.append("^.*");
			endsWith = escapeRegexSpecials(endsWith);
			regex.append(endsWith);
			regex.append('$');
			rawValues.set(i, regex.toString());
		}
		return rawValues;
	}

	private JsArrayMixed translateToStartsWithExpresions(JsArrayMixed rawValues) {
		String startsWith;
		StringBuilder regex;
		for (int i = 0; i < rawValues.length(); i++) {
			startsWith = rawValues.getString(i);
			regex = new StringBuilder(startsWith.length() + 6);
			regex.append('^');
			startsWith = escapeRegexSpecials(startsWith);
			regex.append(startsWith);
			regex.append(".*$");
			rawValues.set(i, regex.toString());
		}
		return rawValues;
	}

	private JsArrayMixed translateToExpresions(JsArrayMixed rawValues) {
		String wildcardExpresion;
		String regex;
		for (int i = 0; i < rawValues.length(); i++) {
			wildcardExpresion = rawValues.getString(i);
			regex = toRegex(wildcardExpresion);
			rawValues.set(i, regex);
		}
		return rawValues;
	}

	private native String escapeRegexSpecials(String str)/*-{
		return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?TOKEN_SPLITTER\\\^\$\|]/g, "\\$&");
	}-*/;

	private String toRegex(String wildcard) {
		if (wildcard == null) {
			return null;
		}

		StringBuilder buffer = new StringBuilder(wildcard.length() * 2);

		buffer.append("^");
		char[] chars = wildcard.toCharArray();

		boolean foundBackSlash = false;
		for (int i = 0; i < chars.length; ++i) {

			if (chars[i] == '\\') {
				// found backslash
				if (foundBackSlash) {
					buffer.append("\\\\");
				}
				foundBackSlash = true;
			} else {
				if (chars[i] == '*') {
					if (foundBackSlash) {
						buffer.append(chars[i]);
					} else {
						buffer.append(".*");
					}
				} else if (chars[i] == '?') {
					if (foundBackSlash) {
						buffer.append(chars[i]);
					} else {
						buffer.append('.');
					}
					// maybe +()^$.{}[]| withouth backslash?
				} else if ("+()^$.{}[]|".indexOf(chars[i]) != -1) {
					if (foundBackSlash) {
						buffer.append("\\\\");
					}
					buffer.append('\\');
					buffer.append(chars[i]);
				} else {
					if (foundBackSlash) {
						buffer.append("\\\\");
					}
					buffer.append(chars[i]);
				}

				foundBackSlash = false;
			}

		}
		buffer.append("$");

		return buffer.toString();
	}

	private native JsArrayMixed regrexArray(JsArrayMixed exprexions) /*-{
		var expression;
		var regex;
		for (var i = 0; i < exprexions.length; i++) {
			expression = exprexions[i];
			regex = new RegExp(expression, "i");
			exprexions[i] = regex;
		}
		return exprexions;
	}-*/;

	private boolean matchRecursive(JavaScriptObject entry, JsArrayString fieldTokens, String operator, JsArrayMixed values, int pathTokenIndex,
			int filterIndex, boolean nested, boolean mustMatchAll) {
		String field = fieldTokens.get(pathTokenIndex);

		// is last token
		if (pathTokenIndex == (fieldTokens.length() - 1)) {
			boolean matched = matchFinal(entry, field, operator, values, filterIndex);
			// GWT.log(JSOHelper.getAttribute(entry, field)+" == "+new
			// JSONObject(values)+"@"+filterIndex+"  IS  "+matched);
			return matched;
		}
		int newPathTokenIndex = pathTokenIndex + 1;
		JavaScriptObject nestedEntry = GWTUtils.getAttributeAsJavaScriptObject(entry, field);
		if (nestedEntry == null) {
			// establish a criteria for null values along the path?
			return false;
		}
		if (GWTUtils.isArray(nestedEntry)) {
			JsArray<JavaScriptObject> nestedArray = nestedEntry.cast();
			boolean match;
			boolean matchedAtLeastOne = false;
			for (int i = 0; i < nestedArray.length(); i++) {
				nestedEntry = nestedArray.get(i);
				match = matchRecursive(nestedEntry, fieldTokens, operator, values, newPathTokenIndex, filterIndex, nested, mustMatchAll);
				if (match) {
					if (mustMatchAll) {
						matchedAtLeastOne = true;
					} else {
						return true;
					}
				} else {
					if (mustMatchAll) {
						return false;
					}
				}
			}
			return matchedAtLeastOne;
		} else {
			return matchRecursive(nestedEntry, fieldTokens, operator, values, newPathTokenIndex, filterIndex, nested, mustMatchAll);
		}
	}

	private native boolean matchFinal(JavaScriptObject entry, String field, String operator, JavaScriptObject values, int filterIndex) /*-{
		var field;
		var value = values[filterIndex];
		var comparableValue = entry[field];

		if (comparableValue == null) {
			if ("null" == value) {
				return true;
			} else {
				return false;
			}
		} else {
			if ("==" == operator) {
				return comparableValue == value;
			} else if ("!=" == operator) {
				return comparableValue != value;
			} else if (">" == operator) {
				return comparableValue > value;
			} else if ("<" == operator) {
				return comparableValue < value;
			} else if (">=" == operator) {
				return comparableValue >= value;
			} else if ("<=" == operator) {
				return comparableValue <= value;
			} else if (operator == @com.wrupple.vegetate.domain.FilterData::LIKE
					|| operator == @com.wrupple.vegetate.domain.FilterData::REGEX
					|| operator == @com.wrupple.vegetate.domain.FilterData::STARTS
					|| operator == @com.wrupple.vegetate.domain.FilterData::ENDS) {
				//expects elements to have been translated into regular expressions
				return value.test(comparableValue);
			}
			return false;
		}
	}-*/;
	
	private JsArray<JsCatalogEntry> cutRange(JsArray<JsCatalogEntry> filterMatches, int start, int length) {
		JsArray<JsCatalogEntry> regreso = JavaScriptObject.createArray().cast();
		JsCatalogEntry entry;
		for (int i = start; regreso.length() < length && i < filterMatches.length(); i++) {
			entry = filterMatches.get(i);
			if (entry == null) {
				break;
			} else {
				regreso.push(entry);
			}
		}

		return regreso;
	}
	
	

}
