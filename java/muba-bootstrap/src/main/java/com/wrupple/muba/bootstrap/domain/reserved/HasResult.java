package com.wrupple.muba.bootstrap.domain.reserved;

public interface HasResult {
	public <T> T getConvertedResult();

	public Object getResult();
	public void setResult(Object r);
}
