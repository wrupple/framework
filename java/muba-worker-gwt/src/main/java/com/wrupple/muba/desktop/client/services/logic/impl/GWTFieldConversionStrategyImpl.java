package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONValue;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsFilterCriteria;
import com.wrupple.muba.worker.shared.services.FieldConversionStrategy;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;

import java.util.Collection;
import java.util.List;

public class GWTFieldConversionStrategyImpl implements FieldConversionStrategy {

	@Override
	public Object convertToPresentableValue(String attr, JavaScriptObject elem, List<FilterCriteria> includeCriteria) {
		Object userVersion = userReadableValue(elem, attr, includeCriteria);
		return userVersion;
	}

	@Override
	public Object convertToPresentableValue(JSONValue value) {
		return userReadableValue(value, null);
	}

	@Override
	public void setAsPersistentDatabaseValue(Object value, FieldDescriptor field, JavaScriptObject jso) {
		String fieldId = field.getFieldId();
		int dataType = field == null ? -1 : field.getDataType();
		if (value == null) {
			GWTUtils.deleteAttribute(jso, fieldId);
		} else if (value instanceof Collection) {
			Collection<Object> collection = (Collection<Object>) value;
			GWTUtils.setAttribute(jso, fieldId, GWTUtils.convertToJavaScriptArray(collection.toArray()));
		} else if (value instanceof Number) {
			Number v = (Number) value;
			if (dataType == CatalogEntry.INTEGER_DATA_TYPE) {
				GWTUtils.setAttribute(jso, fieldId, v.intValue());
			} else {
				GWTUtils.setAttribute(jso, fieldId, v.doubleValue());
			}
		} else if (value instanceof String) {
			String v = (String) value;
			v = getSendableString(v);
			if (v == null) {
				GWTUtils.deleteAttribute(jso, fieldId);
			}else{
				if(field.isMultiple()){
					GWTUtils.setAttribute(jso, fieldId, GWTUtils.eval(v));
				}else{
					try {
						switch (dataType) {
						case CatalogEntry.BOOLEAN_DATA_TYPE:
							GWTUtils.setAttribute(jso, fieldId, "1".equals(v) || Boolean.parseBoolean(v));
							break;
						case CatalogEntry.INTEGER_DATA_TYPE:
							if(field.isKey()){
								GWTUtils.setAttribute(jso, fieldId, v);
							}else{
								parseSetInteger(v,jso,fieldId);
							}
							break;
						case CatalogEntry.NUMERIC_DATA_TYPE:
							parseSetDouble(v,jso,fieldId);
							break;
						default:
							GWTUtils.setAttribute(jso, fieldId, v);
						}
					} catch (Exception e) {
						GWTUtils.setAttribute(jso, fieldId, v);
					}
				}
			}
			

		} else if (value instanceof JavaScriptObject) {

			GWTUtils.setAttribute(jso, fieldId, (JavaScriptObject)value);

		} else {
			GWTUtils.setAttributeJava(jso, fieldId, value);
		}
	}

	@Override
	public Object convertToPersistentDatabaseValue(Object value, FieldDescriptor field) {
		int dataType = field == null ? -1 : field.getDataType();
		if (value == null) {
			return null;
		} else if (value instanceof Collection) {
			Collection<Object> collection = (Collection<Object>) value;
			 return GWTUtils.convertToJavaScriptArray(collection.toArray());
		} else if (value instanceof Number) {
			Number v = (Number) value;
			if (dataType == CatalogEntry.INTEGER_DATA_TYPE) {
				return v.intValue();
			} else {
				return v.doubleValue();
			}
		} else if (value instanceof String) {
			String v = (String) value;
			v = getSendableString(v);
			if (v == null) {
				return null;
			}
			
			try {
				switch (dataType) {
				case CatalogEntry.BOOLEAN_DATA_TYPE:
					return  "1".equals(v) || Boolean.parseBoolean(v);
				case CatalogEntry.INTEGER_DATA_TYPE:
					if(field.isKey()){
						return v;
					}
					return Integer.parseInt(v);
				case CatalogEntry.NUMERIC_DATA_TYPE:
					return Double.parseDouble(v);
				default:
					return v;
				}
			} catch (Exception e) {
				return v;
			}

		} else if (value instanceof JavaScriptObject) {

			return value;

		} else {
			return value;
		}
	}



	private native void parseSetDouble(String v, JavaScriptObject jso, String fieldId) /*-{
		jso[fieldId]=parseFloat(v);
	}-*/;

	private native void parseSetInteger(String v, JavaScriptObject jso, String fieldId) /*-{
	jso[fieldId]=parseInt(v);
}-*/;

