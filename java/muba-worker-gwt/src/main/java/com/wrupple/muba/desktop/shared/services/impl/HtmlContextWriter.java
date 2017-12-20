package com.wrupple.muba.desktop.shared.services.impl;

import com.wrupple.muba.bpm.domain.BPMPeer;
import com.wrupple.muba.catalogs.server.service.SharedContextWriter;
import com.wrupple.vegetate.server.services.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class HtmlContextWriter implements SharedContextWriter {
	/**
	 * 
	 */
	private final ObjectMapper mapper;
	private final PrintWriter writer;
	private final List<String> globalExpressions;
	private final long domain;
	private final BPMPeer peer;

	public HtmlContextWriter(BPMPeer peer, ObjectMapper mapper, PrintWriter writer, List<String> globalExpressions, long domain) {
		super();
		this.mapper = mapper;
		this.writer = writer;
		this.globalExpressions = globalExpressions;
		this.domain = domain;
		this.peer = peer;
	}

	@Override
	public void writeUnregisteredVariable(String name, Object value) throws IOException {
		String serializedVariable = mapper.writeValueAsString(value);
		writeFactoryFunction(writer, name, serializedVariable, false, null);
	}

	@Override
	public void registerVariable(String name, Object value, String registryFunction) throws IOException {
		String serializedVariable = mapper.writeValueAsString(value);
		String factoryFunction = writeFactoryFunction(writer, name, serializedVariable, true, null);
		globalExpressions.add("registerFactory:" + factoryFunction);
		if (registryFunction != null) {
			globalExpressions.add(registryFunction + "(" + factoryFunction + "())");
		}
	}

	@Override
	public void addContextExpression(String expr) {
		globalExpressions.add(expr);
	}

	@Override
	public void addContextFunction(String name, String returnLine, String paramDeclaration) {
		String factoryFunction = writeFactoryFunction(writer, name, returnLine, false, paramDeclaration);
		globalExpressions.add("registerFactory:" + factoryFunction);
	}

	private String writeFactoryFunction(PrintWriter writer, String name, String returnLine, boolean prefix, String functionParamDeclaration) {
		writer.print("function ");
		String factoryName = getFactoryName(name, prefix);
		writer.print(factoryName);
		writer.print('(');
		if (functionParamDeclaration != null) {
			writer.print(functionParamDeclaration);
		}
		writer.print("){");
		writer.print("return ");
		writer.print(returnLine);
		writer.print(";");
		writer.print("};");
		return factoryName;
	}

	private String getFactoryName(String variable, boolean prefix) {
		if (prefix) {
			return "get_" + variable;
		} else {
			return variable;
		}

	}

	@Override
	public long getDomain() {
		return domain;
	}

	public BPMPeer getPeer() {
		return peer;
	}

}