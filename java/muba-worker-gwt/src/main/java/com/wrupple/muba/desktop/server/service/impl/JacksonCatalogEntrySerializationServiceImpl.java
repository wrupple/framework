package com.wrupple.muba.desktop.server.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyEncodingService;
import com.wrupple.muba.desktop.server.service.JacksonCatalogDeserializationService;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class JacksonCatalogEntrySerializationServiceImpl implements JacksonCatalogDeserializationService {

	public interface FieldDeserializer {

		Object deserializeValue(JsonNode value, FieldDescriptor field);

		List<FilterCriteria> rewriteFilterListIteratorData(List<FilterCriteria> criteria, FieldDescriptionService descriptor);

	}

	public interface RawFieldReaderMap {

		FieldDeserializer get(int dataType);

	}

	private final ObjectMapper mapper;
	private final RawFieldReaderMap fieldDataStore;
	private String ancestorIdField;
	private PrimaryKeyEncodingService pkes;
	private final PersistentCatalogEntityFactory entityFactory;
	private final Boolean keyFieldCreatable;

	@Inject
	public JacksonCatalogEntrySerializationServiceImpl(@Named("keyFieldCreatable") Boolean keyFieldCreatable, PersistentCatalogEntityFactory entityFactory,
			PrimaryKeyEncodingService pkes, @Named("ancestorIdField") String ancestorIdField, ObjectMapper mapper, RawFieldReaderMap fieldDataStore) {
		super();
		this.keyFieldCreatable = keyFieldCreatable;
		this.entityFactory = entityFactory;
		this.pkes = pkes;
		this.ancestorIdField = ancestorIdField;
		this.mapper = mapper;
		this.fieldDataStore = fieldDataStore;
	}

	@Override
	public CatalogEntry deserialize(ObjectNode root, CatalogDescriptor descriptor, Context context) throws Exception {
		Class<? extends CatalogEntry> clazz = ((CatalogExcecutionContext) context).getTargetClass();
		preprocess(root, descriptor);
		if (PersistentCatalogEntity.class.equals(clazz)) {
			return deserializeEntity(clazz, root, descriptor);
		} else {
			JsonParser traverser = root.traverse();
			return mapper.readValue(traverser, clazz);
		}
	}

	@Override
	public CatalogEntry deserialize(String rawSeed, CatalogDescriptor descriptor, CatalogExcecutionContext context) throws Exception {
		Class<? extends CatalogEntry> clazz = CatalogExcecutionContext.asClass(descriptor);
		ObjectNode root = (ObjectNode) mapper.readTree(rawSeed);
		preprocess(root, descriptor);
		if (PersistentCatalogEntity.class.equals(clazz)) {
			return deserializeEntity(clazz, root, descriptor);
		} else {
			JsonParser traverser = root.traverse();
			return mapper.readValue(traverser, clazz);
		}
	}

	private void preprocess(ObjectNode root, CatalogDescriptor descriptor) {
		Iterator<FieldDescriptor> catalogFieldIterator = descriptor.fieldIterator();
		FieldDescriptor campoALeer;
		String fieldId;
		JsonNode rawValue;
		while (catalogFieldIterator.hasNext()) {
			campoALeer = catalogFieldIterator.next();
			fieldId = campoALeer.getFieldId();
			rawValue = root.get(fieldId);
			if (rawValue != null) {
				if (rawValue.isNull()) {
					root.remove(fieldId);
				} else {
					// CHECK PRECONDITIONS

					checkPrimaryKeyEncoding(root, rawValue, campoALeer, descriptor);
				}
			}

		}
	}

	private void checkPrimaryKeyEncoding(ObjectNode root, JsonNode rawValue, FieldDescriptor field, CatalogDescriptor descriptor) {

		if (pkes.qualifiesForEncoding(field, descriptor)) {

			decodePrimaryKeyField(root, rawValue, field, descriptor);

		}

	}

	public void decodePrimaryKeyField(ObjectNode root, JsonNode rawValue, FieldDescriptor field, CatalogDescriptor descriptor) {
		String fieldName = field.getFieldId();

		if ((!keyFieldCreatable) && CatalogEntry.ID_FIELD.equals(fieldName)) {
			root.remove(fieldName);
		} else {
			String unencoded = root.get(fieldName).textValue();
			String decoded = pkes.decodePrimaryKeyToken(unencoded, descriptor);
			if (decoded != unencoded) {
				root.set(fieldName, new TextNode(decoded));
			}
		}
	}

	private CatalogEntry deserializeEntity(Class<? extends CatalogEntry> clazz, ObjectNode root, CatalogDescriptor descriptor)
			throws JsonProcessingException, IOException {
		PersistentCatalogEntity regreso = entityFactory.newEntity(descriptor);

		Iterator<FieldDescriptor> catalogFieldIterator = descriptor.fieldIterator();
		FieldDescriptor campoALeer;
		int declaredFieldDataType;
		FieldDeserializer fieldDataAccesObject;
		String fieldId;
		JsonNode rawValue;
		Object value;
		while (catalogFieldIterator.hasNext()) {
			campoALeer = catalogFieldIterator.next();
			fieldId = campoALeer.getFieldId();
			rawValue = root.get(fieldId);
			if (rawValue != null && !rawValue.isNull()) {
				if (!rawValue.isNull() && !(rawValue.isTextual() && "null".equals(rawValue.asText()))) {

					declaredFieldDataType = campoALeer.getDataType();
					fieldDataAccesObject = fieldDataStore.get(declaredFieldDataType);
					if (fieldDataAccesObject != null) {
						value = fieldDataAccesObject.deserializeValue(rawValue, campoALeer);
						regreso.setPropertyValue(value, fieldId);
					}
				}
			}
		}
		rawValue = root.get(this.ancestorIdField);
		if (descriptor.getParent() != null && rawValue != null && !rawValue.isNull()) {
			declaredFieldDataType = CatalogEntry.STRING_DATA_TYPE;
			fieldDataAccesObject = fieldDataStore.get(declaredFieldDataType);
			if (fieldDataAccesObject == null) {
				// field data type not supported
			} else {
				if (!(rawValue.isTextual() && "null".equals(rawValue.asText()))) {
					value = fieldDataAccesObject.deserializeValue(rawValue, null);
					regreso.setPropertyValue(value, ancestorIdField);
				}
			}
		}
		return regreso;
	}

}
