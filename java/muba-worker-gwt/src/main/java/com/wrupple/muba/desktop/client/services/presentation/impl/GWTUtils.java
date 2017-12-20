package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterCriteria;

import java.util.*;

public class GWTUtils {

	public static class DataGroup<T extends JavaScriptObject> {
		final JsArray<T> members;
		final String value;
		final int totalPopulation;

		public DataGroup(String value, int totalPopulation) {
			super();
			this.value = value;
			this.totalPopulation = totalPopulation;
			members = JavaScriptObject.createArray().cast();
		}

		public void addMember(T entry) {
			members.push(entry);
		}

		public JsArray<T> getMembers() {
			return this.members;
		}

		public String getValue() {
			return this.value;
		}

		public int getTotalPopulation() {
			return totalPopulation;
		}

	}

	public static String[][] getCustomJoins(String raw) {
		if (raw == null) {
			return null;
		} else {
			String[] statements = raw.split(";");
			String[][] regreso = new String[statements.length][];
			String[] joinTokens;
			for (int i = 0; i < statements.length; i++) {
				joinTokens = statements[i].split(",");
				if (joinTokens.length == 3) {
					regreso[i] = joinTokens;
				} else {
					throw new IllegalArgumentException("Malformed or empty Join Statement : [" + statements[i] + "]");
				}
			}
			return regreso;
		}

	}

	public static <T extends JavaScriptObject> Collection<DataGroup<T>> groupData(List<T> values, String groupingField) {

		Map<String, DataGroup<T>> groupMap = new HashMap<String, DataGroup<T>>();
		boolean nested = groupingField.contains(".");
		String[] pathTokens;
		if (nested) {
			pathTokens = groupingField.split("\TOKEN_SPLITTER");
		} else {
			pathTokens = null;
		}
		String currentValue;
		DataGroup<T> currentGroup;
		int size = values.size();
		for (T entry : values) {
			if (nested) {
				currentValue = GWTUtils.getAttributeFromPath(entry, pathTokens);
			} else {
				currentValue = GWTUtils.getAttribute(entry, groupingField);
			}

			currentGroup = groupMap.get(currentValue);
			if (currentGroup == null) {
				currentGroup = new DataGroup<T>(currentValue, size);
				groupMap.put(currentValue, currentGroup);
			}
			currentGroup.addMember(entry);
		}

		return groupMap.values();
	}

	public static String getAttributeFromPath(JavaScriptObject o, String pathDeclaration) {
		if (pathDeclaration.contains(".")) {
			String[] pathTokens = pathDeclaration.split("\TOKEN_SPLITTER");
			return getAttributeFromPath(o, pathTokens);
		} else {
			return getAttribute(o, pathDeclaration);
		}

	}

	public static String getAttributeFromPath(JavaScriptObject o, String[] pathTokens) {
		String fieldid;
		JavaScriptObject entry = o;
		for (int j = 0; j < pathTokens.length; j++) {
			fieldid = pathTokens[j];
			if (j == pathTokens.length - 1) {
				return getAttribute(entry, fieldid);
			} else {
				entry = getAttributeAsJavaScriptObject(entry, fieldid);
				if (entry == null) {
					return null;
				}
			}
		}
		return null;
	}

	public native static void copyProperties(JavaScriptObject target, JavaScriptObject source, JsArrayString properties, String prefix) /*-{
		var k;
		for (var i = 0; i < properties.length; i++) {
			k = properties[i];
			if (prefix == null) {
				target[k] = source[k];
			} else {
				target[prefix + k] = source[k];
			}

		}
	}-*/;

	public static native void copyAllProperties(JavaScriptObject target, JavaScriptObject source) /*-{
		var sourceValue;
		for ( var k in source) {
			sourceValue = source[k];
				target[k] = sourceValue;
		}
	}-*/;

	public static JavaScriptObject getPropertiesObject(JsArrayString arr) {

		JavaScriptObject regreso = JavaScriptObject.createObject();

		if (arr == null) {
		} else {
			String property;
			String value = null;
			String element;
			int split;
			for (int i = 0; i < arr.length(); i++) {
				element = arr.get(i);
				split = element.indexOf('=');
				if (split > 0) {
					property = element.substring(0, split);
					value = element.substring(split + 1, element.length());
					GWTUtils.setAttribute(regreso, property, value);
				}
			}
		}

		return regreso;
	}

