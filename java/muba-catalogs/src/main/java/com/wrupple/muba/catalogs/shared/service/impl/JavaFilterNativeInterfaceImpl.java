package com.wrupple.muba.catalogs.shared.service.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;
import com.wrupple.muba.catalogs.shared.service.FilterNativeInterface;
import com.wrupple.muba.catalogs.shared.service.ObjectNativeInterface;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by rarl on 7/06/17.
 */
public class JavaFilterNativeInterfaceImpl implements FilterNativeInterface{

    private final FieldAccessStrategy oni;

    @Inject
    public JavaFilterNativeInterfaceImpl(FieldAccessStrategy oni) {
        this.oni = oni;
    }

    @Override
    public  boolean jsMatch(String pathing, Object o, List<Object> values, int valueIndex, FieldAccessStrategy.Session session){
         /*-{
		var rawValue = o[pathing];
		var string = values[i];
		if (string != null && rawValue != null) {
			var equals = rawValue == string;
			return equals;
		}
		return false;

	}-*/;
        // FIXME use the same (more tested) mechanism as
        // VegetateStorageUnitImpl to test filters
        var rawValue = o[pathing];
        var string = values[i];
        if (string != null && rawValue != null) {
            var equals = rawValue == string;
            return equals;
        }
        return false;

    }

    @Override
    public boolean matchAgainstFilters(CatalogEntry entry, List<FilterCriteria> filters, CatalogDescriptor descriptor, FieldAccessStrategy.Session session) {
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


    private boolean mathAgainstCriteria(CatalogEntry entry, FilterCriteria criteria, FieldDescriptor fieldDescriptor) {
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

    private boolean matchRecursive(CatalogEntry entry, List<String> fieldTokens, String operator, List<Object> values, int pathTokenIndex,
                                   int filterIndex, boolean nested, boolean mustMatchAll) {
        String field = fieldTokens.get(pathTokenIndex);

        // is last token
        if (pathTokenIndex == (fieldTokens.size() - 1)) {
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

}