	private String getSendableString(String v) {
		if (v == null || (v.trim().isEmpty())) {
			return null;
		}
		return v.trim();
	}

	private Object userReadableValue(JavaScriptObject elem, String attr, List<FilterCriteria> includeCriteria) {
		JSONValue value = GWTUtils.getObjectValue(attr, elem);
		return userReadableValue(value, includeCriteria);
	}

	protected Object getDefaultValue(JSONValue value, List<FilterCriteria> includeCriteria) {
		return value.toString();
	}

	protected Object getStringValue(String string, List<FilterCriteria> includeCriteria) {
		// TODO parse Double?
		return string.trim();
	}

	protected Object getBoolanValue(boolean bool, List<FilterCriteria> includeCriteria) {
		return bool;
	}

	protected Object getJSOValue(JavaScriptObject jso, List<FilterCriteria> includeCriteria) {
		return jso;
	}

	protected Object getArrayValue(JsArray<JavaScriptObject> arr, List<FilterCriteria> includeCriteria) {
		// FIXME use the same (more tested) mechanism as
		// IncrementalCachingRetrivingService to test filters
		if (includeCriteria == null) {
			return arr;
		} else {
			if (arr == null) {
				return null;
			} else {
				JavaScriptObject o;
				boolean match;
				JsArray<JavaScriptObject> regreso = JavaScriptObject.createArray().cast();
				for (int i = 0; i < arr.length(); i++) {
					o = arr.get(i);
					match = matchesCriteria(o, includeCriteria);
					if (match) {
						// include
						regreso.push(o);
					}
				}
				return regreso;
			}
		}
	}

	private boolean matchesCriteria(JavaScriptObject o, List<FilterCriteria> includeCriteria) {
		for (FilterCriteria criteria : includeCriteria) {
			if (matches((JsFilterCriteria) criteria, o)) {
				return true;
			}
		}
		return false;
	}

	private Object userReadableValue(JSONValue value, List<FilterCriteria> includeCriteria) {
		if (value == null) {
			return getNullObject();
		} else if (value.isNull() != null) {
			return getNullObject();
		} else if (value.isArray() != null) {
			JsArray<JavaScriptObject> arr = value.isArray().getJavaScriptObject().cast();
			return getArrayValue(arr, includeCriteria);
		} else if (value.isObject() != null) {
			JavaScriptObject jso = value.isObject().getJavaScriptObject();
			return getJSOValue(jso, includeCriteria);
		} else if (value.isBoolean() != null) {
			boolean bool = value.isBoolean().booleanValue();
			return getBoolanValue(bool, includeCriteria);
		} else if (value.isNumber() != null) {
			JSONNumber jsonNumber = value.isNumber();
			return jsonNumber.toString();
		} else {
			try {
				String string = value.isString().stringValue();
				return getStringValue(string, includeCriteria);
			} catch (Exception e) {
				return getDefaultValue(value, includeCriteria);
			}
		}
	}

	// TRUE IF MATCH AGINST AT LEAST ONE CRITERIA
	public boolean matches(JsFilterCriteria criteria, JavaScriptObject o) {
		JsArrayMixed values = criteria.getValuesArray();
		JsArrayString path = criteria.getPathArray();
		if (path != null && values != null) {
			if (values.length() > 0 && path.length() > 0) {
				String pathing = path.get(0);
				for (int i = 0; i < values.length(); i++) {
					if (jsMatch(pathing, o, values, i)) {
						return true;
					}
				}
			}
		}
		return false;
    }

    private native boolean jsMatch(String pathing, JavaScriptObject o, JsArrayMixed values, int i) /*-{
        var rawValue = o[pathing];
		var string = values[i];
		if (string != null && rawValue != null) {
			var equals = rawValue == string;
			return equals;
		}
		return false;

	}-*/;

	protected Object getNullObject() {
		return null;
	}

    public static void setAttribute(JavaScriptObject elem, String attr, Object systemValue) {
        // TODO this seems excesive, allow conversion strategy to set field
        // value?
        if (systemValue instanceof Boolean) {
            boolean bolVal = (Boolean) systemValue;
            GWTUtils.setAttribute(elem, attr, bolVal);
        } else if (systemValue instanceof Integer) {
            int intval = (Integer) systemValue;
            GWTUtils.setAttribute(elem, attr, intval);
        } else if (systemValue instanceof Double) {
            double doubleval = (Double) systemValue;
            GWTUtils.setAttribute(elem, attr, doubleval);
        } else if (systemValue instanceof Long) {
            String stirngval = systemValue.toString();
            GWTUtils.setAttribute(elem, attr, stirngval);
        } else {
            GWTUtils.setAttributeJava(elem, attr, systemValue);
        }

    }

}
