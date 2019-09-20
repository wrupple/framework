package com.wrupple.muba.catalogs.server.domain;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class ValidationExpression {
	public final Class<? extends Annotation> clazz;
	private final String name,parameterDeclaration,returnStatement;
	private List<String> givenVariable;

	public ValidationExpression(Class<? extends Annotation>  name, String parameterDeclaration, String returnStatement) {
        this(name,parameterDeclaration,returnStatement,"value");
	}
	public ValidationExpression(Class<? extends Annotation>  name, String parameterDeclaration, String returnStatement,String... givenVariable) {
		super();
		this.clazz=name;
		this.name = name.getSimpleName();
		this.parameterDeclaration = parameterDeclaration;
		this.returnStatement = returnStatement;
		this.givenVariable = Arrays.asList(givenVariable);
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

	public List<String> getGivenVariable() {
		return givenVariable;
	}
}
