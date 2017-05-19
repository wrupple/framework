package com.wrupple.muba.desktop.domain;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldConstraint;

public class FieldConstraintImpl implements FieldConstraint, CatalogEntry, Serializable {

	private static final long serialVersionUID = 6481551268294013856L;

	@Override
	public String getCatalog() {
		return CATALOG_ID;
	}

	private Long id;// numericId
	private String name, constraint;
	private Long domain;
	private List<String> properties;

	@Inject
	public FieldConstraintImpl() {
		super();
	}

	public FieldConstraintImpl(String constraint, List<String> properties) {
		super();
		this.constraint = constraint;
		this.properties = properties;
	}

	public FieldConstraintImpl(String constraint) {
		super();
		this.constraint = constraint;
	}

	@Override
	public void setIdAsString(String id) {
		setId(Long.parseLong(id));
	}

	@Override
	public String getIdAsString() {
		return String.valueOf(getId());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Long getDomain() {
		return domain;
	}

	public void setDomain(Long domain) {
		this.domain = domain;
	}

	@Override
	@JsonIgnore
	public String getImage() {
		return null;
	}

	@Override
	public boolean isAnonymouslyVisible() {
		return false;
	}

	@Override
	public void setAnonymouslyVisible(boolean p) {

	}

	@Override
	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

}
