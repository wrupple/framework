package com.wrupple.vegetate.server.domain;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.chain.CatalogFactory;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.VegetateAuthenticationToken;

@Singleton
public class VegetateAuthenticationTokenDescriptor extends CatalogDescriptorImpl {

	private static final long serialVersionUID = -7624136841856921924L;

	@Inject
	public VegetateAuthenticationTokenDescriptor( CatalogFactory handlersDictionary, @Named(VegetateAuthenticationToken.CATALOG)Class clazz,  PrimaryKeyField id,NameField name) {
		super(VegetateAuthenticationToken.CATALOG, clazz, serialVersionUID, "Authentication Token", id,name);
		FieldDescriptorImpl field ;
		field = new FieldDescriptorImpl().makeDefault("realm", "Realm", "listPicker", CatalogEntry.INTEGER_DATA_TYPE);
		Iterator<String> asfsd = handlersDictionary.getCatalog(VegetateAuthenticationToken.REALM_PARAMETER).getNames();
		
		if(asfsd.hasNext()){
			ArrayList<String> options = new ArrayList<String>(5);
			while(asfsd.hasNext()){
				options.add(asfsd.next());
			}
			field.setDefaultValueOptions(options);
		}
		putField(field);
		field = new FieldDescriptorImpl().makeDefault("properties", "Credentials", "multiText", CatalogEntry.STRING_DATA_TYPE);
		field.setMultiple(true);
		putField(field);
		field = new FieldDescriptorImpl().makeDefault("callback", "Callback", "text", CatalogEntry.STRING_DATA_TYPE);
		putField(field);
	}

}