	public static native JSONValue getObjectValue(String key, JavaScriptObject jsObject) /*-{
		var v;
		// In Firefox, jsObject.hasOwnProperty(key) requires a primitive string
		key = String(key);
		if (jsObject.hasOwnProperty(key)) {
			v = jsObject[key];
		}
		var func = @com.google.gwt.json.client.JSONParser::typeMap[typeof v];
		var ret = func ? func(v)
				: @com.google.gwt.json.client.JSONParser::throwUnknownTypeException(Ljava/lang/String;)(typeof v);
		return ret;
	}-*/;

	public static native void putValueInObject(String key, JSONValue value, JavaScriptObject jsObject) /*-{
		if (value) {
			var func = value.@com.google.gwt.json.client.JSONValue::getUnwrapper()();
			jsObject[key] = func(value);
		} else {
			delete jsObject[key];
		}
	}-*/;

	/**
	 * Evaluate the passed string as Javascript
	 * 
	 * @param jsFrag
	 *            the string to evaluate
	 * @return the JavaScriptObject upon evaluation
	 */
	public static  JavaScriptObject eval(String jsFrag){
		return JsonUtils.safeEval(jsFrag);
	} /*{
        jsFrag = '(' + jsFrag + ')';
		return eval(jsFrag);
	}*/

    public static boolean isJSO(Object object) {
		return object instanceof JavaScriptObject;
	}

	public static native String getAttribute(JavaScriptObject elem, String attr) /*-{
		var ret = elem[attr];
		return (ret === undefined || ret == null) ? null : String(ret);
	}-*/;

	public static native void setAttribute(JavaScriptObject elem, String attr, String value) /*-{
		elem[attr] = value;
	}-*/;

	public static native Object getAttributeAsObject(JavaScriptObject elem, String attr) /*-{
		var ret = elem[attr];
		return (ret === undefined) ? null : ret;
	}-*/;

	public static native JavaScriptObject getAttributeAsJavaScriptObject(JavaScriptObject elem, String attr) /*-{
		var ret = elem[attr];
		return (ret === undefined) ? null : ret;
	}-*/;

	public static native JavaScriptObject[] getAttributeAsJavaScriptObjectArray(JavaScriptObject elem, String attr) /*-{
		var arrayJS = elem[attr];
		return (arrayJS === undefined) ? null
				: @com.wrupple.muba.desktop.client.JSOHelper::toArray(Lcom/google/gwt/core/client/JavaScriptObject;)(arrayJS);
	}-*/;

	public static JavaScriptObject[] toArray(JavaScriptObject array) {
		// handle case where a ResultSet is passed
		if (GWTUtils.getAttributeAsJavaScriptObject(array, "allRows") != null) {
			array = GWTUtils.getAttributeAsJavaScriptObject(array, "allRows");
		}
		int length = getJavaScriptObjectArraySize(array);
		JavaScriptObject[] recs = new JavaScriptObject[length];
		for (int i = 0; i < length; i++) {
			recs[i] = getValueFromJavaScriptObjectArray(array, i);
		}
		return recs;
	}

	public static native boolean isArray(JavaScriptObject o)/*-{
		return Object.prototype.toString.call(o) === '[object Array]';
	}-*/;

	public static native boolean hasAttribute(JavaScriptObject o, String attrib) /*-{
		return !(o[attrib] === undefined || o[attrib] == null);
	}-*/;

	public static Element[] toElementArray(JavaScriptObject array) {
		int length = getJavaScriptObjectArraySize(array);
		Element[] recs = new Element[length];
		for (int i = 0; i < length; i++) {
			recs[i] = getElementValueFromJavaScriptObjectArray(array, i);
		}
		return recs;
	}

	public static native void setAttribute(JavaScriptObject elem, String attr, JavaScriptObject[] value) /*-{
		elem[attr] = value;
	}-*/;

