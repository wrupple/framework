package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.desktop.client.service.impl.JacksonCatalogEntrySerializationServiceImpl.FieldDeserializer;
import com.wrupple.muba.desktop.client.service.impl.JacksonCatalogEntrySerializationServiceImpl.RawFieldReaderMap;
import com.wrupple.vegetate.domain.CatalogEntry;

import javax.inject.Inject;

public class RawFieldReaderMapImpl implements RawFieldReaderMap {

	private BooleanFieldDeserializer bolean;
	private NumericFieldDataAccessObject numeric;
	private IntegerFieldDataAccessObject integer;
	private StringFieldDataAccessObject string;
	private LargeStringFieldDataAccessObjectImpl largeString;
	private DateFieldDataAccessObject date;

	@Inject
	public RawFieldReaderMapImpl(BooleanFieldDeserializer bolean, NumericFieldDataAccessObject numeric, IntegerFieldDataAccessObject integer,
			LargeStringFieldDataAccessObjectImpl largeString, StringFieldDataAccessObject string, DateFieldDataAccessObject date) {
		super();
		this.bolean = bolean;
		this.numeric = numeric;
		this.integer = integer;
		this.string = string;
		this.largeString = largeString;
		this.date = date;

	}

	@Override
	public FieldDeserializer get(int dataType) {
		switch (dataType) {
		case CatalogEntry.BOOLEAN_DATA_TYPE:
			return bolean;
		case CatalogEntry.NUMERIC_DATA_TYPE:
			return numeric;
		case CatalogEntry.INTEGER_DATA_TYPE:
			return integer;
		case CatalogEntry.STRING_DATA_TYPE:
			return string;
		case CatalogEntry.LARGE_STRING_DATA_TYPE:
			return largeString;
		case CatalogEntry.DATE_DATA_TYPE:
			return date;
		case CatalogEntry.CATALOG_ENTRY_DATA_TYPE:
		case CatalogEntry.BLOB_DATA_TYPE:
			return null;
		}
		return null;
	}

}
