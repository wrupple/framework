package com.wrupple.vegetate.server.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.wrupple.vegetate.domain.FilterCriteria;
import com.wrupple.vegetate.domain.FilterData;


public class FilterCriteriaImpl implements Serializable, FilterCriteria {
	private static final long serialVersionUID = -369384271702851519L;
	private List<Object> values;
	private String operator;
	private List<String> path;

	public FilterCriteriaImpl() {
		super();
		values= new ArrayList<Object>(1);
		path = new ArrayList<String>(1);
	}
	

	public FilterCriteriaImpl(String field, String value) {
		this();
		operator= FilterData.EQUALS;
		values.add(value);
		path.add(field);
	}

	public void setValue(Object value) {
		values.clear();
		values.add(value);
	}

	public Object getValue() {
		return values.size()>0? values.get(0) : null;
	}


	
	public FilterCriteriaImpl(String field,String operator,Object... values) {
		super();
		path = Collections.singletonList(field);
		this.values = Arrays.asList(values);
		this.operator = operator;
	}



	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return new StringBuilder(250).append('[').append(path).append(operator).append(values==null?null:values.size()>10?values.size()+" items":values).append(']').toString() ;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilterCriteriaImpl other = (FilterCriteriaImpl) obj;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public void addValue(String value) {
		values.add(value);
	}


	@Override
	public void removeValue(String valueToRemove) {
		values.remove(valueToRemove);
	}


	@Override
	public int getPathTokenCount() {
		return getPath().size();
	}


	@Override
	public String getPath(int tokenIndex) {
		return getPath().get(tokenIndex);
	}


	@Override
	public void pushToPath(String field) {
		getPath().add(field);
	}


	public List<String> getPath() {
		return path;
	}


	public void setPath(List<String> path) {
		if(this.path==null){
			path = new ArrayList<String>();
		}
		this.path = path;
	}


	public List<String> stringify(Collection<Object> matchingFieldValues) {
		List<String> regreso = new ArrayList<String>(matchingFieldValues.size());
		String value;
		Collection<Object> collectifiedObject;
		for (Object o : matchingFieldValues) {
			if (o != null) {
				if (o instanceof Collection) {
					collectifiedObject = (Collection<Object>) o;
					for (Object c : collectifiedObject) {
						value = String.valueOf(c);
						regreso.add(value);
					}
				} else {
					value = String.valueOf(o);
					regreso.add(value);
				}
			}
		}

		return regreso;
	}


	

}