	public static void setAttribute(JavaScriptObject elem, String attr, int[] values) {
		setAttribute(elem, attr, GWTUtils.convertToJavaScriptArray(values));
	}

	public static void setAttribute(JavaScriptObject elem, String attr, String[] values) {
		setAttribute(elem, attr, GWTUtils.convertToJavaScriptArray(values));
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Integer[] values) {
		setAttribute(elem, attr, GWTUtils.convertToJavaScriptArray(values));
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Float[] values) {
		setAttribute(elem, attr, GWTUtils.convertToJavaScriptArray(values));
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Boolean[] values) {
		setAttribute(elem, attr, GWTUtils.convertToJavaScriptArray(values));
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Double[] values) {
		setAttribute(elem, attr, GWTUtils.convertToJavaScriptArray(values));
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Date[] values) {
		setAttribute(elem, attr, GWTUtils.convertToJavaScriptArray(values));
	}

	public static void setAttribute(JavaScriptObject elem, String attr, JavaScriptObject value) {
		setAttributeJava(elem, attr, value);
	}

	public static native void setAttributeJava(JavaScriptObject elem, String attr, Object value) /*-{
		elem[attr] = value;
	}-*/;

	public static native void setAttribute(JavaScriptObject elem, String attr, int value) /*-{
		elem[attr] = value;
	}-*/;

	public static void setAttribute(JavaScriptObject elem, String attr, Integer value) {
		if (value == null) {
			setNullAttribute(elem, attr);
		} else {
			setAttribute(elem, attr, value.intValue());
		}
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Double value) {
		if (value == null) {
			setNullAttribute(elem, attr);
		} else {
			setAttribute(elem, attr, value.doubleValue());
		}
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Float value) {
		if (value == null) {
			setNullAttribute(elem, attr);
		} else {
			setAttribute(elem, attr, value.floatValue());
		}
	}

	public static void setAttribute(JavaScriptObject elem, String attr, Boolean value) {
		if (value == null) {
			setNullAttribute(elem, attr);
		} else {
			setAttribute(elem, attr, value.booleanValue());
		}
	}

	public static native void setNullAttribute(JavaScriptObject elem, String attr) /*-{
		elem[attr] = null;
	}-*/;

	public static native void deleteAttribute(JavaScriptObject elem, String attr) /*-{
		delete elem[attr];
	}-*/;

	public static native void setAttribute(JavaScriptObject elem, String attr, boolean value) /*-{
		elem[attr] = value;
	}-*/;

	public static native void setAttribute(JavaScriptObject elem, String attr, float value) /*-{
		elem[attr] = value;
	}-*/;

	public static native void setAttribute(JavaScriptObject elem, String attr, double value) /*-{
		elem[attr] = value;
	}-*/;

	public static void setAttribute(JavaScriptObject elem, String attr, Date value) {
		if (value == null) {
			setAttribute(elem, attr, (String) null);
		} else {
			setDateAttribute(elem, attr, value.getTime());
		}
	}

	private static native void setDateAttribute(JavaScriptObject elem, String attr, double time) /*-{
		var dateJS = $wnd.Date.create();
		dateJS.setTime(time);
		elem[attr] = dateJS;
	}-*/;

	public static native void setObjectAttribute(JavaScriptObject elem, String attr, Object object) /*-{
		elem[attr] = object;
	}-*/;

	public static native Element getAttributeAsElement(JavaScriptObject elem, String attr) /*-{
		var ret = elem[attr];
		return (ret === undefined) ? null : ret;
	}-*/;

	public static native int getAttributeAsInt(JavaScriptObject elem, String attr) /*-{
		var ret = elem[attr];
		if (ret === undefined || ret == null) {
			return 0;
		} else {
			return parseInt(ret);
		}
	}-*/;

	public static native double getAttributeAsDouble(JavaScriptObject elem, String attr) /*-{
		var ret = elem[attr];
		if (ret === undefined || ret == null) {
			return -1;
		} else {
			return ret;
		}
	}-*/;

	public static native Date getAttributeAsDate(JavaScriptObject elem, String attr) /*-{
		var ret = elem[attr];
		return (ret === undefined || ret == null) ? null
				: @com.wrupple.muba.desktop.client.JSOHelper::toDate(D)(ret);
	}-*/;

