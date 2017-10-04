package com.wrupple.muba.event.domain.reserved;

public interface HasResult<R> {
	public <T extends R> T getConvertedResult();

	public R getResult();
	public void setResult(R r);
}
