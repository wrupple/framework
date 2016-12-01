package com.wrupple.muba.desktop.client.activity.widgets.fields.column;

import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;
import com.wrupple.muba.desktop.client.services.logic.FieldConversionStrategy;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.vegetate.domain.FilterCriteria;

public class FieldColumn extends Column<JsCatalogEntry, Object> {
	protected String id;
	private FieldConversionStrategy cs;
	private List<FilterCriteria> includeCriteria;

	public FieldColumn(String fieldid, Cell<Object> cell,FieldConversionStrategy cs,List<FilterCriteria> ignoreCriteria) {
		super(cell);
		this.id = fieldid;
		this.cs=cs;
		this.includeCriteria=ignoreCriteria;
	}

	@Override
	public Object getValue(JsCatalogEntry object) {
		Object regreso = cs.convertToUserReadableValue(id, object,includeCriteria);
		return regreso;
	}

	public String getFieldId() {
		return id;
	}
}