	public static native Float getAttributeAsFloat(JavaScriptObject elem, String attr) /*-{
		var ret = elem[attr];
		return (ret === undefined || ret == null) ? null
				: @com.wrupple.muba.desktop.client.JSOHelper::toFloat(F)(ret);
	}-*/;

	public static int[] getAttributeAsIntArray(JavaScriptObject elem, String attr) {
		int[] rtn = null;
		JavaScriptObject hold = getAttributeAsJavaScriptObject(elem, attr);

		if (hold != null) {
			rtn = new int[getJavaScriptObjectArraySize(hold)];

			for (int i = 0; i < rtn.length; i++) {
				rtn[i] = getIntValueFromJavaScriptObjectArray(hold, i);
			}
		}
		return rtn;
	}

	public static double[] getAttributeAsDoubleArray(JavaScriptObject elem, String attr) {
		double[] rtn = null;
		JavaScriptObject hold = getAttributeAsJavaScriptObject(elem, attr);

		if (hold != null) {
			rtn = new double[getJavaScriptObjectArraySize(hold)];

			for (int i = 0; i < rtn.length; i++) {
				rtn[i] = getDoubleValueFromJavaScriptObjectArray(hold, i);
			}
		}
		return rtn;
	}

	public static native int getJavaScriptObjectArraySize(JavaScriptObject elem) /*-{
		if (elem)
			return elem.length;
		return 0;
	}-*/;

	public static native int getIntValueFromJavaScriptObjectArray(JavaScriptObject elem, int i) /*-{
		return elem[i];
	}-*/;

	public static native double getDoubleValueFromJavaScriptObjectArray(JavaScriptObject elem, int i) /*-{
		return elem[i];
	}-*/;

	public static native String getStringValueFromJavaScriptObjectArray(JavaScriptObject elem, int i) /*-{
		return elem[i];
	}-*/;

	public static native JavaScriptObject getValueFromJavaScriptObjectArray(JavaScriptObject elem, int i) /*-{
		return elem[i];
	}-*/;

	public static native boolean getAttributeAsBoolean(JavaScriptObject elem, String attr) /*-{
		var ret = elem[attr];
		return (ret == null || ret === undefined) ? false : ret;
	}-*/;

	public static Map getAttributeAsMap(JavaScriptObject elem, String attr) {
		JavaScriptObject value = getAttributeAsJavaScriptObject(elem, attr);
		return value == null ? null : convertToMap(value);
	}

	public static JavaScriptObject[] listToArray(List list) {
		JavaScriptObject[] array = new JavaScriptObject[list.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = (JavaScriptObject) list.get(i);
		}
		return array;
	}

	public static JavaScriptObject arrayConvert(Object[] array) {
		if (array == null)
			return null;
		JavaScriptObject result = newJSArray(array.length);

		for (int i = 0; i < array.length; i++) {
			arraySet(result, i, array[i]);
		}
		return result;
	}

	public static JavaScriptObject arrayConvert(JavaScriptObject[] array) {
		if (array == null)
			return null;
		JavaScriptObject result = newJSArray(array.length);

		for (int i = 0; i < array.length; i++) {
			arraySet(result, i, array[i]);
		}
		return result;
	}

	private static native JavaScriptObject newJSArray(int length) /*-{
		if (length < 0) {
			return $wnd.Array.create();
		} else {
			var arr = $wnd.Array.create();
			arr.setLength(length);
			return arr;
		}
	}-*/;

	public static native int arrayLength(JavaScriptObject array) /*-{
		return array.length;
	}-*/;

	public static native Object arrayGetObject(JavaScriptObject array, int index) /*-{
		return array[index];
	}-*/;

	public static native void arraySet(JavaScriptObject array, int index, Object value) /*-{
		array[index] = value;
	}-*/;

	public static native void arraySet(JavaScriptObject array, int index, JavaScriptObject value) /*-{
		array[index] = value;
	}-*/;

	/**
	 * This is used to access Element array as JavaScriptObject
	 */
	public static native Element getElementValueFromJavaScriptObjectArray(JavaScriptObject elem, int i) /*-{
		return elem[i];
	}-*/;

