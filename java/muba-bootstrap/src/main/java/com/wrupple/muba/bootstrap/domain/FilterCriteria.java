package com.wrupple.muba.bootstrap.domain;

import java.util.List;

public interface FilterCriteria {

	public List<Object> getValues();

	public void setValues(List<Object> values);
	
	
	public String getOperator();

	public void setOperator(String operator);
	
	public int getPathTokenCount();

	public String getPath(int tokenIndex) ;
	
	public void pushToPath(String field);

	public void setValue(Object value) ;

	public Object getValue() ;

	public void addValue(String value);

	public void removeValue(String valueToRemove);

	List<String> getPath();

	public boolean getEvaluate();

}
