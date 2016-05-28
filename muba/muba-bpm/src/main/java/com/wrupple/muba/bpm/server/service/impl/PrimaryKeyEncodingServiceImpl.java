package com.wrupple.muba.bpm.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.apache.commons.lang3.math.NumberUtils;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.server.services.PrimaryKeyEncodingService;

@Singleton
public class PrimaryKeyEncodingServiceImpl implements PrimaryKeyEncodingService {

	@Override
	public Object encodeClientPrimaryKeyFieldValue(Object rawValue,
			FieldDescriptor field, CatalogDescriptor catalog) {
		if(rawValue==null){
			return null;
		}
		if (field.isMultiple()) {
			List<Object> rawCollection = (List<Object>) rawValue;
			List<String> encodedValues = new ArrayList<String>(
					rawCollection.size());
			boolean wasTested = false;
			boolean expectLongValues = false;
			for (Object rawCollectionValue : rawCollection) {
				if (!wasTested) {
					wasTested =true;
					expectLongValues = rawCollectionValue instanceof Long;
				}
				if (expectLongValues) {
					encodedValues.add(encodeLongKey((Long) rawCollectionValue));
				} else {
					encodedValues.add(String.valueOf(rawCollectionValue));
				}
			}
			return encodedValues;
		} else {
			if(field.isKey()||field.getDataType()==CatalogEntry.INTEGER_DATA_TYPE){
				try{
					return encodeLongKey((Long) rawValue);
				}catch(Exception e){
					System.err.println("Unable to encode numeric key most likely already a String");
					return String.valueOf(rawValue);
				}
				
			}else{
				return rawValue;
			}
		}
	}

	


	@Override
	public List<Object> decodePrimaryKeyFilters(List<Object> values) {
		if(values!=null&&!values.isEmpty()){
			try{
				List<Object> ids = new ArrayList<Object>(values.size());
				String raw;
				for(int  i = 0; i< values.size(); i++){
					raw = (String) values.get(i);
					ids.add(decodeKey(raw));
				}
				return ids;
			}catch(NumberFormatException e){
				return values;
			}catch(ClassCastException e){
				return values;
			}
		}
		return values;
	}
	
	@Override
	public boolean qualifiesForEncoding(FieldDescriptor field, CatalogDescriptor catalog) {
		return field.isKey();
	}

	

	@Override
	public String decodePrimaryKeyToken(String targetEntryId,
			CatalogDescriptor catalogDescriptor) {
		// null check!
		//decodeKey(targetEntryId)
		return targetEntryId;
	}
	
	private String encodeLongKey(Long key) {
		return Long.toString(key, 36);
	}

	
	private Long decodeKey(String key) {
		return Long.parseLong(key,36);
	}




	@Override
	public boolean isPrimaryKey(String vanityId) {
		return NumberUtils.isDigits(vanityId);
	}
}