	public static native JavaScriptObject createObject() /*-{
		return new Object;
	}-*/;

	public static JavaScriptObject convertToJavaScriptArray(int[] array) {
		if (array == null)
			return null;
		JavaScriptObject jsArray = createJavaScriptArray();
		for (int i = 0; i < array.length; i++) {
			GWTUtils.setArrayValue(jsArray, i, array[i]);
		}
		return jsArray;
	}

	private static void doAddToMap(Map map, String key, Object value) {
		map.put(key, value);
	}

	public static native Map convertToMap(JavaScriptObject jsObj) /*-{
		var mapJ = @java.util.LinkedHashMap::new()();
		for ( var k in jsObj) {
			if ($wnd.isA.String(k)) {
				var value = jsObj[k];
				var valueJ = $wnd.SmartGWT.convertToJavaType(value);
				@com.wrupple.muba.desktop.client.JSOHelper::doAddToMap(Ljava/util/Map;Ljava/lang/String;Ljava/lang/Object;)(mapJ, k, valueJ);
			}
		}
		return mapJ;
	}-*/;

	public static JavaScriptObject convertToJavaScriptDate(Date date) {
		if (date == null)
			return null;
		JavaScriptObject dateJS = doConvertToJavaScriptDate(date.getTime());
		return dateJS;
	}

	private static native JavaScriptObject doConvertToJavaScriptDate(double time) /*-{
		var dateJS = $wnd.Date.create();
		dateJS.setTime(time);
		return dateJS;
	}-*/;

	public static JavaScriptObject convertToJavaScriptArray(Object[] array) {
		if (array == null)
			return null;
		JavaScriptObject jsArray = createJavaScriptArray();
		for (int i = 0; i < array.length; i++) {
			Object val = array[i];
			if (val instanceof String) {
				GWTUtils.setArrayValue(jsArray, i, (String) val);
			} else if (val instanceof Integer) {
				GWTUtils.setArrayValue(jsArray, i, ((Integer) val).intValue());
			} else if (val instanceof Float) {
				GWTUtils.setArrayValue(jsArray, i, ((Float) val).floatValue());
			} else if (val instanceof Double) {
				GWTUtils.setArrayValue(jsArray, i, ((Double) val).doubleValue());
			} else if (val instanceof Boolean) {
				GWTUtils.setArrayValue(jsArray, i, ((Boolean) val).booleanValue());
			} else if (val instanceof Date) {
				GWTUtils.setArrayValue(jsArray, i, (Date) val);
			} else if (val instanceof JavaScriptObject) {
				GWTUtils.setArrayValue(jsArray, i, ((JavaScriptObject) val));
			} /*
			 * else if (val instanceof JsObject) {
			 * JSOHelper.setArrayValue(jsArray, i, ((JsObject) val).getJsObj());
			 * }
			 */
			else if (val instanceof Object[]) {
				GWTUtils.setArrayValue(jsArray, i, convertToJavaScriptArray((Object[]) val));
			} else {
				GWTUtils.setArrayValue(jsArray, i, (val));
			}
		}
		return jsArray;

	}

	public static Integer toInteger(int value) {
		return value;
	}

	public static Long toLong(double value) {
		return (long) value;
	}

	public static Float toFloat(float value) {
		return value;
	}

	public static Double toDouble(double value) {
		return value;
	}

	private static double getTime(Date date) {
		return date.getTime();
	}

	public static Date toDate(double millis) {
		return new Date((long) millis);
	}

	public static native JavaScriptObject toDateJS(Date date) /*-{
		var dateJS = $wnd.Date.create();
		dateJS
				.setTime(@com.wrupple.muba.desktop.client.JSOHelper::getTime(Ljava/util/Date;)(date));
		return dateJS;
	}-*/;

	public static Boolean toBoolean(boolean value) {
		return value;
	}

	public static native JavaScriptObject createJavaScriptArray() /*-{
		//Important : constructing an from JSNI array using [] or new Array() results in a
		//corrupted array object in the final javascript. The array ends up havign the correct elements
		//but the test (myarr instaneof Array) fails because the jsni created array constructor is different.
		//Need to construct array within the scope of the applications iframe by using new $wnd.Array
		return $wnd.Array.create();
	}-*/;

