package com.wrupple.vegetate.server.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wrupple.vegetate.domain.CatalogDescriptor;

public interface ObjectMapper {

	void writeValue(OutputStreamWriter writer, Object object) throws IOException;

	void writeValue(PrintWriter out, Object o) throws IOException;

	String writeValueAsString(Object value) throws IOException;

	<T> T readValue(String string, Class<T> clazz) throws IOException;
	
	<T> T readValue(InputStream inputStream, Class<T> clzz) throws IOException;
	
	<T> List<T> readList(InputStream inputStream, Class<T> clazz) throws IOException;

	<T> Map<String, T> readMap(InputStream inputStream, Class<T> mapValueClass) throws IOException;

	Map<String, Object> readMap(String serializedPayload,CatalogDescriptor catalog) throws IOException;

	Map<String, Object> readMap(InputStream inputStream,CatalogDescriptor catalog)throws IOException;
	
	String getCharacterEncoding();
	
	String getMimeType();
	
	void writeInvocationStart(String function, PrintWriter writer);

	void writeInvocationEnd(String function, PrintWriter writer);

	void writeContentStart(PrintWriter writer);

	void writeContentEnd(PrintWriter writer);

	void writePropertyOpen(PrintWriter writer, String key);
	
	void writePropertyClose(PrintWriter writer, String key, boolean hasNext);

	Date parseDate(String text);

	String formatDate(Date timestamp);

}
