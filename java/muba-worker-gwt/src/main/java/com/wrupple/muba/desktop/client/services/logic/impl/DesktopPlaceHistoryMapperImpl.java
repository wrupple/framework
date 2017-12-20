package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wrupple.muba.desktop.domain.DesktopPlace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class DesktopPlaceHistoryMapperImpl implements PlaceHistoryMapper {

	@Inject
	public DesktopPlaceHistoryMapperImpl() {
		super();
	}

	@Override
	public Place getPlace(String historyToken) {

		String activity;
		String paramenters;
		// Split history token into activity uri & parameter chain
		if (historyToken == null) {
			activity = "";
			paramenters = null;
		} else {
			int index = historyToken.indexOf('?');
			if (index != -1) {
				activity = historyToken.substring(0, index);
				paramenters = historyToken.substring(index + 1, historyToken.length());
			} else {
				activity = historyToken;
				paramenters = null;
			}
		}
		// create place from activity uri
		String[] activityTokens = activity.split("/");
		DesktopPlace place = new DesktopPlace(activityTokens);
		// and add all known parameters
		if (paramenters != null) {
			String[] paramString = paramenters.split("&");
			Map<String, String> params = new HashMap<String, String>(paramString.length + 1);
			String key;
			String value;
			int separatorIndex;
			for (String param : paramString) {
				separatorIndex = param.indexOf('=');
				if (separatorIndex > 0) {
					key = param.substring(0, separatorIndex);
					value = param.substring(separatorIndex + 1, param.length());
					key = URL.decodeQueryString(key);
					value = URL.decodeQueryString(value);
					params.put(key, value);
				}
			}
			place.setProperties(params);
		}

		return place;
	}

	@Override
	public String getToken(Place place) {
		DesktopPlace item = (DesktopPlace) place;
		List<String> parameterBlacklist = item.getTaskTokens();
		String[] tokens = item.getTokens();
		Map<String, String> paramMap = item.getProperties();
		StringBuilder builder;
		if (tokens == null) {
			if (paramMap == null) {
				return null;
			} else {
				builder = new StringBuilder(paramMap.size() * 16);
			}
		} else {
			if (paramMap == null) {
				builder = new StringBuilder((tokens.length * 10));
			} else {
				builder = new StringBuilder((tokens.length * 10) + (paramMap.size() * 16));
			}

			for (int i = 0; i < tokens.length; i++) {
				builder.append(tokens[i]);
				if (i < (tokens.length - 1)) {
					builder.append('/');
				}
			}

		}

		if (paramMap != null) {
			builder.append('?');
			int count = 0;
			String value = null;
			String encodedValue;
			Set<String> keySet = paramMap.keySet();
			for (String k : keySet) {
				//never show in url parameters in task tokens, or image or label
				if (!(k.equals(DesktopPlace.IMAGE_PARAMETER) || k.equals(DesktopPlace.LABEL_PARAMETER ) || (parameterBlacklist!=null && parameterBlacklist.contains(k)))) {
					if (count > 0) {
						builder.append('&');
					}
					value = paramMap.get(k);
					if (value != null) {
						builder.append(k);
						builder.append('=');
						encodedValue = URL.encodeQueryString(value);
						builder.append(encodedValue);
						count++;
					}
				}

			}
		}

		return builder.toString();
	}


}