	public static void setArrayValue(JavaScriptObject array, int index, Date value) {
		if (value == null) {
			setArrayValue(array, index, (String) null);
		} else {
			setArrayDateValue(array, index, value.getTime());
		}
	}

	private static native void setArrayDateValue(JavaScriptObject array, int index, double time) /*-{
		var dateJS = $wnd.Date.create();
		dateJS.setTime(time);
		array[index] = dateJS;
	}-*/;

	public static native void setArrayValue(JavaScriptObject array, int index, String value) /*-{
		array[index] = value;
	}-*/;

	public static native void setArrayValue(JavaScriptObject array, int index, double value) /*-{
		array[index] = value;
	}-*/;

	public static native void setArrayValue(JavaScriptObject array, int index, int value) /*-{
		array[index] = value;
	}-*/;

	public static native void setArrayValue(JavaScriptObject array, int index, float value) /*-{
		array[index] = value;
	}-*/;

	public static native void setArrayValue(JavaScriptObject array, int index, boolean value) /*-{
		array[index] = value;
	}-*/;

	public static native void setArrayValue(JavaScriptObject array, int index, JavaScriptObject value) /*-{
		array[index] = value;
	}-*/;

	public static native void setArrayValue(JavaScriptObject array, int index, Object value) /*-{
		array[index] = value;
	}-*/;

	public static native String getArrayValue(JavaScriptObject array, int index) /*-{
		var result = array[index];
		return (result == null || result === undefined) ? null : result;
	}-*/;

	public static native Object getObjectArrayValue(JavaScriptObject array, int index) /*-{
		var result = array[index];
		return (result == null || result === undefined) ? null : result;
	}-*/;

	public static native int getIntArrayValue(JavaScriptObject array, int index) /*-{
		return array[index];
	}-*/;

	public static native Integer getIntegerArrayValue(JavaScriptObject array, int index) /*-{
		var ret = array[index];
		return (ret === undefined || ret == null) ? null
				: @com.wrupple.muba.desktop.client.JSOHelper::toInteger(I)(ret);
	}-*/;

	public static native int getArrayLength(JavaScriptObject array) /*-{
		return array.length;
	}-*/;

	public static int[] convertToJavaIntArray(JavaScriptObject array) {
		int length = getArrayLength(array);
		int[] arr = new int[length];
		for (int i = 0; i < length; i++) {
			arr[i] = getIntArrayValue(array, i);
		}
		return arr;
	}

	public static Integer[] convertToJavaInterArray(JavaScriptObject array) {
		int length = getArrayLength(array);
		Integer[] arr = new Integer[length];
		for (int i = 0; i < length; i++) {
			arr[i] = getIntegerArrayValue(array, i);
		}
		return arr;
	}

	public static String[] convertToJavaStringArray(JavaScriptObject array) {
		if (array == null)
			return new String[] {};
		int length = getArrayLength(array);
		String[] arr = new String[length];
		for (int i = 0; i < length; i++) {
			arr[i] = getArrayValue(array, i);
		}
		return arr;
	}

	public static Object[] convertToJavaObjectArray(JavaScriptObject array) {
		if (array == null)
			return new Object[] {};
		int length = getArrayLength(array);
		Object[] arr = new Object[length];
		for (int i = 0; i < length; i++) {
			arr[i] = getObjectArrayValue(array, i);
		}
		return arr;
	}

	public static native void apply(JavaScriptObject config, JavaScriptObject jsObj) /*-{
		for ( var k in config) {
			jsObj[k] = config[k];
		}
	}-*/;

	public static void setAttribute(JavaScriptObject jsObj, String attr, Map valueMap) {
		JavaScriptObject valueJS = convertMapToJavascriptObject(valueMap);
		setAttribute(jsObj, attr, valueJS);
	}

