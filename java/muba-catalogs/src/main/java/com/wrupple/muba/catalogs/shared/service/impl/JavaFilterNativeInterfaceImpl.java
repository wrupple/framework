package com.wrupple.muba.catalogs.shared.service.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.server.service.FilterNativeInterface;

import javax.inject.Inject;
import java.util.List;
import java.util.regex.Pattern;

/** Source WruppleCatalogEvaluationDelegate:matchAgainstFilters
 * VegetateStorageUnit:getTranslatedFilterValues
 * Created by rarl on 7/06/17.
 */
public class JavaFilterNativeInterfaceImpl implements FilterNativeInterface{

    private final JavaObjectNativeInterface oni;

    @Inject
    public JavaFilterNativeInterfaceImpl(JavaObjectNativeInterface oni) {
        this.oni = oni;
    }

    @Override
    public  boolean jsMatch(String pathing, CatalogEntry o, List<Object> values, int valueIndex, Instrospection instrospection){
         /*-{
		var rawValue = o[pathing];
		var string = values[i];
		if (string != null && rawValue != null) {
			var equals = rawValue == string;
			return equals;
		}
		return false;

	}-*/;

        //var rawValue = o[pathing];
        Object rawValue = oni.getPropertyValue(o,pathing, instrospection);
        //var string = values[i];
        Object string = values.get(valueIndex);
        if (string != null && rawValue != null) {

            return rawValue.equals(string);
        }
        return false;

    }

    @Override
    public boolean matchAgainstFilters(CatalogEntry entry, List<FilterCriteria> filters, CatalogDescriptor descriptor, Instrospection instrospection) {
        if (entry == null) {
            return false;
        } else if (filters == null || filters.size() == 0) {
            return true;
        } else {
            FilterCriteria criteria;
            String firstPathToken;
            String rootKeyField;
            FieldDescriptor fieldDescriptor;
            for (int i = 0; i < filters.size(); i++) {
                criteria = filters.get(i);
                firstPathToken = criteria.getPath(0);
                fieldDescriptor = descriptor.getFieldDescriptor(firstPathToken);
                if (fieldDescriptor == null) {
                    rootKeyField = null;
                    // attempt to find root key field
                    if (firstPathToken.endsWith(CatalogEntry.MULTIPLE_FOREIGN_KEY)) {
                        rootKeyField = firstPathToken.substring(0, firstPathToken.length() - 6);
                    } else if (firstPathToken.endsWith(CatalogEntry.FOREIGN_KEY)) {
                        rootKeyField = firstPathToken.substring(0, firstPathToken.length() - 5);
                    }
                    if (rootKeyField != null) {
                        fieldDescriptor = descriptor.getFieldDescriptor(rootKeyField);
                    }
                }
                if (fieldDescriptor != null) {

                    if (!mathAgainstCriteria(entry, criteria, fieldDescriptor, instrospection)) {
                        // GWT.log(" FAILED ON CRITERIA  : "+new
                        // JSONObject((JavaScriptObject)criteria).toString());
                        return false;
                    }
                }
            }
            return true;
        }
    }


    private boolean mathAgainstCriteria(CatalogEntry entry, FilterCriteria criteria, FieldDescriptor fieldDescriptor, Instrospection instrospection) {
        String operator = criteria.getOperator();
        List<String> fieldTokens = criteria.getPath();
        List<Object> values = criteria.getValues();

        int valuesSize = values.size();
        boolean match;
        boolean mustMatchAll = !(FilterData.EQUALS.equals(operator) || FilterData.DIFFERENT.equals(operator));
        boolean matchedAtLeastOne = false;
        boolean nested = fieldTokens.size() > 1 && (fieldDescriptor.isEphemeral() || fieldDescriptor.isKey());
        for (int i = 0; i < valuesSize; i++) {
            // perform or
            match = matchRecursive(entry, fieldTokens, operator, values, 0, i, nested, mustMatchAll, instrospection);
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

    private boolean matchRecursive(CatalogEntry entry, List<String> fieldTokens, String operator, List<Object> values, int pathTokenIndex,
                                   int filterIndex, boolean nested, boolean mustMatchAll, Instrospection instrospection) {
        String field = fieldTokens.get(pathTokenIndex);

        // is last token
        if (pathTokenIndex == (fieldTokens.size() - 1)) {
            boolean matched = matchFinal(entry, field, operator, values, filterIndex, instrospection);
            // GWT.log(JSOHelper.getAttribute(entry, field)+" == "+new
            // JSONObject(values)+"@"+filterIndex+"  IS  "+matched);
            return matched;
        }
        int newPathTokenIndex = pathTokenIndex + 1;
        //Object nestedEntry = GWTUtils.getAttributeAsJavaScriptObject(entry, field);
        Object nestedEntry = (CatalogEntry) oni.getPropertyValue(entry,field, instrospection);
        if (nestedEntry == null) {
            // establish a criteria for null values along the path?
            return false;
        }

        if (nestedEntry instanceof List) {
            List<Object> nestedArray = (List<Object>)nestedEntry;
            boolean match;
            boolean matchedAtLeastOne = false;
            for (int i = 0; i < nestedArray.size(); i++) {
                nestedEntry = (CatalogEntry) nestedArray.get(i);
                match = matchRecursive((CatalogEntry) nestedEntry, fieldTokens, operator, values, newPathTokenIndex, filterIndex, nested, mustMatchAll, instrospection);
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
            return matchRecursive((CatalogEntry) nestedEntry, fieldTokens, operator, values, newPathTokenIndex, filterIndex, nested, mustMatchAll, instrospection);
        }
    }


    private boolean matchFinal(CatalogEntry entry, String field, String operator, List<Object> values, int filterIndex, Instrospection instrospection) {

		//var value = values[filterIndex];
        Object value = values.get(filterIndex);
		Object comparableValue = oni.getPropertyValue(entry,field, instrospection);

		if (comparableValue == null) {
			if ("null".equals(value)) {
				return true;
			} else {
				return false;
			}
		} else {
			if ("==" == operator) {
				return comparableValue .equals( value);
			} else if ("!=" == operator) {
				return comparableValue != value;
			} else if (">" == operator) {
				//return comparableValue > value;
                return ((Comparable)comparableValue).compareTo(value)>0;
			} else if ("<" == operator) {
				//return comparableValue < value;
                return ((Comparable)comparableValue).compareTo(value)<0;
			} else if (">=" == operator) {
				//return comparableValue >= value;
                return ((Comparable)comparableValue).compareTo(value)>=0;
			} else if ("<=" == operator) {
				//return comparableValue <= value;
                return ((Comparable)comparableValue).compareTo(value)<=0;
			} else if (operator.equals(FilterData.LIKE)
					|| operator .equals( FilterData.REGEX)
					|| operator .equals( FilterData.STARTS)
					|| operator .equals( FilterData.ENDS)) {
				//expects elements to have been translated into regular expressions
				//return value.test(comparableValue);

                return((Pattern)value).matcher((CharSequence) value).find();
			}
			return false;
		}
	}

}
