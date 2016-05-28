package com.wrupple.vegetate.server.domain;

import java.lang.annotation.Annotation;

public class ValidationExpression {
	public final Class<? extends Annotation> clazz;
	private final String name,parameterDeclaration,returnStatement;

	public ValidationExpression(Class<? extends Annotation>  name, String parameterDeclaration, String returnStatement) {
		super();
		this.clazz=name;
		this.name = name.getSimpleName();
		this.parameterDeclaration = parameterDeclaration;
		this.returnStatement = returnStatement;
	}

	public String getName() {
		return name;
	}

	public String getParameterDeclaration() {
		return parameterDeclaration;
	}

	public String getReturnStatement() {
		return returnStatement;
	}
	
	
}