	public static JavaScriptObject convertMapToJavascriptObject(Map valueMap) {
		if (valueMap == null)
			return null;
		JavaScriptObject valueJS = GWTUtils.createObject();
		for (Iterator iterator = valueMap.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Object value = valueMap.get(key);

			if (value instanceof JavaScriptObject) {
				setAttribute(valueJS, key, (JavaScriptObject) value);
			} else if (value instanceof Date) {
				setAttribute(valueJS, key, ((Date) value));
			} else if (value instanceof Number) {
				setAttribute(valueJS, key, ((Number) value).doubleValue());
			} else if (value instanceof String) {
				setAttribute(valueJS, key, ((String) value));
			} else if (value instanceof Boolean) {
				setAttribute(valueJS, key, ((Boolean) value).booleanValue());
			} else if (value == null) {
				setNullAttribute(valueJS, key);
			} else if (value instanceof String[]) {
				setAttribute(valueJS, key, convertToJavaScriptArray((String[]) value));
			} else {
				throw new IllegalArgumentException("Unsupported type for attribute " + key + " : " + value);
			}
		}
		return valueJS;
	}

	public static native JsArrayString getProperties(JavaScriptObject jsObj) /*-{
		var props = [];
		for ( var k in jsObj) {
			props.push(k);
		}
		return props
	}-*/;

	public static native String getPropertiesAsString(JavaScriptObject jsObj) /*-{
		var props = '{';
		for ( var k in jsObj) {
			props += '\n' + k;
		}
		return props + '}';
	}-*/;

	/**
	 * Adds all properties and methods from the propertiesObject to the
	 * destination object.
	 * 
	 * @param destination
	 *            the destination object
	 * @param propertiesObject
	 *            the propertiesObject
	 */
	public static native void addProperties(JavaScriptObject destination, JavaScriptObject propertiesObject) /*-{

		$wnd.isc.addProperties(destination, propertiesObject);

	}-*/;

	public static void copyIntoList(JsArray<? super JavaScriptObject> from, List<? super JavaScriptObject> to) {
		JavaScriptObject value;
		for (int i = 0; i < from.length(); i++) {
			value = from.get(i);
			to.add(value);
		}
	}

	public static native boolean performJavaScriptEquality(JavaScriptObject object, String field, String value) /*-{
		if (object == null || field == null) {
			return false;
		}
		return ("" + object[field]) == value;

	}-*/;

	public static List<Double> asLongSet(JsArrayNumber arr) {
		if (arr == null) {
			return null;
		}
		double number;
		List<Double> regreso = new ArrayList<Double>(arr.length());
		for (int i = 0; i < arr.length(); i++) {
			number = arr.get(i);
			regreso.add(number);
		}
		return regreso;
	}

	public native static void writeDefaultValuesOverNullFields(JavaScriptObject result, JavaScriptObject defaultObject) /*-{
		var defaultValue;
		var actualValue;
		for ( var key in defaultObject) {
			defaultValue = defaultObject[key];
			actualValue = result[key];
			if (actualValue === undefined || actualValue == null) {
				result[key] = defaultValue;
			}
		}

	}-*/;

	public static Set<String> asStringSet(JsArrayString arr) {
		if (arr == null) {
			return null;
		}
		String number;
		HashSet<String> regreso = new HashSet<String>(arr.length());
		for (int i = 0; i < arr.length(); i++) {
			number = arr.get(i);
			regreso.add(number);
		}
		return regreso;
	}

	public static List<String> asStringList(JsArrayString values) {
		if(values==null){
			return null;
		}
		ArrayList<String> regreso = new ArrayList<String>(values.length());
		String value;
		for (int i = 0; i < values.length(); i++) {
			value = String.valueOf(values.get(i));
			regreso.add(value);
		}
		return regreso;
	}

	public static List<String> asStringList(JsArrayNumber values) {
		JSONArray array = new JSONArray(values);
		ArrayList<String> regreso = new ArrayList<String>(values.length());
		String value;
		for (int i = 0; i < values.length(); i++) {
			value = array.get(i).isNumber().toString();
			regreso.add(value);
		}
		return regreso;
	}

	public static JsArray<JavaScriptObject> convertList(List<? extends JavaScriptObject> parameter) {
		JsArray<JavaScriptObject> regreso = JavaScriptObject.createArray().cast();
		for (JavaScriptObject o : parameter) {
			regreso.push(o);
		}
		return regreso;
	}

