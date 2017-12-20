package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.*;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.JSOAdapterCell.JSOAdapter;

public class StringJSOadapter implements JSOAdapter<String> {

	private static StringJSOadapter instance = new StringJSOadapter();

	protected StringJSOadapter() {
		super();
	}

	public JavaScriptObject toJSO(String value) {
		return asJavascriptObject(value);
	}

	public static native String asString(Object key) /*-{
																return key+"";
																}-*/;

	public static native JavaScriptObject asJavascriptObject(String key)/*-{
																		return key+"";
																		}-*/;

	public static String performTransformation(Object temp) {
		return asString(temp);

	}

	public static JSONValue performJSONValueTransformation(String value) {
		if (value == null) {
			return JSONNull.getInstance();
		}
		try {
			double d = Double.parseDouble(value);
			return new JSONNumber(d);
		} catch (Exception e) {
			boolean b = Boolean.parseBoolean(value);
			if (b) {
				return JSONBoolean.getInstance(b);
			} else {
				if (value.equalsIgnoreCase("false")) {
					return JSONBoolean.getInstance(false);
				}
			}
		}
		return new JSONString(value);
	}

	public static StringJSOadapter getInstance() {
		return instance;
	}

	@Override
	public String fromJSO(Object value) {
		return performTransformation(value);
	}

	public static Object performValueTransformation(String value) {
		if (value == null) {
			return null;
		}
		try {
			double d = Double.parseDouble(value);
			return d;
		} catch (Exception e) {
			try {
				boolean b = Boolean.parseBoolean(value);
				return b;
			} catch (Exception f) {
				
			}
		}
		return value;
	}

}
