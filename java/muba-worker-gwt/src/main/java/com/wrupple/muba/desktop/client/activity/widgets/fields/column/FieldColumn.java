package com.wrupple.muba.desktop.client.activity.widgets.fields.column;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.worker.shared.services.FieldConversionStrategy;
import com.wrupple.vegetate.domain.FilterCriteria;

import java.util.List;

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
		Object regreso = cs.convertToPresentableValue(id, object,includeCriteria);
		return regreso;
	}

	public String getFieldId() {
		return id;
	}
}