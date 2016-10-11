package com.wrupple.muba.catalogs.server.domain;

import java.util.ArrayList;
import java.util.List;

import com.wrupple.muba.catalogs.domain.CatalogActionResult;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;

public class CatalogActionResultImpl implements CatalogActionResult {

	private List<CatalogColumnResultSet> response;
	private List<String> warnings;
	private long responseTimestamp;

	public CatalogActionResultImpl(long rResponseTimeStamp, List<CatalogColumnResultSet> response,List<String> warnings) {
		super();
		this.response = response;
		if(warnings==null){
			warnings=new ArrayList<String>(1);
		}
		this.warnings = warnings;
		this.responseTimestamp = rResponseTimeStamp;
	}

	public CatalogActionResultImpl() {
		super();
	}


	@Override
	public List<CatalogColumnResultSet> getResponse() {
		return response;
	}

	@Override
	public List<String> getWarnings() {
		return warnings;
	}

	@Override
	public Long getResponseTimestamp() {
		return responseTimestamp;
	}

	public void setResponse(List<CatalogColumnResultSet> response) {
		this.response = response;
	}


	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}

	public void setResponseTimestamp(long responseTimestamp) {
		this.responseTimestamp = responseTimestamp;
	}

}