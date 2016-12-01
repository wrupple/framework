package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.reserved.Versioned;
import com.wrupple.muba.catalogs.domain.MathProblem;

public class CompositeMathProblem extends MathProblem implements Versioned {
	private static final long serialVersionUID = 1L;
	private Long version;


	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}
