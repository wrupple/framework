package com.wrupple.muba.desktop.domain;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.i18n.client.DateTimeFormat;

import javax.inject.Provider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DesktopLoadingStateHolder {
	private HashMap<String, Provider<? extends Activity>> activityMap;
	private HashMap<String, String> properties;
	public static String[] homeActivity;
	public static String datePattern;
	private static DateTimeFormat format;
	public static String defaultCurrencyCode;

	public DesktopLoadingStateHolder() {
		super();
		activityMap = new HashMap<String, Provider<? extends Activity>>();
	}

	public Map<String, Provider<? extends Activity>> getActivityMap() {
		return Collections.unmodifiableMap(activityMap);
	}

	public void registerAll(Map<String, Provider<? extends Activity>> activities) {
		activityMap.putAll(activities);
	}

	/**
	 * @param activityMap
	 *            the activityMap to set
	 */
	public void setActivityMap(HashMap<String, Provider<? extends Activity>> activityMap) {
		this.activityMap = activityMap;
	}

	public void setHomeActivity(String[] homeActivity) {
		DesktopLoadingStateHolder.homeActivity = homeActivity;
	}

	public String[] getHomeActivity() {
		return homeActivity;
	}

	public void addMetaProperty(String metaTagName, String metaContent) {
		if (properties == null) {
			properties = new HashMap<String, String>();
		}
		properties.put(metaTagName, metaContent);
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	public static DateTimeFormat getFormat() {
		if (format == null) {
			format = DateTimeFormat.getFormat(datePattern);
		}
		return format;
	}

}
