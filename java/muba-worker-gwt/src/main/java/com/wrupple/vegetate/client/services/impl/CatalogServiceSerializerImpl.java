package com.wrupple.vegetate.client.services.impl;

import com.google.gwt.json.client.JSONObject;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogActionRequest;
import com.wrupple.muba.desktop.domain.overlay.JsonVegetateResponse;
import com.wrupple.vegetate.client.services.CatalogServiceSerializer;

public class CatalogServiceSerializerImpl implements CatalogServiceSerializer {

	@Override
	public JsonVegetateResponse deserialize(String string) throws Exception {
		if(string==null){
			return null;
		}
		return GWTUtils.eval(string).cast();
	}

	@Override
	public String serialize(JsCatalogActionRequest object) throws Exception {
		if(object ==null){
			return null;
		}
		return new JSONObject(object).toString();
	}

	

}