	private static int calculateBlue(int point, int plusvalue) {
		int plot = point % 1530;
		if (plot <= 510) {
		} else {
			plot = 1020 - plot;
		}
		return rgbFloorTop(rgbFloorTop(plot) + plusvalue);
	}

	/**
	 * Generates an array of visually distinct color hexes
	 * 
	 * @param size
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static String[] generateColorArray(int size, int r, int g, int b) {
		String[] regreso = new String[size];
		int step = 1530 / size;
		for (int i = 0; i < regreso.length; i++) {
			regreso[i] = getColorFromSpace(i * step, r, g, b);
		}
		return regreso;
	}

	/**
	 * @param b
	 * @param g
	 * @param r
	 * @param colorUnit
	 *            a number ranging from 0 to 1530 (which is the RGB color space)
	 * @return
	 */
	public static int[] getColorComponentsFromSpace(int colorPoint, int r, int g, int b) {
		int rr = calculateBlue(colorPoint + 510, r);
		int gg = calculateBlue(colorPoint + 1020, g);
		int bb = calculateBlue(colorPoint, b);
		return new int[] { rr, gg, bb };
	}

	/**
	 * @param b
	 * @param g
	 * @param r
	 * @param colorUnit
	 *            a number ranging from 0 to 1530 (which is the RGB color space)
	 * @return
	 */
	public static String getColorFromSpace(int colorPoint, int r, int g, int b) {
		int[] comps = getColorComponentsFromSpace(colorPoint, r, g, b);
		return "#" + toColorHex(comps[0]) + toColorHex(comps[1]) + toColorHex(comps[2]);
	}

	public static int getNonZeroParentHeight(Widget w) {
		if (!w.isAttached()) {
			throw new IllegalStateException("Attempting to find height of unatached widget");
		}
		int width = w.getOffsetHeight();
		Widget parent = w;
		while (width == 0) {
			parent = parent.getParent();
			width = parent.getOffsetHeight();
		}
	
		return width;
	}

	public static int getNonZeroParentWidth(Widget w) {
		if (!w.isAttached()) {
			throw new IllegalStateException("Attempting to find width of unatached widget");
		}
		int width = w.getOffsetWidth();
		Widget parent = w;
		while (width == 0) {
			parent = parent.getParent();
			width = parent.getOffsetWidth();
		}
	
		return width;
	}

	private static int rgbFloorTop(int point) {
		if (point > 255) {
			return 255;
		}
		if (point < 0) {
			return 0;
		}
		return point;
	}

	private static String toColorHex(int component) {
		String regreso = Integer.toHexString(component);
		if (component <= 15) {
			regreso = "0" + regreso;
		}
		return regreso;
	}

	public static String asTokenizedAddress(String[] tokens) {
		if(tokens==null){
			return null;
		}else{
			StringBuilder b = new StringBuilder(tokens.length*10);
			for(int i = 0 ; i < tokens.length; i++){
				b.append(tokens[i]);
				if(i!=(tokens.length-1)){
					b.append('/');
				}
			}
			return b.toString();
		}
		
	}

	////////////////////////////////////////////////////////////////////////////
    //               MOVED FROM FIELD CONVERSTON STRATEGY
    ////////////////////////////////////////////////////////////////////////////
    private native void parseSetDouble(String v, CatalogEntry jso, String fieldId) /*-{
		jso[fieldId]=parseFloat(v);
	}-*/;

    private native void parseSetInteger(String v, CatalogEntry jso, String fieldId) /*-{
	jso[fieldId]=parseInt(v);
}-*/;


	private Object userReadableValue(CatalogEntry elem, String attr, List<FilterCriteria> includeCriteria) {
		JSONValue value = access.getObjectValue(attr, elem);
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




	protected Object getDefaultValue(JSONValue value, List<FilterCriteria> includeCriteria) {
		return value.toString();
	}

	protected Object getStringValue(String string, List<FilterCriteria> includeCriteria) {
		// TODO parse Double?
		return string.trim();
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

}
