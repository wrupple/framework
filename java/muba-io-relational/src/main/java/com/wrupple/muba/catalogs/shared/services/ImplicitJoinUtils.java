package com.wrupple.muba.catalogs.shared.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.CatalogKey;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.PersistentImageMetadata;
import com.wrupple.muba.catalogs.domain.WrupleSVGDocument;
import com.wrupple.muba.catalogs.domain.WruppleAudioMetadata;
import com.wrupple.muba.catalogs.domain.WruppleFileMetadata;
import com.wrupple.muba.catalogs.domain.WruppleVideoMetadata;
import com.wrupple.muba.catalogs.server.service.CatalogManager;

public class ImplicitJoinUtils {

	public static String[][] getJoins(CatalogManager serverSide,Object clientSide,CatalogDescriptor descriptor,String[][] customJoins, Object domain,String host) throws Exception {
		// TODO configure how many levels/orders deep to go into for
		// joinable fields

		Collection<FieldDescriptor> thisCatalogFields = descriptor.getFieldsValues();
		List<FieldDescriptor> thisCatalogJoinableFields = new ArrayList<FieldDescriptor>();
		String foreignCatalogId;
		String foreignField;
		String localField;

		// GATHER JOINABLE FIELDS

		for (FieldDescriptor field : thisCatalogFields) {
			if (isJoinableValueField(field)) {
				thisCatalogJoinableFields.add(field);
			}
		}
		int size = thisCatalogJoinableFields.size();
		int customJoinsSize = customJoins == null ? 0 : customJoins.length;
		// Generate join sentence
		String[][] allJoinSentences = new String[size + customJoinsSize][];
		FieldDescriptor currentJoinableField;
		int i;
		CatalogDescriptor foreign;
		for (i = 0; i < size; i++) {
			currentJoinableField = thisCatalogJoinableFields.get(i);
			foreignCatalogId = currentJoinableField.getCatalog();
			localField = null;
			foreignField = null;
			if(serverSide==null){
				throw new RuntimeException("not implemented");
				//foreign =clientSide.loadFromCache(host, (String)domain, foreignCatalogId);
			}else{
				foreign =serverSide.getDescriptorForName(foreignCatalogId,(CatalogActionContext) domain);
			}
			
			if (currentJoinableField.isKey()) {
				localField = currentJoinableField.getFieldId();
				foreignField = getCatalogKeyFieldId(foreign);
			} else if (currentJoinableField.isEphemeral()) {
				localField = descriptor.getKeyField();
				foreignField = getIncomingForeignJoinableFieldId(foreign,descriptor.getCatalog());
			}
			if(localField==null){
				localField=CatalogKey.ID_FIELD;
			}
			allJoinSentences[i] = new String[] { foreignCatalogId, foreignField, localField };
		}
		if (customJoins != null) {
			for (String[] customJoinStatement : customJoins) {
				allJoinSentences[i] = customJoinStatement;
				i++;
			}
		}
		return allJoinSentences;
	}

	public static String getIncomingForeignJoinableFieldId(CatalogDescriptor foreignDescriptor,String catalog) {
		
			Collection<FieldDescriptor> fields = foreignDescriptor.getFieldsValues();
			String fieldsForeignCatalog;
			for (FieldDescriptor field : fields) {
				fieldsForeignCatalog = field.getCatalog();
				if (catalog.equals(fieldsForeignCatalog)) {
					return field.getFieldId();
				}
			}
			throw new IllegalArgumentException("No fields in " + foreignDescriptor.getCatalog()
					+ " point to " + catalog);

	}

	private static String getCatalogKeyFieldId(CatalogDescriptor descriptor) {
		if (descriptor == null) {
			return CatalogEntry.ID_FIELD;
		} else {
			String keyField = descriptor.getKeyField();
			if (keyField == null) {
				// FIXME validate produced catalog descriptors properly when
				// generated, recover from "poorly formed" persistent
				// descriptors
				return CatalogEntry.ID_FIELD;
			} else {
				return keyField;
			}
		}
	}
	
	public static boolean isJoinableValueField(FieldDescriptor field) {
		return (field.getCatalog() != null&& (field.isEphemeral()|| field.isKey() && !isFileField(field)));
	}
	
	public static boolean isFileField(FieldDescriptor field){
		String catalog = field.getCatalog();
		return (field.isKey()&& catalog!=null && (catalog.equals(PersistentImageMetadata.CATALOG)||catalog.equals(WrupleSVGDocument.CATALOG)||catalog.equals(WruppleFileMetadata.CATALOG)||catalog.equals(WruppleAudioMetadata.CATALOG)||catalog.equals(WruppleVideoMetadata.CATALOG) ));
	}
}
